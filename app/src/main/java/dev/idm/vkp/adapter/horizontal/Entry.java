package dev.idm.vkp.adapter.horizontal;

import android.content.Context;

import androidx.annotation.NonNull;

public interface Entry {

    String getTitle(@NonNull Context context);

    boolean isActive();

    boolean isCustom();
}
