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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Constants;
import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.adapter.OwnerArticlesAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.link.LinkHelper;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.listener.OnSectionResumeCallback;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.Article;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.OwnerArticlesPresenter;
import dev.idm.vkp.mvp.view.IOwnerArticlesView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class OwnerArticlesFragment extends BaseMvpFragment<OwnerArticlesPresenter, IOwnerArticlesView>
        implements IOwnerArticlesView, SwipeRefreshLayout.OnRefreshListener, OwnerArticlesAdapter.ClickListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OwnerArticlesAdapter mAdapter;
    private TextView mEmpty;

    public static OwnerArticlesFragment newInstance(int accountId, int ownerId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.OWNER_ID, ownerId);
        OwnerArticlesFragment fragment = new OwnerArticlesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_owner_articles, container, false);
        RecyclerView recyclerView = root.findViewById(android.R.id.list);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        mEmpty = root.findViewById(R.id.empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
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

        mAdapter = new OwnerArticlesAdapter(Collections.emptyList(), requireActivity());
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
    public void displayData(List<Article> articles) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(articles);
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
    public void goToArticle(int accountId, String url) {
        LinkHelper.openLinkInBrowser(requireActivity(), url);
    }

    @Override
    public void goToPhoto(int accountId, Photo photo) {
        ArrayList<Photo> temp = new ArrayList<>(Collections.singletonList(photo));
        PlaceFactory.getSimpleGalleryPlace(accountId, temp, 0, false).tryOpenWith(requireActivity());
    }

    @NotNull
    @Override
    public IPresenterFactory<OwnerArticlesPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new OwnerArticlesPresenter(getArguments().getInt(Extra.ACCOUNT_ID), getArguments().getInt(Extra.OWNER_ID), saveInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (nonNull(actionBar)) {
            actionBar.setTitle(R.string.articles);
            actionBar.setSubtitle(null);
        }

        if (requireActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) requireActivity()).onClearSelection();
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    public void onUrlClick(String url) {
        getPresenter().fireArticleClick(url);
    }

    @Override
    public void onPhotosOpen(Photo photo) {
        getPresenter().firePhotoClick(photo);
    }

    @Override
    public void onDelete(int index, Article article) {
        getPresenter().fireArticleDelete(index, article);
    }

    @Override
    public void onAdd(int index, Article article) {
        getPresenter().fireArticleAdd(index, article);
    }
}
