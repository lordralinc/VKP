package dev.idm.vkp.mvp.view;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.model.AttachmenEntry;
import dev.idm.vkp.model.LocalPhoto;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IBaseAttachmentsEditView extends IMvpView, IAccountDependencyView, IErrorView {

    void displayInitialModels(@NonNull List<AttachmenEntry> models);

    void setSupportedButtons(boolean photo, boolean audio, boolean video, boolean doc, boolean poll, boolean timer);

    void setTextBody(CharSequence text);

    void openAddVkPhotosWindow(int maxSelectionCount, int accountId, int ownerId);

    void openAddPhotoFromGalleryWindow(int maxSelectionCount);

    void openCamera(@NonNull Uri photoCameraUri);

    void openAddAudiosWindow(int maxSelectionCount, int accountId);

    void openAddDocumentsWindow(int maxSelectionCount, int accountId);

    void openAddVideosWindow(int maxSelectionCount, int accountId);

    void openPollCreationWindow(int accountId, int ownerId);

    void requestReadExternalStoragePermission();

    void requestCameraPermission();

    void displayChoosePhotoTypeDialog();

    void notifySystemAboutNewPhoto(@NonNull Uri uri);

    void displaySelectUploadPhotoSizeDialog(@NonNull List<LocalPhoto> photos);

    void notifyDataSetChanged();

    void updateProgressAtIndex(int index, int progress);

    void setTimerValue(Long time);

    void showEnterTimeDialog(long initialTimeUnixtime);

    void notifyItemRangeInsert(int position, int count);

    void notifyItemRemoved(int position);

    void displayCropPhotoDialog(Uri uri);
}
