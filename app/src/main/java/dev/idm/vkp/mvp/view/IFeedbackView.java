package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.model.LoadMoreState;
import dev.idm.vkp.model.feedback.Feedback;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IFeedbackView extends IAccountDependencyView, IMvpView, IAttachmentsPlacesView, IErrorView {
    void displayData(List<Feedback> data);

    void showLoading(boolean loading);

    void notifyDataAdding(int position, int count);

    void notifyDataSetChanged();

    void configLoadMore(@LoadMoreState int loadmoreState);

    void showLinksDialog(int accountId, @NonNull Feedback notification);

    void notifyUpdateCounter();
}
