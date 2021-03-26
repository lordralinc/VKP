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
import dev.idm.vkp.db.interfaces.IMessagesStorage;
import dev.idm.vkp.domain.IAttachmentsRepository;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.exception.NotFoundException;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.upload.IUploadable;
import dev.idm.vkp.upload.Upload;
import dev.idm.vkp.upload.UploadResult;
import dev.idm.vkp.upload.UploadUtils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.RxUtils.safelyCloseAction;
import static dev.idm.vkp.util.Utils.safelyClose;

public class Photo2MessageUploadable implements IUploadable<Photo> {

    private final Context context;
    private final INetworker networker;
    private final IAttachmentsRepository attachmentsRepository;
    private final IMessagesStorage messagesStorage;

    public Photo2MessageUploadable(Context context, INetworker networker, IAttachmentsRepository attachmentsRepository, IMessagesStorage messagesStorage) {
        this.context = context;
        this.networker = networker;
        this.attachmentsRepository = attachmentsRepository;
        this.messagesStorage = messagesStorage;
    }

    private static Completable attachIntoDatabaseRx(IAttachmentsRepository repository, IMessagesStorage storage,
                                                    int accountId, int messageId, Photo photo) {
        return repository
                .attach(accountId, AttachToType.MESSAGE, messageId, Collections.singletonList(photo))
                .andThen(storage.notifyMessageHasAttachments(accountId, messageId));
    }

    @Override
    public Single<UploadResult<Photo>> doUpload(@NonNull Upload upload,
                                                @Nullable UploadServer initialServer,
                                                @Nullable PercentagePublisher listener) {
        int accountId = upload.getAccountId();
        int messageId = upload.getDestination().getId();

        Single<UploadServer> serverSingle;
        if (nonNull(initialServer)) {
            serverSingle = Single.just(initialServer);
        } else {
            serverSingle = networker.vkDefault(accountId)
                    .photos()
                    .getMessagesUploadServer().map(s -> s);
        }

        return serverSingle.flatMap(server -> {
            InputStream[] is = new InputStream[1];

            try {
                is[0] = UploadUtils.openStream(context, upload.getFileUri(), upload.getSize());
                return networker.uploads()
                        .uploadPhotoToMessageRx(server.getUrl(), is[0], listener)
                        .doFinally(safelyCloseAction(is[0]))
                        .flatMap(dto -> networker.vkDefault(accountId)
                                .photos()
                                .saveMessagesPhoto(dto.server, dto.photo, dto.hash)
                                .flatMap(photos -> {
                                    if (photos.isEmpty()) {
                                        return Single.error(new NotFoundException());
                                    }

                                    Photo photo = Dto2Model.transform(photos.get(0));
                                    UploadResult<Photo> result = new UploadResult<>(server, photo);

                                    if (upload.isAutoCommit()) {
                                        return attachIntoDatabaseRx(attachmentsRepository, messagesStorage, accountId, messageId, photo)
                                                .andThen(Single.just(result));
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
}