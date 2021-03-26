package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Video;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IFaveVideosView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<Video> videos);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void goToPreview(int accountId, Video video);
}
