package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.model.DocFilter;
import dev.idm.vkp.model.Document;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;
import dev.idm.vkp.upload.Upload;


public interface IDocListView extends IAccountDependencyView, IMvpView, IErrorView {

    void displayData(List<Document> documents, boolean asImages);

    void showRefreshing(boolean refreshing);

    void notifyDataSetChanged();

    void notifyDataAdd(int position, int count);

    void openDocument(int accountId, @NonNull Document document);

    void returnSelection(ArrayList<Document> docs);

    void goToGifPlayer(int accountId, @NonNull ArrayList<Document> gifs, int selected);

    void requestReadExternalStoragePermission();

    void startSelectUploadFileActivity(int accountId);

    void setUploadDataVisible(boolean visible);

    void displayUploads(List<Upload> data);

    void notifyUploadDataChanged();

    void notifyUploadItemsAdded(int position, int count);

    void notifyUploadItemChanged(int position);

    void notifyUploadItemRemoved(int position);

    void notifyUploadProgressChanged(int position, int progress, boolean smoothly);

    void displayFilterData(List<DocFilter> filters);

    void notifyFiltersChanged();

    void setAdapterType(boolean imagesOnly);
}
