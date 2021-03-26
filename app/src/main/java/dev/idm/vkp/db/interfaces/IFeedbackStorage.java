package dev.idm.vkp.db.interfaces;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.db.model.entity.OwnerEntities;
import dev.idm.vkp.db.model.entity.feedback.FeedbackEntity;
import dev.idm.vkp.model.criteria.NotificationsCriteria;
import io.reactivex.rxjava3.core.Single;

public interface IFeedbackStorage extends IStorage {
    Single<int[]> insert(int accountId, List<FeedbackEntity> dbos, OwnerEntities owners, boolean clearBefore);

    Single<List<FeedbackEntity>> findByCriteria(@NonNull NotificationsCriteria criteria);
}