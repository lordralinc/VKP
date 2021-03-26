package dev.idm.vkp.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dev.idm.vkp.Extra;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.CommunityMembersPresenter;
import dev.idm.vkp.mvp.view.ICommunityMembersView;

public class CommunityControlMembersFragment extends BaseMvpFragment<CommunityMembersPresenter, ICommunityMembersView>
        implements ICommunityMembersView {

    public static CommunityControlMembersFragment newInstance(int accountId, int groupId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.GROUP_ID, groupId);
        CommunityControlMembersFragment fragment = new CommunityControlMembersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NotNull
    @Override
    public IPresenterFactory<CommunityMembersPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunityMembersPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.GROUP_ID),
                saveInstanceState
        );
    }
}