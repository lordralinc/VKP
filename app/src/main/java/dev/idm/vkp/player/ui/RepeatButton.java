package dev.idm.vkp.player.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;

import dev.idm.vkp.R;
import dev.idm.vkp.player.MusicPlaybackService;
import dev.idm.vkp.player.util.MusicUtils;

public class RepeatButton extends AppCompatImageButton implements OnClickListener {

    public RepeatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MusicUtils.cycleRepeat();
        updateRepeatState();
    }

    public void updateRepeatState() {
        switch (MusicUtils.getRepeatMode()) {
            case MusicPlaybackService.REPEAT_ALL:
                setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.repeat));
                break;
            case MusicPlaybackService.REPEAT_CURRENT:
                setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.repeat_once));
                break;
            case MusicPlaybackService.REPEAT_NONE:
                setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.repeat_off));
                break;
            default:
                break;
        }
    }
}
