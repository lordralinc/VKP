package dev.idm.vkp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.adapter.OwnersAdapter;
import dev.idm.vkp.adapter.VideoAlbumsNewAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.FriendsByPhonesPresenter;
import dev.idm.vkp.mvp.view.IFriendsByPhonesView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.ViewUtils;

public class FriendsByPhonesFragment extends BaseMvpFragment<FriendsByPhonesPresenter, IFriendsByPhonesView>
        implements OwnersAdapter.ClickListener, IFriendsByPhonesView {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OwnersAdapter mAdapter;
    private TextView mEmpty;

    public static FriendsByPhonesFragment newInstance(Bundle args) {
        FriendsByPhonesFragment fragment = new FriendsByPhonesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FriendsByPhonesFragment newInstance(int accountId) {
        return newInstance(buildArgs(accountId));
    }

    public static Bundle buildArgs(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        return args;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends_by_phones, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        mEmpty = root.findViewById(R.id.empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(VideoAlbumsNewAdapter.PICASSO_TAG));

        mAdapter = new OwnersAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        resolveEmptyTextVisibility();
        return root;
    }

    @Override
    public void displayData(@NonNull List<Owner> owners) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.setItems(owners);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void displayLoading(boolean loading) {
        if (Objects.nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(loading));
        }
    }

    private void resolveEmptyTextVisibility() {
        if (Objects.nonNull(mEmpty) && Objects.nonNull(mAdapter)) {
            mEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.friends_by_phone);
            actionBar.setSubtitle(null);
        }
        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @NotNull
    @Override
    public IPresenterFactory<FriendsByPhonesPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = requireArguments().getInt(Extra.ACCOUNT_ID);
            return new FriendsByPhonesPresenter(accountId, requireActivity(), saveInstanceState);
        };
    }

    @Override
    public void onOwnerClick(Owner owner) {
        getPresenter().onUserOwnerClicked(owner);
    }

    @Override
    public void showOwnerWall(int accountId, Owner owner) {
        PlaceFactory.getOwnerWallPlace(accountId, owner).tryOpenWith(requireActivity());
    }
}
