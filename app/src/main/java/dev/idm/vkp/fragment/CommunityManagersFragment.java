package dev.idm.vkp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.SelectProfilesActivity;
import dev.idm.vkp.adapter.CommunityManagersAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.fragment.search.SearchContentType;
import dev.idm.vkp.fragment.search.criteria.PeopleSearchCriteria;
import dev.idm.vkp.model.Community;
import dev.idm.vkp.model.Manager;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.SelectProfileCriteria;
import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.CommunityManagersPresenter;
import dev.idm.vkp.mvp.view.ICommunityManagersView;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.AssertUtils;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.ViewUtils;

public class CommunityManagersFragment extends BaseMvpFragment<CommunityManagersPresenter, ICommunityManagersView>
        implements ICommunityManagersView {

    private final ActivityResultLauncher<Intent> requestSelectProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ArrayList<Owner> users = result.getData().getParcelableArrayListExtra(Extra.OWNERS);
                    AssertUtils.requireNonNull(users);
                    postPrenseterReceive(presenter -> presenter.fireProfilesSelected(users));
                }
            });
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommunityManagersAdapter mAdapter;

    public static CommunityManagersFragment newInstance(int accountId, Community groupId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.GROUP_ID, groupId);
        CommunityManagersFragment fragment = new CommunityManagersFragment();
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

        mAdapter = new CommunityManagersAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setActionListener(new CommunityManagersAdapter.ActionListener() {
            @Override
            public void onManagerClick(Manager manager) {
                getPresenter().fireManagerClick(manager);
            }

            @Override
            public void onManagerLongClick(Manager manager) {
                showManagerContextMenu(manager);
            }
        });

        recyclerView.setAdapter(mAdapter);
        root.findViewById(R.id.button_add).setOnClickListener(v -> getPresenter().fireButtonAddClick());
        return root;
    }

    private void showManagerContextMenu(Manager manager) {
        String[] items = {getString(R.string.delete)};
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(manager.getUser().getFullName())
                .setItems(items, (dialog, which) -> getPresenter().fireRemoveClick(manager))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @NotNull
    @Override
    public IPresenterFactory<CommunityManagersPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunityManagersPresenter(
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
    public void goToManagerEditing(int accountId, int groupId, Manager manager) {
        PlaceFactory.getCommunityManagerEditPlace(accountId, groupId, manager).tryOpenWith(requireActivity());
    }

    @Override
    public void showUserProfile(int accountId, User user) {
        PlaceFactory.getOwnerWallPlace(accountId, user).tryOpenWith(requireActivity());
    }

    @Override
    public void startSelectProfilesActivity(int accountId, int groupId) {
        PeopleSearchCriteria criteria = new PeopleSearchCriteria("").setGroupId(groupId);

        SelectProfileCriteria c = new SelectProfileCriteria();

        Place place = PlaceFactory.getSingleTabSearchPlace(accountId, SearchContentType.PEOPLE, criteria);
        Intent intent = SelectProfilesActivity.createIntent(requireActivity(), place, c);
        requestSelectProfile.launch(intent);
    }

    @Override
    public void startAddingUsersToManagers(int accountId, int groupId, ArrayList<User> users) {
        PlaceFactory.getCommunityManagerAddPlace(accountId, groupId, users).tryOpenWith(requireActivity());
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
