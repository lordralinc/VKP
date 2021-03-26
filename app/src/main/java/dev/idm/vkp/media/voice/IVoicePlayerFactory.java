package dev.idm.vkp.media.voice;

import androidx.annotation.NonNull;

public interface IVoicePlayerFactory {
    @NonNull
    IVoicePlayer createPlayer();
}