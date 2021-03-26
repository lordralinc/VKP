package dev.idm.vkp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.adapter.TopicsAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.LoadMoreState;
import dev.idm.vkp.model.Topic;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.TopicsPresenter;
import dev.idm.vkp.mvp.view.ITopicsView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.ViewUtils;
import dev.idm.vkp.view.LoadMoreFooterHelper;

import static dev.idm.vkp.util.Objects.nonNull;

public class TopicsFragment extends BaseMvpFragment<TopicsPresenter, ITopicsView>
        implements SwipeRefreshLayout.OnRefreshListener, ITopicsView, TopicsAdapter.ActionListener {

    private TopicsAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreFooterHelper helper;
    private FloatingActionButton fabCreate;

    public static Bundle buildArgs(int accountId, int ownerId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.OWNER_ID, ownerId);
        return args;
    }

    public static TopicsFragment newInstance(Bundle args) {
        TopicsFragment fragment = new TopicsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TopicsFragment newInstance(int accountId, int ownerId) {
        return newInstance(buildArgs(accountId, ownerId));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_topics, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(requireActivity());

        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mAdapter = new TopicsAdapter(requireActivity(), Collections.emptyList(), this);

        View footer = inflater.inflate(R.layout.footer_load_more, recyclerView, false);
        helper = LoadMoreFooterHelper.createFrom(footer, () -> getPresenter().fireLoadMoreClick());
        mAdapter.addFooter(footer);

        recyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        fabCreate = root.findViewById(R.id.fragment_topics_create);
        fabCreate.setOnClickListener(view -> getPresenter().fireButtonCreateClick());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.topics);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    public void onRefresh() {
        getPresenter().fireRefresh();
    }

    @Override
    public void displayData(@NonNull List<Topic> topics) {
        if (nonNull(mAdapter)) {
            mAdapter.setItems(topics);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataAdd(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(refreshing));
        }
    }

    @Override
    public void setupLoadMore(@LoadMoreState int state) {
        if (nonNull(helper)) {
            helper.switchToState(state);
        }
    }

    @Override
    public void goToComments(int accountId, @NonNull Topic topic) {
        PlaceFactory.getCommentsPlace(accountId, Commented.from(topic), null)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void setButtonCreateVisible(boolean visible) {
        if (nonNull(fabCreate)) {
            if (visible && !fabCreate.isShown()) {
                fabCreate.show();
            }

            if (!visible && fabCreate.isShown()) {
                fabCreate.hide();
            }
        }
    }

    @NotNull
    @Override
    public IPresenterFactory<TopicsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = requireArguments().getInt(Extra.ACCOUNT_ID);
            int ownerId = requireArguments().getInt(Extra.OWNER_ID);
            return new TopicsPresenter(accountId, ownerId, saveInstanceState);
        };
    }

    @Override
    public void onTopicClick(@NonNull Topic topic) {
        getPresenter().fireTopicClick(topic);
    }
}