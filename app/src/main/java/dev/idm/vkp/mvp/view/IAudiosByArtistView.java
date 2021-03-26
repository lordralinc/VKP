package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Audio;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IAudiosByArtistView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayList(List<Audio> audios);

    void notifyListChanged();

    void notifyDataAdded(int position, int count);

    void notifyItemChanged(int index);

    void displayRefreshing(boolean refreshing);
}
