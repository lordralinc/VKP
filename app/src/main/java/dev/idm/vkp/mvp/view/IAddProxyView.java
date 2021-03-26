package dev.idm.vkp.mvp.view;

import dev.idm.vkp.mvp.core.IMvpView;


public interface IAddProxyView extends IMvpView, IErrorView {
    void setAuthFieldsEnabled(boolean enabled);

    void setAuthChecked(boolean checked);

    void goBack();
}
