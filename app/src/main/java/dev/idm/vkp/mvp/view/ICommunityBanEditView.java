package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.IdOption;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface ICommunityBanEditView extends IMvpView, IAccountDependencyView, IErrorView, IProgressView, IToastView {
    void displayUserInfo(Owner user);

    void displayBanStatus(int adminId, String adminName, long endDate);

    void displayBlockFor(String blockFor);

    void displayReason(String reason);

    void diplayComment(String comment);

    void setShowCommentChecked(boolean checked);

    void goBack();

    void displaySelectOptionDialog(int requestCode, List<IdOption> options);

    void openProfile(int accountId, Owner owner);
}