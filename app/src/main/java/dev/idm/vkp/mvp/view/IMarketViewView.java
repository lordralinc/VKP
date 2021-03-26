package dev.idm.vkp.mvp.view;

import dev.idm.vkp.model.Market;
import dev.idm.vkp.model.Peer;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IMarketViewView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayLoading(boolean loading);

    void displayMarket(Market market);

    void sendMarket(int accountId, Market market);

    void onWriteToMarketer(int accountId, Market market, Peer peer);
}
