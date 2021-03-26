package dev.idm.vkp.domain;

import dev.idm.vkp.model.Chat;
import io.reactivex.rxjava3.core.Single;

public interface IDialogsInteractor {
    Single<Chat> getChatById(int accountId, int peerId);
}