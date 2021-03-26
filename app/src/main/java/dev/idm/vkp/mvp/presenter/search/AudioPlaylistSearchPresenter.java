package dev.idm.vkp.mvp.presenter.search;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.domain.IAudioInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.fragment.search.criteria.AudioPlaylistSearchCriteria;
import dev.idm.vkp.fragment.search.nextfrom.IntNextFrom;
import dev.idm.vkp.model.AudioPlaylist;
import dev.idm.vkp.mvp.view.search.IAudioPlaylistSearchView;
import dev.idm.vkp.util.Pair;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;

public class AudioPlaylistSearchPresenter extends AbsSearchPresenter<IAudioPlaylistSearchView, AudioPlaylistSearchCriteria, AudioPlaylist, IntNextFrom> {

    private final IAudioInteractor audioInteractor;

    public AudioPlaylistSearchPresenter(int accountId, @Nullable AudioPlaylistSearchCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        audioInteractor = InteractorFactory.createAudioInteractor();
    }

    @Override
    IntNextFrom getInitialNextFrom() {
        return new IntNextFrom(0);
    }

    @Override
    boolean isAtLast(IntNextFrom startFrom) {
        return startFrom.getOffset() == 0;
    }

    @Override
    void onSeacrhError(Throwable throwable) {
        super.onSeacrhError(throwable);
        if (isGuiResumed()) {
            showError(getView(), Utils.getCauseIfRuntime(throwable));
        }
    }

    @Override
    Single<Pair<List<AudioPlaylist>, IntNextFrom>> doSearch(int accountId, AudioPlaylistSearchCriteria criteria, IntNextFrom startFrom) {
        IntNextFrom nextFrom = new IntNextFrom(startFrom.getOffset() + 50);
        return audioInteractor.searchPlaylists(accountId, criteria, startFrom.getOffset(), 50)
                .map(audio -> Pair.Companion.create(audio, nextFrom));
    }

    public void onAdd(AudioPlaylist album) {
        int accountId = getAccountId();
        appendDisposable(audioInteractor.followPlaylist(accountId, album.getId(), album.getOwnerId(), album.getAccess_key())
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(data -> getView().getCustomToast().showToast(R.string.success), throwable ->
                        showError(getView(), throwable)));
    }

    @Override
    boolean canSearch(AudioPlaylistSearchCriteria criteria) {
        return true;
    }

    @Override
    AudioPlaylistSearchCriteria instantiateEmptyCriteria() {
        return new AudioPlaylistSearchCriteria("");
    }
}
