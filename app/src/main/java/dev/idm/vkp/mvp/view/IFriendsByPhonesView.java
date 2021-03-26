package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IFriendsByPhonesView extends IMvpView, IAccountDependencyView, IErrorView {

    void displayData(@NonNull List<Owner> owners);

    void notifyDataAdded(int position, int count);

    void displayLoading(boolean loading);

    void notifyDataSetChanged();

    void showOwnerWall(int accountId, Owner owner);
}
