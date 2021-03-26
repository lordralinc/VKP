package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.domain.IVideosInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.model.VideoAlbum;
import dev.idm.vkp.mvp.presenter.base.AccountDependencyPresenter;
import dev.idm.vkp.mvp.reflect.OnGuiCreated;
import dev.idm.vkp.mvp.view.IVideoAlbumsByVideoView;
import dev.idm.vkp.util.RxUtils;


public class VideoAlbumsByVideoPresenter extends AccountDependencyPresenter<IVideoAlbumsByVideoView> {

    private final int ownerId;
    private final int videoOwnerId;
    private final int videoId;
    private final List<VideoAlbum> data;
    private final IVideosInteractor videosInteractor;
    private boolean netLoadingNow;

    public VideoAlbumsByVideoPresenter(int accountId, int ownerId, int owner, int video, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);

        videosInteractor = InteractorFactory.createVideosInteractor();
        this.ownerId = ownerId;
        videoOwnerId = owner;
        videoId = video;
        data = new ArrayList<>();

        requestActualData();
    }

    @OnGuiCreated
    private void resolveRefreshingView() {
        if (isGuiReady()) {
            getView().displayLoading(netLoadingNow);
        }
    }

    private void requestActualData() {
        netLoadingNow = true;

        resolveRefreshingView();

        int accountId = getAccountId();
        appendDisposable(videosInteractor.getAlbumsByVideo(accountId, ownerId, videoOwnerId, videoId)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onActualDataReceived, this::onActualDataGetError));
    }

    private void onActualDataGetError(Throwable t) {
        netLoadingNow = false;
        resolveRefreshingView();

        showError(getView(), t);
    }

    private void onActualDataReceived(List<VideoAlbum> albums) {

        netLoadingNow = false;

        resolveRefreshingView();

        data.clear();
        data.addAll(albums);
        callView(IVideoAlbumsByVideoView::notifyDataSetChanged);
    }

    @Override
    public void onGuiCreated(@NonNull IVideoAlbumsByVideoView view) {
        super.onGuiCreated(view);
        view.displayData(data);
    }

    public void fireItemClick(VideoAlbum album) {
        getView().openAlbum(getAccountId(), ownerId, album.getId(), null, album.getTitle());
    }

    public void fireRefresh() {
        requestActualData();
    }
}
