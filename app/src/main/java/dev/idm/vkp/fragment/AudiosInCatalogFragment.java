package dev.idm.vkp.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Constants;
import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.adapter.AudioRecyclerAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.listener.OnSectionResumeCallback;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.Audio;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.AudiosInCatalogPresenter;
import dev.idm.vkp.mvp.view.IAudiosInCatalogView;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.player.util.MusicUtils;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.AppPerms;
import dev.idm.vkp.util.CustomToast;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class AudiosInCatalogFragment extends BaseMvpFragment<AudiosInCatalogPresenter, IAudiosInCatalogView>
        implements IAudiosInCatalogView {

    public static final String EXTRA_IN_TABS_CONTAINER = "in_tabs_container";
    private final AppPerms.doRequestPermissions requestWritePermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> CustomToast.CreateCustomToast(requireActivity()).showToast(R.string.permission_all_granted_text));
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AudioRecyclerAdapter mAudioRecyclerAdapter;
    private String Header;
    private boolean inTabsContainer;

    public static AudiosInCatalogFragment newInstance(int accountId, String block_id, String title) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putString(Extra.ID, block_id);
        args.putString(Extra.TITLE, title);
        AudiosInCatalogFragment fragment = new AudiosInCatalogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inTabsContainer = requireArguments().getBoolean(EXTRA_IN_TABS_CONTAINER);
        Header = requireArguments().getString(Extra.TITLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_catalog_music, container, false);
        Toolbar toolbar = root.findViewById(R.id.toolbar);

        if (!inTabsContainer) {
            toolbar.setVisibility(View.VISIBLE);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        FloatingActionButton Goto = root.findViewById(R.id.goto_button);
        Goto.setImageResource(R.drawable.audio_player);

        Goto.setOnLongClickListener(v -> {
            Audio curr = MusicUtils.getCurrentAudio();
            if (curr != null) {
                PlaceFactory.getPlayerPlace(Settings.get().accounts().getCurrent()).tryOpenWith(requireActivity());
            } else
                CustomToast.CreateCustomToast(requireActivity()).showToastError(R.string.null_audio);
            return false;
        });
        Goto.setOnClickListener(v -> {
            Audio curr = MusicUtils.getCurrentAudio();
            if (curr != null) {
                int index = getPresenter().getAudioPos(curr);
                if (index >= 0) {
                    recyclerView.scrollToPosition(index + mAudioRecyclerAdapter.getHeadersCount());
                } else
                    CustomToast.CreateCustomToast(requireActivity()).showToast(R.string.audio_not_found);
            } else
                CustomToast.CreateCustomToast(requireActivity()).showToastError(R.string.null_audio);
        });
        mAudioRecyclerAdapter = new AudioRecyclerAdapter(requireActivity(), Collections.emptyList(), false, false, 0, null);
        mAudioRecyclerAdapter.setClickListener(new AudioRecyclerAdapter.ClickListener() {
            @Override
            public void onClick(int position, int catalog, Audio audio) {
                getPresenter().playAudio(requireActivity(), position);
            }

            @Override
            public void onEdit(int position, Audio audio) {

            }

            @Override
            public void onDelete(int position) {

            }

            @Override
            public void onUrlPhotoOpen(@NonNull String url, @NonNull String prefix, @NonNull String photo_prefix) {
                PlaceFactory.getSingleURLPhotoPlace(url, prefix, photo_prefix).tryOpenWith(requireActivity());
            }

            @Override
            public void onRequestWritePermissions() {
                requestWritePermission.launch();
            }
        });
        recyclerView.setAdapter(mAudioRecyclerAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!inTabsContainer) {
            Settings.get().ui().notifyPlaceResumed(Place.AUDIOS);
            ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
            if (actionBar != null) {
                actionBar.setTitle(Header);
                actionBar.setSubtitle(R.string.music);
            }

            if (requireActivity() instanceof OnSectionResumeCallback) {
                ((OnSectionResumeCallback) requireActivity()).onSectionResume(AdditionalNavigationFragment.SECTION_ITEM_AUDIOS);
            }

            new ActivityFeatures.Builder()
                    .begin()
                    .setHideNavigationMenu(false)
                    .setBarsColored(requireActivity(), true)
                    .build()
                    .apply(requireActivity());
        }
    }

    @NotNull
    @Override
    public IPresenterFactory<AudiosInCatalogPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new AudiosInCatalogPresenter(
                requireArguments().getInt(Extra.ACCOUNT_ID),
                requireArguments().getString(Extra.ID),
                saveInstanceState
        );
    }

    @Override
    public void displayList(List<Audio> audios) {
        if (nonNull(mAudioRecyclerAdapter)) {
            mAudioRecyclerAdapter.setData(audios);
        }
    }

    @Override
    public void notifyListChanged() {
        if (nonNull(mAudioRecyclerAdapter)) {
            mAudioRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyItemChanged(int index) {
        if (nonNull(mAudioRecyclerAdapter)) {
            mAudioRecyclerAdapter.notifyItemBindableChanged(index);
        }
    }

    @Override
    public void displayRefreshing(boolean refreshing) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }
}
