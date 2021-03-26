package dev.idm.vkp.mvp.view.search;

import dev.idm.vkp.model.Audio;

public interface IAudioSearchView extends IBaseSearchView<Audio> {
    void notifyAudioChanged(int index);
}
