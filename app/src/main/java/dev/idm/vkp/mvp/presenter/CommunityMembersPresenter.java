package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.Nullable;

import dev.idm.vkp.mvp.presenter.base.AccountDependencyPresenter;
import dev.idm.vkp.mvp.view.ICommunityMembersView;


public class CommunityMembersPresenter extends AccountDependencyPresenter<ICommunityMembersView> {

    public CommunityMembersPresenter(int accountId, int groupId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
    }
}
