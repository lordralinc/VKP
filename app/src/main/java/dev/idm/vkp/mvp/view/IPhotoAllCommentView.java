package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Comment;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IPhotoAllCommentView extends IAccountDependencyView, IMvpView, IErrorView, IAttachmentsPlacesView {
    void displayData(List<Comment> comments);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void dismissDeepLookingCommentProgress();

    void displayDeepLookingCommentProgress();

    void moveFocusTo(int index, boolean smooth);

    void notifyDataAddedToTop(int count);

    void notifyItemChanged(int index);
}
