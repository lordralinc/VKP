package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Video;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IVideosInCatalogView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayList(List<Video> videos);

    void notifyListChanged();

    void displayRefreshing(boolean refresing);
}
