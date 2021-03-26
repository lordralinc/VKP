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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.activity.SelectProfilesActivity;
import dev.idm.vkp.adapter.ChatMembersListAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.fragment.friends.FriendsTabsFragment;
import dev.idm.vkp.model.AppChatUser;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.SelectProfileCriteria;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.ChatMembersPresenter;
import dev.idm.vkp.mvp.view.IChatMembersView;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.AssertUtils;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class ChatUsersFragment extends BaseMvpFragment<ChatMembersPresenter, IChatMembersView>
        implements IChatMembersView, ChatMembersListAdapter.ActionListener {

    private final ActivityResultLauncher<Intent> requestAddUser = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<Owner> users = result.getData().getParcelableArrayListExtra(Extra.OWNERS);
                    AssertUtils.requireNonNull(users);
                    postPrenseterReceive(presenter -> presenter.fireUserSelected(users));
                }
            });
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ChatMembersListAdapter mAdapter;

    public static Bundle buildArgs(int accountId, int chatId) {
        Bundle args = new Bundle();
        args.putInt(Extra.CHAT_ID, chatId);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        return args;
    }

    public static ChatUsersFragment newInstance(Bundle args) {
        ChatUsersFragment fragment = new ChatUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_chat_users, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mAdapter = new ChatMembersListAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setActionListener(this);
        recyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        FloatingActionButton fabAdd = root.findViewById(R.id.fragment_chat_users_add);
        fabAdd.setOnClickListener(v -> getPresenter().fireAddUserClick());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);

        if (actionBar != null) {
            actionBar.setTitle(R.string.chat_users);
            actionBar.setSubtitle(null);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    public void onRemoveClick(AppChatUser user) {
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.confirmation)
                .setMessage(getString(R.string.remove_chat_user_commit, user.getMember().getFullName()))
                .setPositiveButton(R.string.button_ok, (dialog, which) -> getPresenter().fireUserDeteleConfirmed(user))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public void displayData(List<AppChatUser> users) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(users);
        }
    }

    @Override
    public void notifyItemRemoved(int position) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void openUserWall(int accountId, Owner user) {
        PlaceFactory.getOwnerWallPlace(accountId, user).tryOpenWith(requireActivity());
    }

    @Override
    public void displayRefreshing(boolean refreshing) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public void startSelectUsersActivity(int accountId) {
        Place place = PlaceFactory.getFriendsFollowersPlace(accountId, accountId, FriendsTabsFragment.TAB_ALL_FRIENDS, null);
        SelectProfileCriteria criteria = new SelectProfileCriteria().setOwnerType(SelectProfileCriteria.OwnerType.ONLY_FRIENDS);

        Intent intent = SelectProfilesActivity.createIntent(requireActivity(), place, criteria);

        requestAddUser.launch(intent);
    }

    @NotNull
    @Override
    public IPresenterFactory<ChatMembersPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new ChatMembersPresenter(
                requireArguments().getInt(Extra.ACCOUNT_ID),
                requireArguments().getInt(Extra.CHAT_ID),
                saveInstanceState
        );
    }

    @Override
    public void onUserClick(AppChatUser user) {
        getPresenter().fireUserClick(user);
    }
}
