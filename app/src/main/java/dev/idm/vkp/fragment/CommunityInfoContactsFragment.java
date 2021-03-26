package dev.idm.vkp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.CommunityInfoContactsAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.model.Community;
import dev.idm.vkp.model.Manager;
import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.CommunityInfoContactsPresenter;
import dev.idm.vkp.mvp.view.ICommunityInfoContactsView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.ViewUtils;

public class CommunityInfoContactsFragment extends BaseMvpFragment<CommunityInfoContactsPresenter, ICommunityInfoContactsView>
        implements ICommunityInfoContactsView {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommunityInfoContactsAdapter mAdapter;

    public static CommunityInfoContactsFragment newInstance(int accountId, Community groupId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.GROUP_ID, groupId);
        CommunityInfoContactsFragment fragment = new CommunityInfoContactsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community_managers, container, false);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        root.findViewById(R.id.button_add).setVisibility(View.INVISIBLE);

        mAdapter = new CommunityInfoContactsAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setActionListener(manager -> getPresenter().fireManagerClick(manager));

        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @NotNull
    @Override
    public IPresenterFactory<CommunityInfoContactsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunityInfoContactsPresenter(
                requireArguments().getInt(Extra.ACCOUNT_ID),
                requireArguments().getParcelable(Extra.GROUP_ID),
                saveInstanceState
        );
    }

    @Override
    public void notifyDataSetChanged() {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayRefreshing(boolean loadingNow) {
        if (Objects.nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(loadingNow);
        }
    }

    @Override
    public void displayData(List<Manager> managers) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.setData(managers);
        }
    }

    @Override
    public void showUserProfile(int accountId, User user) {
        PlaceFactory.getOwnerWallPlace(accountId, user).tryOpenWith(requireActivity());
    }

    @Override
    public void notifyItemRemoved(int index) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemRemoved(index);
        }
    }

    @Override
    public void notifyItemChanged(int index) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void notifyItemAdded(int index) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemInserted(index);
        }
    }
}
