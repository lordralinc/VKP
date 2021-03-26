package dev.idm.vkp.mvp.view;

import androidx.annotation.StringRes;

import dev.idm.vkp.util.CustomToast;


public interface IToastView {
    void showToast(@StringRes int titleTes, boolean isLong, Object... params);

    CustomToast getCustomToast();
}
