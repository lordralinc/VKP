package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.AudioPlaylist;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IAudioPlaylistsView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<AudioPlaylist> pages);

    void notifyDataSetChanged();

    void notifyItemRemoved(int position);

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void doAddAudios(int accountId);
}
