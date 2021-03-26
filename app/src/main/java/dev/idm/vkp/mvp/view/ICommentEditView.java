package dev.idm.vkp.mvp.view;

import androidx.annotation.Nullable;

import dev.idm.vkp.model.Comment;


public interface ICommentEditView extends IBaseAttachmentsEditView, IProgressView {
    void goBackWithResult(@Nullable Comment comment);

    void showConfirmWithoutSavingDialog();

    void goBack();
}
