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

import dev.idm.vkp.Constants;
import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.activity.MainActivity;
import dev.idm.vkp.adapter.AnswerVKOfficialAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.listener.OnSectionResumeCallback;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.AnswerVKOfficialList;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.AnswerVKOfficialPresenter;
import dev.idm.vkp.mvp.view.IAnswerVKOfficialView;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class AnswerVKOfficialFragment extends BaseMvpFragment<AnswerVKOfficialPresenter, IAnswerVKOfficialView> implements IAnswerVKOfficialView, AnswerVKOfficialAdapter.ClickListener {

    private TextView mEmpty;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AnswerVKOfficialAdapter mAdapter;

    public static AnswerVKOfficialFragment newInstance(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        AnswerVKOfficialFragment fragment = new AnswerVKOfficialFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feedback, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));
        mEmpty = root.findViewById(R.id.fragment_feedback_empty_text);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(requireActivity());
        RecyclerView recyclerView = root.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        mAdapter = new AnswerVKOfficialAdapter(null, requireActivity());
        mAdapter.setClickListener(this);

        recyclerView.setAdapter(mAdapter);

        resolveEmptyText();
        return root;
    }

    private void resolveEmptyText() {
        if (nonNull(mEmpty) && nonNull(mAdapter)) {
            mEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.get().ui().notifyPlaceResumed(Place.NOTIFICATIONS);

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.drawer_feedback);
            actionBar.setSubtitle(null);
        }

        if (requireActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) requireActivity()).onSectionResume(AdditionalNavigationFragment.SECTION_ITEM_FEEDBACK);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    public void displayData(AnswerVKOfficialList users) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(users);
            resolveEmptyText();
        }
    }

    @Override
    public void notifyUpdateCounter() {
        ((MainActivity) requireActivity()).UpdateNotificationCount(Settings.get().accounts().getCurrent());
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
            resolveEmptyText();
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
            resolveEmptyText();
        }
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public void openOwnerWall(int owner_id) {
        PlaceFactory.getOwnerWallPlace(Settings.get().accounts().getCurrent(), owner_id, null).tryOpenWith(requireActivity());
    }

    @NotNull
    @Override
    public IPresenterFactory<AnswerVKOfficialPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new AnswerVKOfficialPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }
}
