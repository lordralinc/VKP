package dev.idm.vkp.fragment.friends;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Constants;
import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.FriendsRecycleAdapter;
import dev.idm.vkp.adapter.OwnersAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.User;
import dev.idm.vkp.model.UsersPart;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.AllFriendsPresenter;
import dev.idm.vkp.mvp.view.IAllFriendsView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.Utils;
import dev.idm.vkp.util.ViewUtils;
import dev.idm.vkp.view.MySearchView;

import static dev.idm.vkp.util.Objects.nonNull;

public class AllFriendsFragment extends BaseMvpFragment<AllFriendsPresenter, IAllFriendsView>
        implements FriendsRecycleAdapter.Listener, IAllFriendsView {

    private FriendsRecycleAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static AllFriendsFragment newInstance(int accountId, int userId) {
        Bundle args = new Bundle();
        args.putInt(Extra.USER_ID, userId);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        AllFriendsFragment allFriendsFragment = new AllFriendsFragment();
        allFriendsFragment.setArguments(args);
        return allFriendsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        RecyclerView mRecyclerView = root.findViewById(R.id.list);
        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        LinearLayoutManager manager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        MySearchView mySearchView = root.findViewById(R.id.searchview);
        mySearchView.setRightButtonVisibility(false);
        mySearchView.setLeftIcon(R.drawable.magnify);
        mySearchView.setOnQueryTextListener(new MySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getPresenter().fireSearchRequestChanged(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getPresenter().fireSearchRequestChanged(newText);
                return false;
            }
        });

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mySearchView.getWindowToken(), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            root.findViewById(R.id.appbar).setElevation(0);
        }

        mAdapter = new FriendsRecycleAdapter(Collections.emptyList(), requireActivity());
        mAdapter.setListener(this);
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    @NotNull
    @Override
    public IPresenterFactory<AllFriendsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new AllFriendsPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.USER_ID), saveInstanceState
        );
    }

    @Override
    public void notifyDatasetChanged(boolean enabled) {
        if (nonNull(mAdapter)) {
            mAdapter.setGroup(enabled);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setSwipeRefreshEnabled(boolean enabled) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setEnabled(enabled);
        }
    }

    @Override
    public void displayData(List<UsersPart> data, boolean grouping) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(data, grouping);
        }
    }

    @Override
    public void notifyItemRangeInserted(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void showUserWall(int accountId, User user) {
        PlaceFactory.getOwnerWallPlace(accountId, user).tryOpenWith(requireActivity());
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public void showNotFriends(List<Owner> data, int accountId) {
        OwnersAdapter adapter = new OwnersAdapter(requireActivity(), data);
        adapter.setClickListener(owner -> PlaceFactory.getOwnerWallPlace(accountId, owner.getOwnerId(), null).tryOpenWith(requireContext()));
        MaterialAlertDialogBuilder dlg = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(requireActivity().getString(R.string.not_friend))
                .setView(Utils.createAlertRecycleFrame(requireActivity(), adapter, null))
                .setPositiveButton("OK", null)
                .setCancelable(true);
        dlg.show();
    }

    @Override
    public void showAddFriends(List<Owner> add, List<Owner> remove, int accountId) {
        if (add.size() <= 0 && remove.size() > 0) {
            showNotFriends(remove, accountId);
            return;
        }
        OwnersAdapter adapter = new OwnersAdapter(requireActivity(), add);
        adapter.setClickListener(owner -> PlaceFactory.getOwnerWallPlace(accountId, owner.getOwnerId(), null).tryOpenWith(requireContext()));
        MaterialAlertDialogBuilder dlg = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(requireActivity().getString(R.string.new_friend))
                .setView(Utils.createAlertRecycleFrame(requireActivity(), adapter, null))
                .setPositiveButton("OK", (dialog, which) -> {
                    if (remove.size() > 0) {
                        showNotFriends(remove, accountId);
                    }
                })
                .setCancelable(remove.size() <= 0);
        dlg.show();
    }

    @Override
    public void onUserClick(User user) {
        getPresenter().fireUserClick(user);
    }
}
