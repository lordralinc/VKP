package dev.idm.vkp.db.interfaces;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.db.model.entity.CommentEntity;
import dev.idm.vkp.db.model.entity.OwnerEntities;
import dev.idm.vkp.model.CommentUpdate;
import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.DraftComment;
import dev.idm.vkp.model.criteria.CommentsCriteria;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface ICommentsStorage extends IStorage {

    Single<int[]> insert(int accountId, int sourceId, int sourceOwnerId, int sourceType, List<CommentEntity> dbos, OwnerEntities owners, boolean clearBefore);

    Single<List<CommentEntity>> getDbosByCriteria(@NonNull CommentsCriteria criteria);

    @CheckResult
    Maybe<DraftComment> findEditingComment(int accountId, @NonNull Commented commented);

    @CheckResult
    Single<Integer> saveDraftComment(int accountId, Commented commented, String text, int replyToUser, int replyToComment);

    Completable commitMinorUpdate(CommentUpdate update);

    Observable<CommentUpdate> observeMinorUpdates();

    Completable deleteByDbid(int accountId, Integer dbid);
}
