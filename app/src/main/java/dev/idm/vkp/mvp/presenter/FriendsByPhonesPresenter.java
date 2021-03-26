package dev.idm.vkp.mvp.presenter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.domain.IRelationshipInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.presenter.base.AccountDependencyPresenter;
import dev.idm.vkp.mvp.reflect.OnGuiCreated;
import dev.idm.vkp.mvp.view.IFriendsByPhonesView;
import dev.idm.vkp.util.RxUtils;


public class FriendsByPhonesPresenter extends AccountDependencyPresenter<IFriendsByPhonesView> {
    private final List<Owner> data;
    private final IRelationshipInteractor friendsInteractor;
    private final Context context;
    private boolean netLoadingNow;

    public FriendsByPhonesPresenter(int accountId, Context context, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        friendsInteractor = InteractorFactory.createRelationshipInteractor();
        data = new ArrayList<>();
        this.context = context;

        requestActualData();
    }

    @OnGuiCreated
    private void resolveRefreshingView() {
        if (isGuiReady()) {
            getView().displayLoading(netLoadingNow);
        }
    }

    private void requestActualData() {
        netLoadingNow = true;

        resolveRefreshingView();

        int accountId = getAccountId();
        appendDisposable(friendsInteractor.getByPhones(accountId, context)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onActualDataReceived, this::onActualDataGetError));
    }

    private void onActualDataGetError(Throwable t) {
        netLoadingNow = false;
        resolveRefreshingView();

        showError(getView(), t);
    }

    private void onActualDataReceived(List<User> owners) {
        netLoadingNow = false;
        resolveRefreshingView();

        data.clear();
        data.addAll(owners);
        callView(IFriendsByPhonesView::notifyDataSetChanged);
    }

    @Override
    public void onGuiCreated(@NonNull IFriendsByPhonesView view) {
        super.onGuiCreated(view);
        view.displayData(data);
    }

    public void fireRefresh() {
        requestActualData();
    }

    public void onUserOwnerClicked(Owner owner) {
        getView().showOwnerWall(getAccountId(), owner);
    }
}
