package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.db.Stores;
import dev.idm.vkp.model.LocalVideo;
import dev.idm.vkp.mvp.presenter.base.RxSupportPresenter;
import dev.idm.vkp.mvp.view.ILocalVideosView;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;

import static dev.idm.vkp.util.Utils.isEmpty;


public class LocalVideosPresenter extends RxSupportPresenter<ILocalVideosView> {

    private static final String TAG = LocalVideosPresenter.class.getSimpleName();

    private final List<LocalVideo> mLocalVideos;
    private final List<LocalVideo> mLocalVideos_search;
    private boolean mLoadingNow;
    private String q;

    public LocalVideosPresenter(@Nullable Bundle savedInstanceState) {
        super(savedInstanceState);

        mLocalVideos = new ArrayList<>();
        mLocalVideos_search = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        if (mLoadingNow) return;

        changeLoadingState(true);
        appendDisposable(Stores.getInstance()
                .localMedia()
                .getVideos()
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onDataLoaded, this::onLoadError));
    }

    public void fireSearchRequestChanged(String q) {
        String query = q == null ? null : q.trim();

        if (Objects.safeEquals(q, this.q)) {
            return;
        }
        this.q = query;
        mLocalVideos_search.clear();
        for (LocalVideo i : mLocalVideos) {
            if (isEmpty(i.getTitle())) {
                continue;
            }
            if (i.getTitle().toLowerCase().contains(q.toLowerCase())) {
                mLocalVideos_search.add(i);
            }
        }

        if (!isEmpty(q))
            callView(v -> v.displayData(mLocalVideos_search));
        else
            callView(v -> v.displayData(mLocalVideos));
    }

    private void onLoadError(Throwable throwable) {
        changeLoadingState(false);
    }

    private void onDataLoaded(List<LocalVideo> data) {
        changeLoadingState(false);
        mLocalVideos.addAll(data);
        resolveListData();
        resolveEmptyTextVisibility();
    }

    @Override
    public void onGuiCreated(@NonNull ILocalVideosView viewHost) {
        super.onGuiCreated(viewHost);
        resolveListData();
        resolveProgressView();
        resolveFabVisibility(false);
        resolveEmptyTextVisibility();
    }

    private void resolveEmptyTextVisibility() {
        if (isGuiReady()) getView().setEmptyTextVisible(Utils.safeIsEmpty(mLocalVideos));
    }

    private void resolveListData() {
        if (isGuiReady())
            getView().displayData(mLocalVideos);
    }

    private void changeLoadingState(boolean loading) {
        mLoadingNow = loading;
        resolveProgressView();
    }

    private void resolveProgressView() {
        if (isGuiReady()) {
            getView().displayProgress(mLoadingNow);
        }
    }

    public void fireFabClick() {
        ArrayList<LocalVideo> localVideos = Utils.getSelected(mLocalVideos);
        if (!localVideos.isEmpty()) {
            getView().returnResultToParent(localVideos);
        } else {
            safeShowError(getView(), R.string.select_attachments);
        }
    }


    public void fireVideoClick(@NonNull LocalVideo video) {
        video.setSelected(!video.isSelected());

        if (video.isSelected()) {
            ArrayList<LocalVideo> single = new ArrayList<>(1);
            single.add(video);
            getView().returnResultToParent(single);
        }
    }

    private void resolveFabVisibility(boolean anim) {
        resolveFabVisibility(Utils.countOfSelection(mLocalVideos) > 0, anim);
    }

    private void resolveFabVisibility(boolean visible, boolean anim) {
        if (isGuiReady()) {
            getView().setFabVisible(visible, anim);
        }
    }

    public void fireRefresh() {
        loadData();
    }
}
