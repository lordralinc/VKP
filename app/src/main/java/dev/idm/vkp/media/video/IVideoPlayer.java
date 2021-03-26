package dev.idm.vkp.media.video;

import android.content.Context;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import dev.idm.vkp.model.InternalVideoSize;
import dev.idm.vkp.model.ProxyConfig;
import dev.idm.vkp.model.VideoSize;


public interface IVideoPlayer {
    void updateSource(Context context, String url, ProxyConfig config, @InternalVideoSize int size);

    void play();

    void pause();

    void release();

    int getDuration();

    int getCurrentPosition();

    void seekTo(int position);

    boolean isPlaying();

    int getBufferPercentage();

    void setSurfaceHolder(SurfaceHolder holder);

    void addVideoSizeChangeListener(IVideoSizeChangeListener listener);

    void removeVideoSizeChangeListener(IVideoSizeChangeListener listener);

    interface IVideoSizeChangeListener {
        void onVideoSizeChanged(@NonNull IVideoPlayer player, VideoSize size);
    }
}
