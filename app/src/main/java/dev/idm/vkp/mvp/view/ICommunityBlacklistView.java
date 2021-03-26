package dev.idm.vkp.mvp.view;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.model.Banned;
import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface ICommunityBlacklistView extends IAccountDependencyView, IErrorView, IMvpView, IToastView {

    void displayRefreshing(boolean loadingNow);

    void notifyDataSetChanged();

    void diplayData(List<Banned> data);

    void notifyItemRemoved(int index);

    void openBanEditor(int accountId, int groupId, Banned banned);

    void startSelectProfilesActivity(int accountId, int groupId);

    void addUsersToBan(int accountId, int groupId, ArrayList<User> users);

    void notifyItemsAdded(int position, int size);
}
