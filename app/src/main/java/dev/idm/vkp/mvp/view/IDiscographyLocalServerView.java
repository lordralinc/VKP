package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Audio;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IDiscographyLocalServerView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayList(List<Audio> audios);

    void notifyListChanged();

    void notifyItemChanged(int index);

    void notifyDataAdded(int position, int count);

    void displayLoading(boolean loading);
}
