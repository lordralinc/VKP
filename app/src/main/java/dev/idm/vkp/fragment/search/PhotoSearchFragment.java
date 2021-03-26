package dev.idm.vkp.fragment.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.SearchPhotosAdapter;
import dev.idm.vkp.fragment.search.criteria.PhotoSearchCriteria;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.search.PhotoSearchPresenter;
import dev.idm.vkp.mvp.view.search.IPhotoSearchView;
import dev.idm.vkp.place.PlaceFactory;

public class PhotoSearchFragment extends AbsSearchFragment<PhotoSearchPresenter, IPhotoSearchView, Photo, SearchPhotosAdapter>
        implements SearchPhotosAdapter.PhotosActionListener, IPhotoSearchView {

    private static final String TAG = PhotoSearchFragment.class.getSimpleName();

    public static PhotoSearchFragment newInstance(int accountId, @Nullable PhotoSearchCriteria initialCriteria) {
        Bundle args = new Bundle();
        args.putParcelable(Extra.CRITERIA, initialCriteria);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        PhotoSearchFragment fragment = new PhotoSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    void setAdapterData(SearchPhotosAdapter adapter, List<Photo> data) {
        adapter.setData(data);
    }

    @Override
    void postCreate(View root) {

    }

    @Override
    SearchPhotosAdapter createAdapter(List<Photo> data) {
        SearchPhotosAdapter adapter = new SearchPhotosAdapter(requireActivity(), data, TAG);
        adapter.setPhotosActionListener(this);
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        int columnCount = getResources().getInteger(R.integer.local_gallery_column_count);
        return new GridLayoutManager(requireActivity(), columnCount);
    }

    @NotNull
    @Override
    public IPresenterFactory<PhotoSearchPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new PhotoSearchPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getParcelable(Extra.CRITERIA),
                saveInstanceState
        );
    }

    @Override
    public void displayGallery(int accountId, ArrayList<Photo> photos, int position) {
        PlaceFactory.getSimpleGalleryPlace(accountId, photos, position, false).tryOpenWith(requireActivity());
    }

    @Override
    public void onPhotoClick(SearchPhotosAdapter.PhotoViewHolder holder, Photo photo) {
        getPresenter().firePhotoClick(photo);
    }
}
