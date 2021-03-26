package dev.idm.vkp.mvp.view;

import dev.idm.vkp.model.Community;
import dev.idm.vkp.model.DataWrapper;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface ICommunitiesView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(DataWrapper<Community> own, DataWrapper<Community> filtered, DataWrapper<Community> seacrh);

    void notifyDataSetChanged();

    void notifyOwnDataAdded(int position, int count);

    void displayRefreshing(boolean refreshing);

    void showCommunityWall(int accountId, Community community);

    void notifySeacrhDataAdded(int position, int count);
}