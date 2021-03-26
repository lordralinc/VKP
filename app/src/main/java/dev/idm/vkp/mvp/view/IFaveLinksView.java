package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.FaveLink;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IFaveLinksView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayLinks(List<FaveLink> links);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void displayRefreshing(boolean refreshing);

    void openLink(int accountId, FaveLink link);

    void notifyItemRemoved(int index);
}