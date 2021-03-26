package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Injection;
import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.exception.NotFoundException;
import dev.idm.vkp.model.Community;
import dev.idm.vkp.mvp.presenter.base.AccountDependencyPresenter;
import dev.idm.vkp.mvp.view.ICommunityInfoLinksView;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.RxUtils;
import io.reactivex.rxjava3.functions.Function;


public class CommunityInfoLinksPresenter extends AccountDependencyPresenter<ICommunityInfoLinksView> {

    private static final String TAG = CommunityInfoLinksPresenter.class.getSimpleName();

    private final Community groupId;
    private final INetworker networker;
    private final List<VKApiCommunity.Link> links;
    private boolean loadingNow;

    public CommunityInfoLinksPresenter(int accountId, Community groupId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);

        networker = Injection.provideNetworkInterfaces();
        this.groupId = groupId;
        links = new ArrayList<>();

        requestLinks();
    }

    private void setLoadingNow(boolean loadingNow) {
        this.loadingNow = loadingNow;
        resolveRefreshingView();
    }

    @Override
    public void onGuiCreated(@NonNull ICommunityInfoLinksView view) {
        super.onGuiCreated(view);
        view.displayData(links);
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshingView();
    }

    private void resolveRefreshingView() {
        if (isGuiResumed()) {
            getView().displayRefreshing(loadingNow);
        }
    }

    private void requestLinks() {
        int accountId = getAccountId();

        setLoadingNow(true);
        appendDisposable(networker.vkDefault(accountId)
                .groups()
                .getById(Collections.singletonList(groupId.getId()), null, null, "links")
                .map((Function<List<VKApiCommunity>, List<VKApiCommunity.Link>>) dtos -> {
                    if (dtos.size() != 1) {
                        throw new NotFoundException();
                    }

                    List<VKApiCommunity.Link> links = dtos.get(0).links;
                    return Objects.nonNull(links) ? links : Collections.emptyList();
                })
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onLinksReceived, this::onRequestError));
    }

    private void onRequestError(Throwable throwable) {
        setLoadingNow(false);
        showError(getView(), throwable);
    }

    private void onLinksReceived(List<VKApiCommunity.Link> links) {
        setLoadingNow(false);

        this.links.clear();
        this.links.addAll(links);

        callView(ICommunityInfoLinksView::notifyDataSetChanged);
    }

    public void fireRefresh() {
        requestLinks();
    }

    public void fireLinkClick(VKApiCommunity.Link link) {
        getView().openLink(link.url);
    }
}
