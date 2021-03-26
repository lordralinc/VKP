package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import dev.idm.vkp.mvp.core.IMvpView;

public interface IEnterPinView extends IMvpView, IErrorView, IToastView {
    void displayPin(int[] value, int noValue);

    void sendSuccessAndClose();

    void displayErrorAnimation();

    void displayAvatarFromUrl(@NonNull String url);

    void displayDefaultAvatar();
}
