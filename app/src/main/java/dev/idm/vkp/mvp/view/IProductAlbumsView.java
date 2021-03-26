package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.MarketAlbum;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IProductAlbumsView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<MarketAlbum> market_albums);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void onMarketAlbumOpen(int accountId, MarketAlbum market_album);
}
