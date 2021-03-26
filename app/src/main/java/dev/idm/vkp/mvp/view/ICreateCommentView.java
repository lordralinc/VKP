package dev.idm.vkp.mvp.view;


public interface ICreateCommentView extends IBaseAttachmentsEditView {
    void returnDataToParent(String textBody);

    void goBack();
}
