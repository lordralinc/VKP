package dev.idm.vkp.mvp.view.search;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.fragment.search.options.BaseOption;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.IAttachmentsPlacesView;
import dev.idm.vkp.mvp.view.IErrorView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IBaseSearchView<T> extends IMvpView, IErrorView, IAccountDependencyView, IAttachmentsPlacesView {
    void displayData(List<T> data);

    void setEmptyTextVisible(boolean visible);

    void notifyDataSetChanged();

    void notifyItemChanged(int index);

    void notifyDataAdded(int position, int count);

    void showLoading(boolean loading);

    void displayFilter(int accountId, ArrayList<BaseOption> options);
}
