package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.domain.IMessagesRepository;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.mvp.view.IImportantMessagesView;
import dev.idm.vkp.util.RxUtils;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static dev.idm.vkp.util.Utils.getCauseIfRuntime;
import static dev.idm.vkp.util.Utils.getSelected;
import static dev.idm.vkp.util.Utils.nonEmpty;

public class ImportantMessagesPresenter extends AbsMessageListPresenter<IImportantMessagesView> {

    private final IMessagesRepository fInteractor;
    private final CompositeDisposable actualDataDisposable = new CompositeDisposable();
    private boolean actualDataReceived;
    private boolean endOfContent;
    private boolean actualDataLoading;

    public ImportantMessagesPresenter(int accountId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        fInteractor = Repository.INSTANCE.getMessages();
        loadActualData(0);
    }


    private void loadActualData(int offset) {
        actualDataLoading = true;

        resolveRefreshingView();

        int accountId = getAccountId();
        actualDataDisposable.add(fInteractor.getImportantMessages(accountId, 50, offset, null)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(data -> onActualDataReceived(offset, data), this::onActualDataGetError));

    }

    private void onActualDataReceived(int offset, List<Message> data) {

        actualDataLoading = false;
        endOfContent = data.isEmpty();
        actualDataReceived = true;

        if (offset == 0) {
            getData().clear();
            getData().addAll(data);
            safeNotifyDataChanged();
        } else {
            int startSize = getData().size();
            getData().addAll(data);
            callView(view -> view.notifyDataAdded(startSize, data.size()));
        }

        resolveRefreshingView();
    }

    private void onActualDataGetError(Throwable t) {
        actualDataLoading = false;
        showError(getView(), getCauseIfRuntime(t));

        resolveRefreshingView();
    }

    @Override
    protected void onActionModeForwardClick() {
        super.onActionModeForwardClick();
        ArrayList<Message> selected = getSelected(getData());

        if (nonEmpty(selected)) {
            getView().forwardMessages(getAccountId(), selected);
        }
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshingView();
    }

    private void resolveRefreshingView() {
        if (isGuiResumed()) {
            getView().showRefreshing(actualDataLoading);
        }
    }

    public boolean fireScrollToEnd() {
        if (!endOfContent && nonEmpty(getData()) && actualDataReceived && !actualDataLoading) {
            loadActualData(getData().size());
            return false;
        }
        return true;
    }

    public void fireRemoveImportant(int position) {
        Message msg = getData().get(position);
        appendDisposable(fInteractor.markAsImportant(getAccountId(), msg.getPeerId(), Collections.singleton(msg.getId()), 0)
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(() -> {
                    getData().remove(position);
                    safeNotifyDataChanged();
                }, t -> {
                }));
    }

    public void fireRefresh() {

        actualDataDisposable.clear();
        actualDataLoading = false;

        loadActualData(0);
    }

    public void fireMessagesLookup(@NonNull Message message) {
        getView().goToMessagesLookup(getAccountId(), message.getPeerId(), message.getId());
    }

    public void fireTranscript(String voiceMessageId, int messageId) {
        appendDisposable(fInteractor.recogniseAudioMessage(getAccountId(), messageId, voiceMessageId)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(v -> {
                }, t -> {
                }));
    }
}
