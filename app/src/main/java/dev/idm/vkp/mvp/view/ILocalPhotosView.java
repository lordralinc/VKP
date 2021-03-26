package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.model.LocalPhoto;
import dev.idm.vkp.mvp.core.IMvpView;


public interface ILocalPhotosView extends IMvpView, IErrorView {
    void displayData(@NonNull List<LocalPhoto> data);

    void setEmptyTextVisible(boolean visible);

    void displayProgress(boolean loading);

    void returnResultToParent(ArrayList<LocalPhoto> photos);

    void updateSelectionAndIndexes();

    void setFabVisible(boolean visible, boolean anim);

    void requestReadExternalStoragePermission();
}
