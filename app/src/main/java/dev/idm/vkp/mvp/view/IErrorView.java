package dev.idm.vkp.mvp.view;

import androidx.annotation.StringRes;

import dev.idm.vkp.util.CustomToast;


public interface IErrorView {
    void showError(String errorText);

    void showThrowable(Throwable throwable);

    void showError(@StringRes int titleTes, Object... params);

    CustomToast getCustomToast();
}
