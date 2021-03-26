package dev.idm.vkp.fragment.fave;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Constants;
import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.MarketAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.Market;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.FaveProductsPresenter;
import dev.idm.vkp.mvp.view.IFaveProductsView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class FaveProductsFragment extends BaseMvpFragment<FaveProductsPresenter, IFaveProductsView>
        implements IFaveProductsView, SwipeRefreshLayout.OnRefreshListener, MarketAdapter.ClickListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MarketAdapter mAdapter;
    private TextView mEmpty;

    public static FaveProductsFragment newInstance(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        FaveProductsFragment fragment = new FaveProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fave_products, container, false);
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

        mAdapter = new MarketAdapter(Collections.emptyList(), requireActivity());
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
    public void displayData(List<Market> products) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(products);
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
    public void onMarketOpen(int accountId, @NonNull @NotNull Market market) {
        PlaceFactory.getMarketViewPlace(accountId, market).tryOpenWith(requireActivity());
    }

    @NotNull
    @Override
    public IPresenterFactory<FaveProductsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new FaveProductsPresenter(getArguments().getInt(Extra.ACCOUNT_ID), saveInstanceState);
    }

    @Override
    public void onOpenClick(int index, Market market) {
        getPresenter().fireMarketOpen(market);
    }
}
