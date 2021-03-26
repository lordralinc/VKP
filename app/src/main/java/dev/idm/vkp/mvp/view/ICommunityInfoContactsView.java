package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Manager;
import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface ICommunityInfoContactsView extends IAccountDependencyView, IErrorView, IMvpView, IToastView {

    void notifyDataSetChanged();

    void displayRefreshing(boolean loadingNow);

    void displayData(List<Manager> managers);

    void showUserProfile(int accountId, User user);

    void notifyItemRemoved(int index);

    void notifyItemChanged(int index);

    void notifyItemAdded(int index);
}
