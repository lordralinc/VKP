package dev.idm.vkp.mvp.view.wallattachments;

import java.util.List;

import dev.idm.vkp.model.Post;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.IAttachmentsPlacesView;
import dev.idm.vkp.mvp.view.IErrorView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IWallAudiosAttachmentsView extends IAccountDependencyView, IMvpView, IErrorView, IAttachmentsPlacesView {
    void displayData(List<Post> posts);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void setToolbarTitle(String title);

    void setToolbarSubtitle(String subtitle);

    void onSetLoadingStatus(int isLoad);

    void openPostEditor(int accountId, Post post);
}
