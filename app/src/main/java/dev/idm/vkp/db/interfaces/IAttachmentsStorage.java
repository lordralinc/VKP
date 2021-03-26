package dev.idm.vkp.db.interfaces;

import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.db.AttachToType;
import dev.idm.vkp.db.model.entity.Entity;
import dev.idm.vkp.util.Pair;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


public interface IAttachmentsStorage extends IStorage {

    Completable remove(int accountId, @AttachToType int attachToType, int attachToDbid, int generatedAttachmentId);

    Single<int[]> attachDbos(int accountId, @AttachToType int attachToType, int attachToDbid, @NonNull List<Entity> entities);

    Single<Integer> getCount(int accountId, @AttachToType int attachToType, int attachToDbid);

    Single<List<Pair<Integer, Entity>>> getAttachmentsDbosWithIds(int accountId, @AttachToType int attachToType, int attachToDbid);

    List<Entity> getAttachmentsDbosSync(int accountId, @AttachToType int attachToType, int attachToDbid, @NonNull Cancelable cancelable);
}