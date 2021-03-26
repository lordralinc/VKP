package dev.idm.vkp.mvp.view;

import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IRequestExecuteView extends IMvpView, IErrorView, IProgressView, IAccountDependencyView, IToastView {
    void displayBody(String body);

    void hideKeyboard();

    void requestWriteExternalStoragePermission();
}