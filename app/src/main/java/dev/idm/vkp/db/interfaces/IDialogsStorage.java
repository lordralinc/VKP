package dev.idm.vkp.db.interfaces;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;

import dev.idm.vkp.api.model.VKApiChat;
import dev.idm.vkp.db.PeerStateEntity;
import dev.idm.vkp.db.model.PeerPatch;
import dev.idm.vkp.db.model.entity.DialogEntity;
import dev.idm.vkp.db.model.entity.SimpleDialogEntity;
import dev.idm.vkp.model.Chat;
import dev.idm.vkp.model.criteria.DialogsCriteria;
import dev.idm.vkp.util.Optional;
import dev.idm.vkp.util.Pair;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface IDialogsStorage extends IStorage {

    int getUnreadDialogsCount(int accountId);

    Observable<Pair<Integer, Integer>> observeUnreadDialogsCount();

    Single<List<PeerStateEntity>> findPeerStates(int accountId, Collection<Integer> ids);

    void setUnreadDialogsCount(int accountId, int unreadCount);

    Single<Optional<SimpleDialogEntity>> findSimple(int accountId, int peerId);

    Completable saveSimple(int accountId, @NonNull SimpleDialogEntity entity);

    Single<List<DialogEntity>> getDialogs(@NonNull DialogsCriteria criteria);

    Completable removePeerWithId(int accountId, int peerId);

    Completable insertDialogs(int accountId, List<DialogEntity> dbos, boolean clearBefore);

    /**
     * Получение списка идентификаторов диалогов, информация о которых отсутствует в базе данных
     *
     * @param ids список входящих идентификаторов
     * @return отсутствующие
     */
    Single<Collection<Integer>> getMissingGroupChats(int accountId, Collection<Integer> ids);

    Completable insertChats(int accountId, List<VKApiChat> chats);

    Completable applyPatches(int accountId, @NonNull List<PeerPatch> patches);

    Single<Optional<Chat>> findChatById(int accountId, int peerId);
}