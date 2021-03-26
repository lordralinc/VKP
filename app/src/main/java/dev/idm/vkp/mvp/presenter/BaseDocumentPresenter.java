package dev.idm.vkp.mvp.presenter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.idm.vkp.R;
import dev.idm.vkp.domain.IDocsInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.model.Document;
import dev.idm.vkp.mvp.presenter.base.AccountDependencyPresenter;
import dev.idm.vkp.mvp.view.IBasicDocumentView;
import dev.idm.vkp.util.RxUtils;

import static dev.idm.vkp.util.Utils.getCauseIfRuntime;


public class BaseDocumentPresenter<V extends IBasicDocumentView> extends AccountDependencyPresenter<V> {

    private static final String TAG = BaseDocumentPresenter.class.getSimpleName();

    private final IDocsInteractor docsInteractor;

    public BaseDocumentPresenter(int accountId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        docsInteractor = InteractorFactory.createDocsInteractor();
    }

    public final void fireWritePermissionResolved(Context context, View view) {
        onWritePermissionResolved(context, view);
    }

    protected void onWritePermissionResolved(Context context, View view) {
        // hook for child classes
    }

    protected void addYourself(@NonNull Document document) {
        int accountId = getAccountId();
        int docId = document.getId();
        int ownerId = document.getOwnerId();

        appendDisposable(docsInteractor.add(accountId, docId, ownerId, document.getAccessKey())
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(id -> onDocAddedSuccessfully(docId, ownerId, id), t -> showError(getView(), getCauseIfRuntime(t))));
    }

    protected void delete(int id, int ownerId) {
        int accountId = getAccountId();
        appendDisposable(docsInteractor.delete(accountId, id, ownerId)
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(() -> onDocDeleteSuccessfully(id, ownerId), this::onDocDeleteError));
    }

    private void onDocDeleteError(Throwable t) {
        showError(getView(), getCauseIfRuntime(t));
    }

    @SuppressWarnings("unused")
    protected void onDocDeleteSuccessfully(int id, int ownerId) {
        safeShowLongToast(getView(), R.string.deleted);
    }

    @SuppressWarnings("unused")
    protected void onDocAddedSuccessfully(int id, int ownerId, int resultDocId) {
        safeShowLongToast(getView(), R.string.added);
    }
}
