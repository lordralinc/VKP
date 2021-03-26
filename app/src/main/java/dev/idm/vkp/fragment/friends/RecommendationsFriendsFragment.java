package dev.idm.vkp.fragment.friends;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dev.idm.vkp.Extra;
import dev.idm.vkp.fragment.AbsOwnersListFragment;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.RecommendationsFriendsPresenter;
import dev.idm.vkp.mvp.view.ISimpleOwnersView;

public class RecommendationsFriendsFragment extends AbsOwnersListFragment<RecommendationsFriendsPresenter, ISimpleOwnersView> {

    public static RecommendationsFriendsFragment newInstance(int accountId, int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Extra.USER_ID, userId);
        bundle.putInt(Extra.ACCOUNT_ID, accountId);
        RecommendationsFriendsFragment friendsFragment = new RecommendationsFriendsFragment();
        friendsFragment.setArguments(bundle);
        return friendsFragment;
    }

    @NotNull
    @Override
    public IPresenterFactory<RecommendationsFriendsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new RecommendationsFriendsPresenter(
                getArguments().getInt(Extra.USER_ID),
                saveInstanceState);
    }
}
