package dev.idm.vkp.domain;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.List;

import dev.idm.vkp.api.model.VKApiMessage;
import dev.idm.vkp.api.model.longpoll.BadgeCountChangeUpdate;
import dev.idm.vkp.api.model.longpoll.InputMessagesSetReadUpdate;
import dev.idm.vkp.api.model.longpoll.MessageFlagsResetUpdate;
import dev.idm.vkp.api.model.longpoll.MessageFlagsSetUpdate;
import dev.idm.vkp.api.model.longpoll.OutputMessagesSetReadUpdate;
import dev.idm.vkp.api.model.longpoll.WriteTextInDialogUpdate;
import dev.idm.vkp.model.AbsModel;
import dev.idm.vkp.model.AppChatUser;
import dev.idm.vkp.model.Conversation;
import dev.idm.vkp.model.Dialog;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.model.MessageUpdate;
import dev.idm.vkp.model.Peer;
import dev.idm.vkp.model.PeerDeleting;
import dev.idm.vkp.model.PeerUpdate;
import dev.idm.vkp.model.SaveMessageBuilder;
import dev.idm.vkp.model.SentMsg;
import dev.idm.vkp.model.User;
import dev.idm.vkp.model.WriteText;
import dev.idm.vkp.util.Pair;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface IMessagesRepository {
    Flowable<Throwable> observeMessagesSendErrors();

    Completable handleFlagsUpdates(int accountId, @Nullable List<MessageFlagsSetUpdate> setUpdates, @Nullable List<MessageFlagsResetUpdate> resetUpdates);

    Completable handleReadUpdates(int accountId, @Nullable List<OutputMessagesSetReadUpdate> setUpdates, @Nullable List<InputMessagesSetReadUpdate> resetUpdates);

    Completable handleUnreadBadgeUpdates(int accountId, @NonNull List<BadgeCountChangeUpdate> updates);

    Completable handleWriteUpdates(int accountId, @NonNull List<WriteTextInDialogUpdate> updates);

    Flowable<SentMsg> observeSentMessages();

    Flowable<List<PeerUpdate>> observePeerUpdates();

    Flowable<List<MessageUpdate>> observeMessageUpdates();

    Flowable<List<WriteText>> observeTextWrite();

    Flowable<PeerDeleting> observePeerDeleting();

    Single<Conversation> getConversationSingle(int accountId, int peerId, @NonNull Mode mode);

    Flowable<Conversation> getConversation(int accountId, int peerId, @NonNull Mode mode);

    Single<Message> edit(int accountId, @NonNull Message message, String body, @NonNull List<AbsModel> attachments, boolean keepForwardMessages);

    void runSendingQueue();

    /**
     * Получить все закэшированные сообщения в локальной БД
     *
     * @param accountId идентификатор аккаунта
     * @param peerId    идентификатор диалога
     * @return полученные сообщения
     */
    Single<List<Message>> getCachedPeerMessages(int accountId, int peerId);

    /**
     * Получить все закэшированные диалоги в локальной БД
     *
     * @param accountId идентификатор аккаунта
     * @return диалоги
     */
    Single<List<Dialog>> getCachedDialogs(int accountId);

    Single<Pair<Peer, List<Message>>> getMessagesFromLocalJSon(int accountId, Context context);

    /**
     * Сохранить в локальную БД сообщения
     *
     * @param accountId идентификатор аккаунта
     * @param messages  сообщения
     * @return Completable
     */
    Completable insertMessages(int accountId, List<VKApiMessage> messages);

    /**
     * Получить актуальный список сообщений для конкретного диалога
     *
     * @param accountId      идентификатор аккаунта
     * @param peerId         идентификатор диалога
     * @param count          количество сообщений
     * @param offset         сдвиг (может быть как положительным, так и отрицательным)
     * @param startMessageId идентификатор сообщения, после которого необходимо получить (если null - от последнего)
     * @param cacheData      если true - сохранить полученные данные в кэш
     * @return полученные сообщения
     */
    Single<List<Message>> getPeerMessages(int accountId, int peerId, int count, Integer offset, Integer startMessageId, boolean cacheData, boolean rev);

    Single<List<String>> getJsonHistory(int accountId, Integer offset, Integer count, int peerId);

    Single<List<Message>> getImportantMessages(int accountId, int count, Integer offset, Integer startMessageId);

    Single<List<Dialog>> getDialogs(int accountId, int count, Integer startMessageId);

    Single<List<Message>> findCachedMessages(int accountId, List<Integer> ids);

    Single<Message> put(SaveMessageBuilder builder);

    Single<SentMsg> sendUnsentMessage(Collection<Integer> accountIds);

    Completable enqueueAgain(int accountId, int messageId);

    Completable enqueueAgainList(int accountId, Collection<Integer> ids);

    /**
     * Поиск диалогов
     *
     * @param accountId идентификатор аккаунта
     * @param count     количество результатов
     * @param q         строка поиска
     * @return список найденных диалогов
     */
    Single<List<Conversation>> searchConversations(int accountId, int count, String q);

    Single<List<Message>> searchMessages(int accountId, Integer peerId, int count, int offset, String q);

    Single<List<AppChatUser>> getChatUsers(int accountId, int chatId);

    Completable removeChatMember(int accountId, int chatId, int userId);

    Single<List<AppChatUser>> addChatUsers(int accountId, int chatId, List<User> users);

    Completable deleteChatPhoto(int accountId, int chatId);

    Completable deleteDialog(int accountId, int peedId);

    Completable deleteMessages(int accountId, int peerId, @NonNull Collection<Integer> ids, boolean forAll);

    Completable restoreMessage(int accountId, int peerId, int messageId);

    Completable editChat(int accountId, int chatId, String title);

    Single<Integer> createGroupChat(int accountId, Collection<Integer> users, String title);

    Single<Integer> recogniseAudioMessage(int accountId, Integer message_id, String audio_message_id);

    Completable markAsRead(int accountId, int peerId, int toId);

    Completable markAsImportant(int accountId, int peerId, @NonNull Collection<Integer> ids, Integer important);

    Completable pin(int accountId, int peerId, @Nullable Message message);
}
