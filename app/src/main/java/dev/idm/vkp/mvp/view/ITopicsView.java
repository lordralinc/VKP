package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.model.LoadMoreState;
import dev.idm.vkp.model.Topic;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface ITopicsView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(@NonNull List<Topic> topics);

    void notifyDataSetChanged();

    void notifyDataAdd(int position, int count);

    void showRefreshing(boolean refreshing);

    void setupLoadMore(@LoadMoreState int state);

    void goToComments(int accountId, @NonNull Topic topic);

    void setButtonCreateVisible(boolean visible);
}