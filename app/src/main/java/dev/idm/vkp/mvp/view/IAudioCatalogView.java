package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.AudioCatalog;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IAudioCatalogView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<AudioCatalog> pages);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);
}
