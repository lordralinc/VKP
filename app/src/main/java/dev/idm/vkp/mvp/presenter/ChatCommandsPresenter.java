package dev.idm.vkp.mvp.presenter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.fragment.Command;
import dev.idm.vkp.idmapi.IdmApiService;
import dev.idm.vkp.mvp.presenter.base.RxSupportPresenter;
import dev.idm.vkp.mvp.view.IChatCommandsView;
import dev.idm.vkp.util.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ChatCommandsPresenter extends RxSupportPresenter<IChatCommandsView> {
    static ArrayList<Command> commands = null;

    private boolean refreshing;
    List<Command> found;
    private String query = "";



    @SuppressLint("CheckResult")
    public ChatCommandsPresenter(@Nullable Bundle savedInstanceState) {
        super(savedInstanceState);
        if (commands == null) {
            IdmApiService.Factory.create()
                .getCommands()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        response -> {
                            if (response.getResponse() == null) {
                                getView().getCustomToast().showToastError("Ошибка");
                                return;
                            }
                            ChatCommandsPresenter.commands = new ArrayList<>();
                            response.getResponse().forEach(commandResponseItem -> ChatCommandsPresenter.commands.add(new Command(
                                    commandResponseItem.getName(),
                                    commandResponseItem.getName(),
                                    commandResponseItem.getDescription()
                            )));
                            updateCriteria();
                        },
                        error -> {
                            getView().getCustomToast().showToastError("Ошибка: " + error.getMessage());
                            Log.e("IDM API", error.getMessage(), error);
                        }
                );
        }
        found = new ArrayList<>();
        updateCriteria();
    }

    public void updateCriteria() {
        found.clear();
        if (ChatCommandsPresenter.commands == null)
            return;
        if (query == null || query.isEmpty()) {
            found.addAll(ChatCommandsPresenter.commands);
        } else {
            for (Command i : ChatCommandsPresenter.commands) {
                if (i == null) {
                    continue;
                }
                if (i.name.toLowerCase().contains(query.toLowerCase())) {
                    found.add(i);
                }
            }
        }
        callView(IChatCommandsView::notifyDataSetChanged);
    }

    public void fireQuery(String q) {
        if (Utils.isEmpty(q))
            query = null;
        else {
            query = q;
        }
        updateCriteria();
    }


    public void setLoadingNow(boolean loadingNow) {
        refreshing = loadingNow;
        resolveRefreshing();
    }

    @Override
    public void onGuiCreated(@NonNull IChatCommandsView view) {
        super.onGuiCreated(view);
        view.displayData(found);
    }

    private void resolveRefreshing() {
        if (isGuiResumed()) {
            getView().displayRefreshing(refreshing);
        }
    }

    public void fireCommandClick(Command command) {
        getView().openCommand(command);
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshing();
    }

}
