package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.User;
import dev.idm.vkp.model.UsersPart;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IRequestsView extends IMvpView, IErrorView, IAccountDependencyView {
    void notifyDatasetChanged(boolean grouping);

    void setSwipeRefreshEnabled(boolean enabled);

    void displayData(List<UsersPart> data, boolean grouping);

    void notifyItemRangeInserted(int position, int count);

    void showUserWall(int accountId, User user);

    void showRefreshing(boolean refreshing);
}
