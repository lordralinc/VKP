package dev.idm.vkp.mvp.view.conversations;

import java.util.List;

import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.IAttachmentsPlacesView;
import dev.idm.vkp.mvp.view.IErrorView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IBaseChatAttachmentsView<T> extends IMvpView, IAccountDependencyView,
        IAttachmentsPlacesView, IErrorView {

    void displayAttachments(List<T> data);

    void notifyDataAdded(int position, int count);

    void notifyDatasetChanged();

    void showLoading(boolean loading);

    void setEmptyTextVisible(boolean visible);

    void setToolbarTitle(String title);

    void setToolbarSubtitle(String subtitle);
}
