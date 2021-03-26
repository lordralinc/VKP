package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Audio;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;
import dev.idm.vkp.upload.Upload;

public interface IAudiosLocalView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayList(List<Audio> audios);

    void notifyItemChanged(int index);

    void notifyItemRemoved(int index);

    void notifyListChanged();

    void notifyUploadItemsAdded(int position, int count);

    void notifyUploadItemRemoved(int position);

    void notifyUploadItemChanged(int position);

    void notifyUploadProgressChanged(int position, int progress, boolean smoothly);

    void setUploadDataVisible(boolean visible);

    void displayUploads(List<Upload> data);

    void notifyUploadDataChanged();

    void displayRefreshing(boolean refreshing);

    void checkPermission();
}
