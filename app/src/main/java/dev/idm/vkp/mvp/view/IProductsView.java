package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.model.Market;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IProductsView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<Market> market);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void onOpenMarket(int accountId, @NonNull Market market);
}
