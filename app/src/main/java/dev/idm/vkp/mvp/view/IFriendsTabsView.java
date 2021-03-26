package dev.idm.vkp.mvp.view;

import dev.idm.vkp.model.FriendsCounters;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IFriendsTabsView extends IMvpView, IAccountDependencyView, IErrorView {
    void displayConters(FriendsCounters counters);

    void configTabs(int accountId, int userId, boolean isNotMyPage);

    void displayUserNameAtToolbar(String userName);

    void setDrawerFriendsSectionSelected(boolean selected);
}