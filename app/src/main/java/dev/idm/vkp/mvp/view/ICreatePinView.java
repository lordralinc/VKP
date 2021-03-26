package dev.idm.vkp.mvp.view;

import androidx.annotation.StringRes;

import dev.idm.vkp.mvp.core.IMvpView;

public interface ICreatePinView extends IMvpView, IErrorView {
    void displayTitle(@StringRes int titleRes);

    void displayErrorAnimation();

    void displayPin(int[] value, int noValue);

    void sendSkipAndClose();

    void sendSuccessAndClose(int[] values);
}
