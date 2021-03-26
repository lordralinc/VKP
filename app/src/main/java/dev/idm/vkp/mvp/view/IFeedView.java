package dev.idm.vkp.mvp.view;

import androidx.annotation.Nullable;

import java.util.List;

import dev.idm.vkp.model.FeedSource;
import dev.idm.vkp.model.LoadMoreState;
import dev.idm.vkp.model.News;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IFeedView extends IAccountDependencyView, IAttachmentsPlacesView, IMvpView, IErrorView {

    void displayFeedSources(List<FeedSource> sources);

    void notifyFeedSourcesChanged();

    void displayFeed(List<News> data, @Nullable String rawScrollState);

    void notifyFeedDataChanged();

    void notifyDataAdded(int position, int count);

    void notifyItemChanged(int position);

    void setupLoadMoreFooter(@LoadMoreState int state);

    void showRefreshing(boolean refreshing);

    void scrollFeedSourcesToPosition(int position);

    void scrollTo(int pos);

    void goToLikes(int accountId, String type, int ownerId, int id);

    void goToReposts(int accountId, String type, int ownerId, int id);

    void goToPostComments(int accountId, int postId, int ownerId);

    void showSuccessToast();

    void askToReload();
}
