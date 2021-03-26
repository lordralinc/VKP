package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Gift;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IGiftsView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<Gift> gifts);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void onOpenWall(int accountId, int ownerId);
}
