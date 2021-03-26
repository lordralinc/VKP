package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.FavePage;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IFaveUsersView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<FavePage> pages);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void openOwnerWall(int accountId, Owner owner);

    void notifyItemRemoved(int index);
}
