package dev.idm.vkp.mvp.view;

import dev.idm.vkp.model.AnswerVKOfficialList;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IAnswerVKOfficialView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(AnswerVKOfficialList pages);

    void notifyDataSetChanged();

    void notifyUpdateCounter();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);
}
