package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface ICommunityLinksView extends IAccountDependencyView, IErrorView, IMvpView {

    void displayRefreshing(boolean loadingNow);

    void notifyDataSetChanged();

    void displayData(List<VKApiCommunity.Link> links);

    void openLink(String link);
}
