package dev.idm.vkp.upload.impl;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.util.Collections;

import dev.idm.vkp.api.PercentagePublisher;
import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.model.server.UploadServer;
import dev.idm.vkp.db.AttachToType;
import dev.idm.vkp.domain.IAttachmentsRepository;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.exception.NotFoundException;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.upload.IUploadable;
import dev.idm.vkp.upload.Method;
import dev.idm.vkp.upload.Upload;
import dev.idm.vkp.upload.UploadDestination;
import dev.idm.vkp.upload.UploadResult;
import dev.idm.vkp.upload.UploadUtils;
import dev.idm.vkp.util.Objects;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.RxUtils.safelyCloseAction;
import static dev.idm.vkp.util.Utils.safelyClose;

public class Photo2WallUploadable implements IUploadable<Photo> {

    private final Context context;
    private final INetworker networker;
    private final IAttachmentsRepository attachmentsRepository;

    public Photo2WallUploadable(Context context, INetworker networker, IAttachmentsRepository attachmentsRepository) {
        this.context = context;
        this.networker = networker;
        this.attachmentsRepository = attachmentsRepository;
    }

    @Override
    public Single<UploadResult<Photo>> doUpload(@NonNull Upload upload, @Nullable UploadServer initialServer, @Nullable PercentagePublisher listener) {
        int subjectOwnerId = upload.getDestination().getOwnerId();
        Integer userId = subjectOwnerId > 0 ? subjectOwnerId : null;
        Integer groupId = subjectOwnerId < 0 ? Math.abs(subjectOwnerId) : null;
        int accountId = upload.getAccountId();

        Single<UploadServer> serverSingle;
        if (Objects.nonNull(initialServer)) {
            serverSingle = Single.just(initialServer);
        } else {
            serverSingle = networker.vkDefault(accountId)
                    .photos()
                    .getWallUploadServer(groupId)
                    .map(s -> s);
        }

        return serverSingle.flatMap(server -> {
            InputStream[] is = new InputStream[1];

            try {
                is[0] = UploadUtils.openStream(context, upload.getFileUri(), upload.getSize());
                return networker.uploads()
                        .uploadPhotoToWallRx(server.getUrl(), is[0], listener)
                        .doFinally(safelyCloseAction(is[0]))
                        .flatMap(dto -> networker.vkDefault(accountId)
                                .photos()
                                .saveWallPhoto(userId, groupId, dto.photo, dto.server, dto.hash, null, null, null)
                                .flatMap(photos -> {
                                    if (photos.isEmpty()) {
                                        return Single.error(new NotFoundException());
                                    }

                                    Photo photo = Dto2Model.transform(photos.get(0));
                                    UploadResult<Photo> result = new UploadResult<>(server, photo);

                                    if (upload.isAutoCommit()) {
                                        return commit(attachmentsRepository, upload, photo).andThen(Single.just(result));
                                    } else {
                                        return Single.just(result);
                                    }
                                }));
            } catch (Exception e) {
                safelyClose(is[0]);
                return Single.error(e);
            }
        });
    }

    private Completable commit(IAttachmentsRepository repository, Upload upload, Photo photo) {
        int accountId = upload.getAccountId();
        UploadDestination dest = upload.getDestination();

        switch (dest.getMethod()) {
            case Method.TO_COMMENT:
                return repository
                        .attach(accountId, AttachToType.COMMENT, dest.getId(), Collections.singletonList(photo));
            case Method.TO_WALL:
                return repository
                        .attach(accountId, AttachToType.POST, dest.getId(), Collections.singletonList(photo));
        }

        return Completable.error(new UnsupportedOperationException());
    }
}