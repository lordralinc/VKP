package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.model.VideoAlbum;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IVideoAlbumsByVideoView extends IMvpView, IAccountDependencyView, IErrorView {

    void displayData(@NonNull List<VideoAlbum> data);

    void notifyDataAdded(int position, int count);

    void displayLoading(boolean loading);

    void notifyDataSetChanged();

    void openAlbum(int accountId, int ownerId, int albumId, String action, String title);
}
