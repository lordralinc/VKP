package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.AppChatUser;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IChatMembersView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<AppChatUser> users);

    void notifyItemRemoved(int position);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void openUserWall(int accountId, Owner user);

    void displayRefreshing(boolean refreshing);

    void startSelectUsersActivity(int accountId);
}