package dev.idm.vkp.domain.impl;

import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.db.interfaces.IStorages;
import dev.idm.vkp.domain.IDialogsInteractor;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.exception.NotFoundException;
import dev.idm.vkp.model.Chat;
import dev.idm.vkp.model.Peer;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Utils.isEmpty;

public class DialogsInteractor implements IDialogsInteractor {

    private final INetworker networker;

    private final IStorages repositories;

    public DialogsInteractor(INetworker networker, IStorages repositories) {
        this.networker = networker;
        this.repositories = repositories;
    }

    @Override
    public Single<Chat> getChatById(int accountId, int peerId) {
        return repositories.dialogs()
                .findChatById(accountId, peerId)
                .flatMap(optional -> {
                    if (optional.nonEmpty()) {
                        return Single.just(optional.get());
                    }

                    int chatId = Peer.toChatId(peerId);
                    return networker.vkDefault(accountId)
                            .messages()
                            .getChat(chatId, null, null, null)
                            .map(chats -> {
                                if (isEmpty(chats)) {
                                    throw new NotFoundException();
                                }

                                return chats.get(0);
                            })
                            .map(Dto2Model::transform);
                });
    }
}
