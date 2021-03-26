package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.List;

import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.mvp.view.IFwdsView;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;

public class FwdsPresenter extends AbsMessageListPresenter<IFwdsView> {

    public FwdsPresenter(int accountId, List<Message> messages, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        if (!Utils.isEmpty(messages)) {
            getData().addAll(messages);
        }
    }

    public void fireTranscript(String voiceMessageId, int messageId) {
        appendDisposable(Repository.INSTANCE.getMessages().recogniseAudioMessage(getAccountId(), messageId, voiceMessageId)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(v -> {
                }, t -> {
                }));
    }
}