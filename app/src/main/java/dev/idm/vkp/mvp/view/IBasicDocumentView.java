package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import dev.idm.vkp.model.Document;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IBasicDocumentView extends IMvpView, IAccountDependencyView, IToastView, IErrorView {

    void shareDocument(int accountId, @NonNull Document document);

    void requestWriteExternalStoragePermission();

}
