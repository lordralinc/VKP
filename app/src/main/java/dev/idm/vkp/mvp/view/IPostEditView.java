package dev.idm.vkp.mvp.view;


public interface IPostEditView extends IBasePostEditView, IToastView, IToolbarView {

    void closeAsSuccess();

    void showConfirmExitDialog();


}
