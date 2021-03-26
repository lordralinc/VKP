package dev.idm.vkp.mvp.presenter.search;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.domain.IPhotosInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.fragment.search.criteria.PhotoSearchCriteria;
import dev.idm.vkp.fragment.search.nextfrom.IntNextFrom;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.mvp.view.search.IPhotoSearchView;
import dev.idm.vkp.util.Pair;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;

public class PhotoSearchPresenter extends AbsSearchPresenter<IPhotoSearchView, PhotoSearchCriteria, Photo, IntNextFrom> {

    private final IPhotosInteractor photoInteractor;

    public PhotoSearchPresenter(int accountId, @Nullable PhotoSearchCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        photoInteractor = InteractorFactory.createPhotosInteractor();
    }

    @Override
    IntNextFrom getInitialNextFrom() {
        return new IntNextFrom(0);
    }

    @Override
    boolean isAtLast(IntNextFrom startFrom) {
        return startFrom.getOffset() == 0;
    }

    @Override
    Single<Pair<List<Photo>, IntNextFrom>> doSearch(int accountId, PhotoSearchCriteria criteria, IntNextFrom startFrom) {
        int offset = startFrom.getOffset();
        IntNextFrom nextFrom = new IntNextFrom(50 + offset);
        return photoInteractor.search(accountId, criteria, offset, 50)
                .map(photos -> Pair.Companion.create(photos, nextFrom));
    }

    @Override
    PhotoSearchCriteria instantiateEmptyCriteria() {
        return new PhotoSearchCriteria("");
    }

    @Override
    boolean canSearch(PhotoSearchCriteria criteria) {
        return Utils.nonEmpty(criteria.getQuery());
    }

    public void firePhotoClick(Photo wrapper) {
        int Index = 0;
        boolean trig = false;
        ArrayList<Photo> photos_ret = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            Photo photo = data.get(i);
            photos_ret.add(photo);
            if (!trig && photo.getId() == wrapper.getId() && photo.getOwnerId() == wrapper.getOwnerId()) {
                Index = i;
                trig = true;
            }
        }
        getView().displayGallery(getAccountId(), photos_ret, Index);
    }
}
