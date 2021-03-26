package dev.idm.vkp.db.interfaces;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import java.util.List;

import dev.idm.vkp.db.model.entity.VideoEntity;
import dev.idm.vkp.model.VideoCriteria;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


public interface IVideoStorage extends IStorage {

    @CheckResult
    Single<List<VideoEntity>> findByCriteria(@NonNull VideoCriteria criteria);

    @CheckResult
    Completable insertData(int accountId, int ownerId, int albumId, List<VideoEntity> videos, boolean invalidateBefore);
}