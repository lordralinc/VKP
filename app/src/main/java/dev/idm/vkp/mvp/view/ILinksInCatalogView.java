package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Link;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface ILinksInCatalogView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayList(List<Link> links);

    void notifyListChanged();

    void displayRefreshing(boolean refresing);
}
