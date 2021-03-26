package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.fragment.Command;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IChatCommandsView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<Command> command);

    void notifyItemRemoved(int position);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void displayRefreshing(boolean refreshing);

    void openCommand(Command command);
}
