package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.AudioPlaylist;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IPlaylistsInCatalogView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayList(List<AudioPlaylist> audios);

    void notifyListChanged();

    void displayRefreshing(boolean refresing);
}
