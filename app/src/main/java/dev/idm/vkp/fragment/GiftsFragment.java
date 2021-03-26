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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Constants;
import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.adapter.GiftAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.Gift;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.GiftsPresenter;
import dev.idm.vkp.mvp.view.IGiftsView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class GiftsFragment extends BaseMvpFragment<GiftsPresenter, IGiftsView>
        implements IGiftsView, SwipeRefreshLayout.OnRefreshListener, GiftAdapter.ClickListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GiftAdapter mAdapter;
    private TextView mEmpty;

    public static GiftsFragment newInstance(int accountId, int ownerId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.OWNER_ID, ownerId);
        GiftsFragment fragment = new GiftsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gifts, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));
        RecyclerView recyclerView = root.findViewById(android.R.id.list);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        mEmpty = root.findViewById(R.id.empty);

        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        mAdapter = new GiftAdapter(Collections.emptyList(), requireActivity());
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        resolveEmptyTextVisibility();
        return root;
    }

    @Override
    public void onRefresh() {
        getPresenter().fireRefresh();
    }

    @Override
    public void displayData(List<Gift> gifts) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(gifts);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
            resolveEmptyTextVisibility();
        }
    }

    private void resolveEmptyTextVisibility() {
        if (nonNull(mEmpty) && nonNull(mAdapter)) {
            mEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(refreshing));
        }
    }

    @Override
    public void onOpenWall(int accountId, int ownerId) {
        PlaceFactory.getOwnerWallPlace(accountId, ownerId, null).tryOpenWith(requireActivity());
    }

    @NotNull
    @Override
    public IPresenterFactory<GiftsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new GiftsPresenter(getArguments().getInt(Extra.ACCOUNT_ID), getArguments().getInt(Extra.OWNER_ID), saveInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.gifts);
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
    public void onOpenClick(int index, Gift gift) {
        getPresenter().fireOpenWall(gift.getFromId());
    }
}
