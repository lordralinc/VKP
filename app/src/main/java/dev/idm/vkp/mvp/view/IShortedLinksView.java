package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.ShortLink;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IShortedLinksView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<ShortLink> links);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void updateLink(String url);

    void showLinkStatus(String status);
}
