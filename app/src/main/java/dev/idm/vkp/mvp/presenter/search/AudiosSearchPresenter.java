package dev.idm.vkp.mvp.presenter.search;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.domain.IAudioInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.fragment.search.criteria.AudioSearchCriteria;
import dev.idm.vkp.fragment.search.nextfrom.IntNextFrom;
import dev.idm.vkp.model.Audio;
import dev.idm.vkp.mvp.view.search.IAudioSearchView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.player.MusicPlaybackService;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Pair;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;

public class AudiosSearchPresenter extends AbsSearchPresenter<IAudioSearchView, AudioSearchCriteria, Audio, IntNextFrom> {

    private final IAudioInteractor audioInteractor;

    public AudiosSearchPresenter(int accountId, @Nullable AudioSearchCriteria criteria, @Nullable Bundle savedInstanceState) {
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
    Single<Pair<List<Audio>, IntNextFrom>> doSearch(int accountId, AudioSearchCriteria criteria, IntNextFrom startFrom) {
        IntNextFrom nextFrom = new IntNextFrom(startFrom.getOffset() + 50);
        return audioInteractor.search(accountId, criteria, startFrom.getOffset(), 50)
                .map(audio -> Pair.Companion.create(audio, nextFrom));
    }

    public void playAudio(Context context, int position) {
        MusicPlaybackService.startForPlayList(context, (ArrayList<Audio>) data, position, false);
        if (!Settings.get().other().isShow_mini_player())
            PlaceFactory.getPlayerPlace(Settings.get().accounts().getCurrent()).tryOpenWith(context);
    }

    @Override
    boolean canSearch(AudioSearchCriteria criteria) {
        return true;
    }

    @Override
    AudioSearchCriteria instantiateEmptyCriteria() {
        return new AudioSearchCriteria("", false, false);
    }

    public ArrayList<Audio> getSelected() {
        ArrayList<Audio> ret = new ArrayList<>();
        for (Audio i : data) {
            if (i.isSelected())
                ret.add(i);
        }
        return ret;
    }

    public int getAudioPos(Audio audio) {
        if (!Utils.isEmpty(data) && audio != null) {
            int pos = 0;
            for (Audio i : data) {
                if (i.getId() == audio.getId() && i.getOwnerId() == audio.getOwnerId()) {
                    i.setAnimationNow(true);
                    int finalPos = pos;
                    callView(v -> v.notifyAudioChanged(finalPos));
                    return pos;
                }
                pos++;
            }
        }
        return -1;
    }

}