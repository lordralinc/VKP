package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IUserBannedView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayUserList(List<User> users);

    void notifyItemsAdded(int position, int count);

    void notifyDataSetChanged();

    void notifyItemRemoved(int position);

    void displayRefreshing(boolean refreshing);

    void startUserSelection(int accountId);

    void showSuccessToast();

    void scrollToPosition(int position);

    void showUserProfile(int accountId, User user);
}