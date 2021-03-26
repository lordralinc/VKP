package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.model.LocalVideo;
import dev.idm.vkp.mvp.core.IMvpView;


public interface ILocalVideosView extends IMvpView, IErrorView {
    void displayData(@NonNull List<LocalVideo> data);

    void setEmptyTextVisible(boolean visible);

    void displayProgress(boolean loading);

    void returnResultToParent(ArrayList<LocalVideo> photos);

    void updateSelectionAndIndexes();

    void setFabVisible(boolean visible, boolean anim);
}
