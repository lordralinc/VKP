package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.model.Video;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;
import dev.idm.vkp.upload.Upload;


public interface IVideosListView extends IAccountDependencyView, IMvpView, IToolbarView, IErrorView {

    String ACTION_SELECT = "VideosFragment.ACTION_SELECT";
    String ACTION_SHOW = "VideosFragment.ACTION_SHOW";

    void displayData(@NonNull List<Video> data);

    void notifyDataAdded(int position, int count);

    void displayLoading(boolean loading);

    void notifyDataSetChanged();

    void notifyItemRemoved(int position);

    void notifyItemChanged(int position);

    void returnSelectionToParent(Video video);

    void showVideoPreview(int accountId, Video video);

    void notifyUploadItemsAdded(int position, int count);

    void notifyUploadItemRemoved(int position);

    void notifyUploadItemChanged(int position);

    void notifyUploadProgressChanged(int position, int progress, boolean smoothly);

    void setUploadDataVisible(boolean visible);

    void startSelectUploadFileActivity(int accountId);

    void requestReadExternalStoragePermission();

    void displayUploads(List<Upload> data);

    void notifyUploadDataChanged();

    void onUploaded(Video upload);

    void doVideoLongClick(int accountId, int ownerId, boolean isMy, int position, @NonNull Video video);

    void displayShareDialog(int accountId, @NonNull Video video, boolean canPostToMyWall);

    void showSuccessToast();
}
