package dev.idm.vkp.fragment.friends;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dev.idm.vkp.Extra;
import dev.idm.vkp.fragment.AbsOwnersListFragment;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.MutualFriendsPresenter;
import dev.idm.vkp.mvp.view.ISimpleOwnersView;

public class MutualFriendsFragment extends AbsOwnersListFragment<MutualFriendsPresenter, ISimpleOwnersView> {

    private static final String EXTRA_TARGET_ID = "targetId";

    public static MutualFriendsFragment newInstance(int accountId, int targetId) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TARGET_ID, targetId);
        bundle.putInt(Extra.ACCOUNT_ID, accountId);
        MutualFriendsFragment friendsFragment = new MutualFriendsFragment();
        friendsFragment.setArguments(bundle);
        return friendsFragment;
    }

    @NotNull
    @Override
    public IPresenterFactory<MutualFriendsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new MutualFriendsPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(EXTRA_TARGET_ID),
                saveInstanceState
        );
    }
}
