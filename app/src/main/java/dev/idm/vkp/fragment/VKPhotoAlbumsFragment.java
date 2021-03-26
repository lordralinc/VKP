package dev.idm.vkp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.CheckDonate;
import dev.idm.vkp.Constants;
import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.adapter.VkPhotoAlbumsAdapter;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.OnSectionResumeCallback;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.ParcelableOwnerWrapper;
import dev.idm.vkp.model.PhotoAlbum;
import dev.idm.vkp.model.PhotoAlbumEditor;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.PhotoAlbumsPresenter;
import dev.idm.vkp.mvp.view.IPhotoAlbumsView;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.ViewUtils;

public class VKPhotoAlbumsFragment extends BaseMvpFragment<PhotoAlbumsPresenter, IPhotoAlbumsView> implements IPhotoAlbumsView,
        VkPhotoAlbumsAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String ACTION_SELECT_ALBUM = "dev.idm.vkp.ACTION_SELECT_ALBUM";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFab;
    private VkPhotoAlbumsAdapter mAdapter;
    private TextView mEmptyText;

    public static VKPhotoAlbumsFragment newInstance(int accountId, int ownerId, String action, ParcelableOwnerWrapper ownerWrapper, boolean hide_toolbar) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.OWNER_ID, ownerId);
        args.putParcelable(Extra.OWNER, ownerWrapper);
        args.putString(Extra.ACTION, action);
        if (hide_toolbar)
            args.putBoolean(BaseMvpFragment.EXTRA_HIDE_TOOLBAR, true);
        VKPhotoAlbumsFragment fragment = new VKPhotoAlbumsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums_gallery, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (!hasHideToolbarExtra()) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        RecyclerView recyclerView = view.findViewById(R.id.list);

        mEmptyText = view.findViewById(R.id.empty);

        int columnCount = getResources().getInteger(R.integer.photos_albums_column_count);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), columnCount));
        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));

        mAdapter = new VkPhotoAlbumsAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setClickListener(this);

        recyclerView.setAdapter(mAdapter);

        mFab = view.findViewById(R.id.fab);
        mFab.setOnClickListener(v -> getPresenter().fireCreateAlbumClick());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.get().ui().notifyPlaceResumed(Place.VK_PHOTO_ALBUMS);

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (Objects.nonNull(actionBar)) {
            actionBar.setTitle(R.string.photos);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    public void showDeleteConfirmDialog(@NonNull PhotoAlbum album) {
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.remove_confirm)
                .setMessage(R.string.album_remove_confirm_message)
                .setPositiveButton(R.string.button_yes, (dialog, which) -> getPresenter().fireAlbumDeletingConfirmed(album))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public void onRefresh() {
        getPresenter().fireRefresh();
    }

    @Override
    public void displayData(@NonNull List<PhotoAlbum> data) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.setData(data);
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
        if (Objects.nonNull(mEmptyText) && Objects.nonNull(mAdapter)) {
            mEmptyText.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
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
    public void setToolbarSubtitle(String subtitle) {
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (Objects.nonNull(actionBar)) {
            actionBar.setTitle(R.string.photos);
        }
    }

    @Override
    public void openAlbum(int accountId, @NonNull PhotoAlbum album, @Nullable Owner owner, @Nullable String action) {
        PlaceFactory.getVKPhotosAlbumPlace(accountId, album.getOwnerId(), album.getId(), action)
                .withParcelableExtra(Extra.ALBUM, album)
                .withParcelableExtra(Extra.OWNER, new ParcelableOwnerWrapper(owner))
                .tryOpenWith(requireActivity());
    }

    @Override
    public void showAlbumContextMenu(@NonNull PhotoAlbum album) {
        String[] items = {getString(R.string.delete), getString(R.string.edit)};
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(album.getTitle())
                .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            getPresenter().fireAlbumDeleteClick(album);
                            break;
                        case 1:
                            getPresenter().fireAlbumEditClick(album);
                            break;
                    }
                })
                .show();
    }

    @Override
    public void doSelection(@NonNull PhotoAlbum album) {
        Intent result = new Intent();
        result.putExtra(Extra.OWNER_ID, album.getOwnerId());
        result.putExtra(Extra.ALBUM_ID, album.getId());
        requireActivity().setResult(Activity.RESULT_OK, result);
        requireActivity().finish();
    }

    @Override
    public void setCreateAlbumFabVisible(boolean visible) {
        if (Objects.isNull(mFab)) return;

        if (mFab.isShown() && !visible) {
            mFab.hide();
        }

        if (!mFab.isShown() && visible) {
            mFab.show();
        }
    }

    @Override
    public void goToAlbumCreation(int accountId, int ownerId) {
        PlaceFactory.getCreatePhotoAlbumPlace(accountId, ownerId)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void goToAlbumEditing(int accountId, @NonNull PhotoAlbum album, @NonNull PhotoAlbumEditor editor) {
        PlaceFactory.getEditPhotoAlbumPlace(accountId, album, editor)
                //.withParcelableExtra(Extra.OWNER, owner)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void seDrawertPhotoSectionActive(boolean active) {
        if (requireActivity() instanceof OnSectionResumeCallback) {
            if (active) {
                ((OnSectionResumeCallback) requireActivity()).onSectionResume(AdditionalNavigationFragment.SECTION_ITEM_PHOTOS);
            } else {
                ((OnSectionResumeCallback) requireActivity()).onClearSelection();
            }
        }
    }

    @Override
    public void notifyItemRemoved(int index) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemRemoved(index);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataAdded(int position, int size) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, size);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void goToPhotoComments(int accountId, int ownerId) {
        PlaceFactory.getPhotoAllCommentsPlace(accountId, ownerId).tryOpenWith(requireActivity());
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_photo_albums, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_photo_toggle_like).setIcon(Settings.get().other().isDisable_likes() ? R.drawable.ic_no_heart : R.drawable.heart);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_photo_comments) {
            if (!CheckDonate.isFullVersion(requireActivity())) {
                return false;
            }
            getPresenter().fireAllComments();
            return true;
        }

        if (item.getItemId() == R.id.action_photo_toggle_like) {
            Settings.get().other().setDisable_likes(!Settings.get().other().isDisable_likes());
            requireActivity().invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NotNull
    @Override
    public IPresenterFactory<PhotoAlbumsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int ownerId = requireArguments().getInt(Extra.OWNER_ID);
            int accountId = requireArguments().getInt(Extra.ACCOUNT_ID);

            ParcelableOwnerWrapper wrapper = requireArguments().getParcelable(Extra.OWNER);
            Owner owner = Objects.nonNull(wrapper) ? wrapper.get() : null;

            String action = requireArguments().getString(Extra.ACTION);
            return new PhotoAlbumsPresenter(accountId, ownerId, new PhotoAlbumsPresenter.AdditionalParams()
                    .setAction(action)
                    .setOwner(owner), saveInstanceState);
        };
    }

    @Override
    public void onVkPhotoAlbumClick(@NonNull PhotoAlbum album) {
        getPresenter().fireAlbumClick(album);
    }

    @Override
    public boolean onVkPhotoAlbumLongClick(@NonNull PhotoAlbum album) {
        return getPresenter().fireAlbumLongClick(album);
    }
}
