package dev.idm.vkp.fragment;

import android.content.Intent;
import android.net.Uri;
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
import dev.idm.vkp.adapter.CommunityInfoLinksAdapter;
import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.model.Community;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.CommunityInfoLinksPresenter;
import dev.idm.vkp.mvp.view.ICommunityInfoLinksView;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class CommunityInfoLinksFragment extends BaseMvpFragment<CommunityInfoLinksPresenter, ICommunityInfoLinksView>
        implements ICommunityInfoLinksView, CommunityInfoLinksAdapter.ActionListener {

    private CommunityInfoLinksAdapter mLinksAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static CommunityInfoLinksFragment newInstance(int accountId, Community groupId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.GROUP_ID, groupId);
        CommunityInfoLinksFragment fragment = new CommunityInfoLinksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community_links, container, false);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mLinksAdapter = new CommunityInfoLinksAdapter(Collections.emptyList());
        mLinksAdapter.setActionListener(this);

        recyclerView.setAdapter(mLinksAdapter);

        root.findViewById(R.id.button_add).setVisibility(View.INVISIBLE);
        return root;
    }

    @NotNull
    @Override
    public IPresenterFactory<CommunityInfoLinksPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunityInfoLinksPresenter(
                requireArguments().getInt(Extra.ACCOUNT_ID),
                requireArguments().getParcelable(Extra.GROUP_ID),
                saveInstanceState
        );
    }

    @Override
    public void displayRefreshing(boolean loadingNow) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(loadingNow);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mLinksAdapter)) {
            mLinksAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayData(List<VKApiCommunity.Link> links) {
        if (nonNull(mLinksAdapter)) {
            mLinksAdapter.setData(links);
        }
    }

    @Override
    public void openLink(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

    @Override
    public void onClick(VKApiCommunity.Link link) {
        getPresenter().fireLinkClick(link);
    }
}
