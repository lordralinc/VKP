package dev.idm.vkp.fragment.friends;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dev.idm.vkp.Extra;
import dev.idm.vkp.fragment.AbsOwnersListFragment;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.OnlineFriendsPresenter;
import dev.idm.vkp.mvp.view.ISimpleOwnersView;

public class OnlineFriendsFragment extends AbsOwnersListFragment<OnlineFriendsPresenter, ISimpleOwnersView> {

    public static OnlineFriendsFragment newInstance(int accountId, int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Extra.USER_ID, userId);
        bundle.putInt(Extra.ACCOUNT_ID, accountId);
        OnlineFriendsFragment friendsFragment = new OnlineFriendsFragment();
        friendsFragment.setArguments(bundle);
        return friendsFragment;
    }

    @NotNull
    @Override
    public IPresenterFactory<OnlineFriendsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new OnlineFriendsPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.USER_ID),
                saveInstanceState);
    }
}
