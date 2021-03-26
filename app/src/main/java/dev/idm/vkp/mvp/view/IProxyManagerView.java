package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.ProxyConfig;
import dev.idm.vkp.mvp.core.IMvpView;


public interface IProxyManagerView extends IMvpView, IErrorView {
    void displayData(List<ProxyConfig> configs, ProxyConfig active);

    void notifyItemAdded(int position);

    void notifyItemRemoved(int position);

    void setActiveAndNotifyDataSetChanged(ProxyConfig config);

    void goToAddingScreen();
}