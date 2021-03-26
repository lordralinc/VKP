package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import dev.idm.vkp.media.gif.IGifPlayer;
import dev.idm.vkp.model.Story;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IStoryPagerView extends IMvpView, IErrorView, IToastView, IAccountDependencyView {

    void displayData(int pageCount, int selectedIndex);

    void setAspectRatioAt(int position, int w, int h);

    void setPreparingProgressVisible(int position, boolean preparing);

    void attachDisplayToPlayer(int adapterPosition, IGifPlayer gifPlayer);

    void setToolbarTitle(@StringRes int titleRes, Object... params);

    void setToolbarSubtitle(@NonNull Story story, int account_id);

    void configHolder(int adapterPosition, boolean progress, int aspectRatioW, int aspectRatioH);

    void requestWriteExternalStoragePermission();
}
