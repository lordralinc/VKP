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
import dev.idm.vkp.adapter.CommunityBannedAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.fragment.search.SearchContentType;
import dev.idm.vkp.fragment.search.criteria.PeopleSearchCriteria;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.model.Banned;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.SelectProfileCriteria;
import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.CommunityBlacklistPresenter;
import dev.idm.vkp.mvp.view.ICommunityBlacklistView;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.AssertUtils;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.ViewUtils;

public class CommunityBlacklistFragment extends BaseMvpFragment<CommunityBlacklistPresenter, ICommunityBlacklistView>
        implements ICommunityBlacklistView, CommunityBannedAdapter.ActionListener {

    private final ActivityResultLauncher<Intent> requestSelectProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ArrayList<Owner> users = result.getData().getParcelableArrayListExtra(Extra.OWNERS);
                    AssertUtils.requireNonNull(users);
                    postPrenseterReceive(presenter -> presenter.fireAddToBanUsersSelected(users));
                }
            });
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommunityBannedAdapter mAdapter;

    public static CommunityBlacklistFragment newInstance(int accountId, int groupdId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.GROUP_ID, groupdId);
        CommunityBlacklistFragment fragment = new CommunityBlacklistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community_blacklist, container, false);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToBottom();
            }
        });

        mAdapter = new CommunityBannedAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setActionListener(this);

        recyclerView.setAdapter(mAdapter);

        root.findViewById(R.id.button_add).setOnClickListener(v -> getPresenter().fireAddClick());
        return root;
    }

    @NotNull
    @Override
    public IPresenterFactory<CommunityBlacklistPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunityBlacklistPresenter(
                requireArguments().getInt(Extra.ACCOUNT_ID),
                requireArguments().getInt(Extra.GROUP_ID),
                saveInstanceState
        );
    }

    @Override
    public void displayRefreshing(boolean loadingNow) {
        if (Objects.nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(loadingNow);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void diplayData(List<Banned> data) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.setData(data);
        }
    }

    @Override
    public void notifyItemRemoved(int index) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemRemoved(index);
        }
    }

    @Override
    public void openBanEditor(int accountId, int groupId, Banned banned) {
        PlaceFactory.getCommunityBanEditPlace(accountId, groupId, banned).tryOpenWith(requireActivity());
    }

    @Override
    public void startSelectProfilesActivity(int accountId, int groupId) {
        PeopleSearchCriteria criteria = new PeopleSearchCriteria("")
                .setGroupId(groupId);

        SelectProfileCriteria c = new SelectProfileCriteria();

        Place place = PlaceFactory.getSingleTabSearchPlace(accountId, SearchContentType.PEOPLE, criteria);
        Intent intent = SelectProfilesActivity.createIntent(requireActivity(), place, c);
        requestSelectProfile.launch(intent);
    }

    @Override
    public void addUsersToBan(int accountId, int groupId, ArrayList<User> users) {
        PlaceFactory.getCommunityAddBanPlace(accountId, groupId, users).tryOpenWith(requireActivity());
    }

    @Override
    public void notifyItemsAdded(int position, int size) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, size);
        }
    }

    @Override
    public void onBannedClick(Banned banned) {
        getPresenter().fireBannedClick(banned);
    }

    @Override
    public void onBannedLongClick(Banned banned) {
        String[] items = {getString(R.string.delete)};
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(banned.getBanned().getFullName())
                .setItems(items, (dialog, which) -> getPresenter().fireBannedRemoveClick(banned))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }
}
