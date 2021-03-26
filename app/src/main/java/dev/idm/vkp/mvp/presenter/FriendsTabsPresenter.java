package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.idm.vkp.domain.IOwnersRepository;
import dev.idm.vkp.domain.IRelationshipInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.model.FriendsCounters;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.presenter.base.AccountDependencyPresenter;
import dev.idm.vkp.mvp.view.IFriendsTabsView;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.RxUtils;


public class FriendsTabsPresenter extends AccountDependencyPresenter<IFriendsTabsView> {

    private static final String SAVE_COUNTERS = "save_counters";

    private final int userId;
    private final IRelationshipInteractor relationshipInteractor;
    private final IOwnersRepository ownersRepository;
    private FriendsCounters counters;
    private Owner owner;

    public FriendsTabsPresenter(int accountId, int userId, @Nullable FriendsCounters counters, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        this.userId = userId;
        relationshipInteractor = InteractorFactory.createRelationshipInteractor();
        ownersRepository = Repository.INSTANCE.getOwners();

        if (Objects.nonNull(savedInstanceState)) {
            this.counters = savedInstanceState.getParcelable(SAVE_COUNTERS);
        } else {
            this.counters = counters;
        }

        if (this.counters == null) {
            this.counters = new FriendsCounters(0, 0, 0, 0);
            requestCounters();
        }

        if (Objects.isNull(owner) && userId != accountId) {
            requestOwnerInfo();
        }
    }

    private void requestOwnerInfo() {
        int accountId = getAccountId();
        appendDisposable(ownersRepository.getBaseOwnerInfo(accountId, userId, IOwnersRepository.MODE_ANY)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onOwnerInfoReceived, t -> {/*ignore*/}));
    }

    private void onOwnerInfoReceived(Owner owner) {
        this.owner = owner;
        callView(view -> view.displayUserNameAtToolbar(owner.getFullName()));
    }

    private void requestCounters() {
        int accountId = getAccountId();
        appendDisposable(relationshipInteractor.getFriendsCounters(accountId, userId)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onCountersReceived, this::onCountersGetError));
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        getView().setDrawerFriendsSectionSelected(userId == getAccountId());
    }

    private void onCountersGetError(Throwable t) {
        callView(view -> view.displayConters(counters));
        showError(getView(), t);
    }

    private void onCountersReceived(FriendsCounters counters) {
        this.counters = counters;
        callView(view -> view.displayConters(counters));
    }

    @Override
    public void onGuiCreated(@NonNull IFriendsTabsView view) {
        super.onGuiCreated(view);
        view.configTabs(getAccountId(), userId, userId != getAccountId());
        view.displayConters(counters);
    }
}