package dev.idm.vkp.mvp.view.search;

import java.util.ArrayList;

import dev.idm.vkp.model.Photo;

public interface IPhotoSearchView extends IBaseSearchView<Photo> {
    void displayGallery(int accountId, ArrayList<Photo> photos, int position);
}
