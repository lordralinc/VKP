package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.LogEventType;
import dev.idm.vkp.model.LogEventWrapper;
import dev.idm.vkp.mvp.core.IMvpView;


public interface ILogsView extends IMvpView, IErrorView {

    void displayTypes(List<LogEventType> types);

    void displayData(List<LogEventWrapper> events);

    void showRefreshing(boolean refreshing);

    void notifyEventDataChanged();

    void notifyTypesDataChanged();

    void setEmptyTextVisible(boolean visible);
}
