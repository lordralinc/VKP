package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface ISimpleOwnersView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayOwnerList(List<Owner> owners);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void displayRefreshing(boolean refreshing);

    void showOwnerWall(int accountId, Owner owner);
}
