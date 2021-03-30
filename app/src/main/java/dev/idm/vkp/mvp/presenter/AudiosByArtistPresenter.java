package dev.idm.vkp.mvp.presenter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.domain.IAudioInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.model.Audio;
import dev.idm.vkp.model.AudioPlaylist;
import dev.idm.vkp.mvp.presenter.base.AccountDependencyPresenter;
import dev.idm.vkp.mvp.view.IAudiosByArtistView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.player.MusicPlaybackService;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.DownloadWorkUtils;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static dev.idm.vkp.util.Utils.getCauseIfRuntime;

public class AudiosByArtistPresenter extends AccountDependencyPresenter<IAudiosByArtistView> {

    private static final int GET_COUNT = 100;
    private final IAudioInteractor audioInteractor;
    private final ArrayList<Audio> audios;
    private final String artist;
    private final CompositeDisposable audioListDisposable = new CompositeDisposable();
    private boolean actualReceived;
    private boolean loadingNow;
    private boolean endOfContent;

    public AudiosByArtistPresenter(int accountId, String artist, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        audioInteractor = InteractorFactory.createAudioInteractor();
        audios = new ArrayList<>();
        this.artist = artist;
        fireRefresh();
    }

    public boolean isMyAudio() {
        return false;
    }

    public void setLoadingNow(boolean loadingNow) {
        this.loadingNow = loadingNow;
        resolveRefreshingView();
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshingView();
    }

    private void resolveRefreshingView() {
        if (isGuiResumed()) {
            getView().displayRefreshing(loadingNow);
        }
    }

    private void requestNext() {
        setLoadingNow(true);
        int offset = audios.size();
        requestList(offset);
    }

    public void requestList(int offset) {
        setLoadingNow(true);
        audioListDisposable.add(audioInteractor.getAudiosByArtist(getAccountId(), artist, offset, GET_COUNT)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(offset == 0 ? this::onListReceived : this::onNextListReceived, this::onListGetError));
    }

    private void onNextListReceived(List<Audio> next) {
        int startOwnSize = audios.size();
        audios.addAll(next);
        endOfContent = next.isEmpty();
        setLoadingNow(false);
        callView(view -> view.notifyDataAdded(startOwnSize, next.size()));
    }

    private void onListReceived(List<Audio> data) {
        audios.clear();
        audios.addAll(data);
        endOfContent = data.isEmpty();
        actualReceived = true;
        setLoadingNow(false);
        callView(IAudiosByArtistView::notifyListChanged);
    }

    public void playAudio(Context context, int position) {
        MusicPlaybackService.startForPlayList(context, audios, position, false);
        if (!Settings.get().other().isShowMiniPlayer())
            PlaceFactory.getPlayerPlace(getAccountId()).tryOpenWith(context);
    }

    @Override
    public void onDestroyed() {
        audioListDisposable.dispose();
        super.onDestroyed();
    }

    private void onListGetError(Throwable t) {
        setLoadingNow(false);

        if (isGuiResumed()) {
            showError(getView(), getCauseIfRuntime(t));
        }
    }

    public void fireSelectAll() {
        for (Audio i : audios) {
            i.setIsSelected(true);
        }
        callView(IAudiosByArtistView::notifyListChanged);
    }

    public ArrayList<Audio> getSelected(boolean noDownloaded) {
        ArrayList<Audio> ret = new ArrayList<>();
        for (Audio i : audios) {
            if (i.isSelected()) {
                if (noDownloaded) {
                    if (DownloadWorkUtils.TrackIsDownloaded(i) == 0 && !Utils.isEmpty(i.getUrl()) && !i.getUrl().contains("file://") && !i.getUrl().contains("content://")) {
                        ret.add(i);
                    }
                } else {
                    ret.add(i);
                }
            }
        }
        return ret;
    }

    public int getAudioPos(Audio audio) {
        if (!Utils.isEmpty(audios) && audio != null) {
            int pos = 0;
            for (Audio i : audios) {
                if (i.getId() == audio.getId() && i.getOwnerId() == audio.getOwnerId()) {
                    i.setAnimationNow(true);
                    int finalPos = pos;
                    callView(v -> v.notifyItemChanged(finalPos));
                    return pos;
                }
                pos++;
            }
        }
        return -1;
    }

    public void fireUpdateSelectMode() {
        for (Audio i : audios) {
            if (i.isSelected()) {
                i.setIsSelected(false);
            }
        }
        callView(IAudiosByArtistView::notifyListChanged);
    }

    public void fireRefresh() {
        audioListDisposable.clear();
        requestList(0);
    }

    public void onAdd(AudioPlaylist album) {
        int accountId = getAccountId();
        audioListDisposable.add(audioInteractor.followPlaylist(accountId, album.getId(), album.getOwnerId(), album.getAccess_key())
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(data -> getView().getCustomToast().showToast(R.string.success), throwable ->
                        showError(getView(), throwable)));
    }

    public void fireScrollToEnd() {
        if (actualReceived && !endOfContent) {
            requestNext();
        }
    }

    @Override
    public void onGuiCreated(@NonNull IAudiosByArtistView view) {
        super.onGuiCreated(view);
        view.displayList(audios);
    }

}
