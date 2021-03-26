package dev.idm.vkp.mvp.view;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.model.Photo;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IFavePhotosView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<Photo> photos);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void goToGallery(int accountId, ArrayList<Photo> photos, int position);
}