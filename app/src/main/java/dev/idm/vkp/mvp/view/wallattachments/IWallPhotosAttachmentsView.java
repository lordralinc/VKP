package dev.idm.vkp.mvp.view.wallattachments;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.TmpSource;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.IAttachmentsPlacesView;
import dev.idm.vkp.mvp.view.IErrorView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IWallPhotosAttachmentsView extends IAccountDependencyView, IMvpView, IErrorView, IAttachmentsPlacesView {
    void displayData(List<Photo> photos);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void setToolbarTitle(String title);

    void setToolbarSubtitle(String subtitle);

    void goToTempPhotosGallery(int accountId, @NonNull TmpSource source, int index);

    void onSetLoadingStatus(int isLoad);
}
