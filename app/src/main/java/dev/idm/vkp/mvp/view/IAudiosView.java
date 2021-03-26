package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Audio;
import dev.idm.vkp.model.AudioPlaylist;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IAudiosView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayList(List<Audio> audios);

    void notifyListChanged();

    void notifyItemMoved(int fromPosition, int toPosition);

    void notifyItemRemoved(int index);

    void notifyDataAdded(int position, int count);

    void notifyItemChanged(int index);

    void displayRefreshing(boolean refreshing);

    void updatePlaylists(List<AudioPlaylist> stories);
}
