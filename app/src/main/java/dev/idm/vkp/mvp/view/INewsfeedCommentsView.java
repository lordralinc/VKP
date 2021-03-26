package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.NewsfeedComment;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface INewsfeedCommentsView extends IAccountDependencyView, IAttachmentsPlacesView, IMvpView, IErrorView {
    void displayData(List<NewsfeedComment> data);

    void notifyDataAdded(int position, int count);

    void notifyDataSetChanged();

    void showLoading(boolean loading);
}
