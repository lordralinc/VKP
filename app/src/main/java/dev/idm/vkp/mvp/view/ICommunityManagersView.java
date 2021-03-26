package dev.idm.vkp.mvp.view;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.model.Manager;
import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface ICommunityManagersView extends IAccountDependencyView, IErrorView, IMvpView, IToastView {

    void notifyDataSetChanged();

    void displayRefreshing(boolean loadingNow);

    void displayData(List<Manager> managers);

    void goToManagerEditing(int accountId, int groupId, Manager manager);

    void showUserProfile(int accountId, User user);

    void startSelectProfilesActivity(int accountId, int groupId);

    void startAddingUsersToManagers(int accountId, int groupId, ArrayList<User> users);

    void notifyItemRemoved(int index);

    void notifyItemChanged(int index);

    void notifyItemAdded(int index);
}
