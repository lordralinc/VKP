package dev.idm.vkp.upload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.idm.vkp.api.PercentagePublisher;
import dev.idm.vkp.api.model.server.UploadServer;
import io.reactivex.rxjava3.core.Single;

public interface IUploadable<T> {
    Single<UploadResult<T>> doUpload(@NonNull Upload upload,
                                     @Nullable UploadServer initialServer,
                                     @Nullable PercentagePublisher listener);
}