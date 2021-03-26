package dev.idm.vkp.fragment.friends;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dev.idm.vkp.Extra;
import dev.idm.vkp.fragment.AbsOwnersListFragment;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.FollowersPresenter;
import dev.idm.vkp.mvp.view.ISimpleOwnersView;

public class FollowersFragment extends AbsOwnersListFragment<FollowersPresenter, ISimpleOwnersView> {
    public static FollowersFragment newInstance(int accountId, int userId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.USER_ID, userId);
        FollowersFragment followersFragment = new FollowersFragment();
        followersFragment.setArguments(args);
        return followersFragment;
    }

    @NotNull
    @Override
    public IPresenterFactory<FollowersPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new FollowersPresenter(getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.USER_ID),
                saveInstanceState);
    }
}
