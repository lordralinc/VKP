package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IPhotoPagerView extends IMvpView, IAccountDependencyView, IErrorView, IToastView {

    void goToLikesList(int accountId, int ownerId, int photoId);

    void setupLikeButton(boolean visible, boolean like, int likes);

    void setupWithUserButton(int users);

    void setupShareButton(boolean visible);

    void setupCommentsButton(boolean visible, int count);

    void displayPhotos(@NonNull List<Photo> photos, int initialIndex);

    void setToolbarTitle(@Nullable String title);

    void setToolbarSubtitle(@Nullable String subtitle);

    void sharePhoto(int accountId, @NonNull Photo photo);

    void postToMyWall(@NonNull Photo photo, int accountId);

    void requestWriteToExternalStoragePermission();

    void setButtonRestoreVisible(boolean visible);

    void setupOptionMenu(boolean canSaveYourself, boolean canDelete);

    void goToComments(int accountId, @NonNull Commented commented);

    void displayPhotoListLoading(boolean loading);

    void setButtonsBarVisible(boolean visible);

    void setToolbarVisible(boolean visible);

    void rebindPhotoAt(int position);
}
