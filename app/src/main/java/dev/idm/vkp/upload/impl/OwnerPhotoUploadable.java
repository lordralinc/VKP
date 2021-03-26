package dev.idm.vkp.upload.impl;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

import dev.idm.vkp.api.PercentagePublisher;
import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.model.server.UploadServer;
import dev.idm.vkp.domain.IWallsRepository;
import dev.idm.vkp.exception.NotFoundException;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.upload.IUploadable;
import dev.idm.vkp.upload.Upload;
import dev.idm.vkp.upload.UploadResult;
import dev.idm.vkp.upload.UploadUtils;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Utils.safelyClose;

public class OwnerPhotoUploadable implements IUploadable<Post> {

    private final Context context;
    private final INetworker networker;
    private final IWallsRepository walls;

    public OwnerPhotoUploadable(Context context, INetworker networker, IWallsRepository walls) {
        this.context = context;
        this.networker = networker;
        this.walls = walls;
    }

    @Override
    public Single<UploadResult<Post>> doUpload(@NonNull Upload upload, @Nullable UploadServer initialServer, @Nullable PercentagePublisher listener) {
        int accountId = upload.getAccountId();
        int ownerId = upload.getDestination().getOwnerId();

        Single<UploadServer> serverSingle;
        if (initialServer == null) {
            serverSingle = networker.vkDefault(accountId)
                    .photos()
                    .getOwnerPhotoUploadServer(ownerId)
                    .map(s -> s);
        } else {
            serverSingle = Single.just(initialServer);
        }

        return serverSingle.flatMap(server -> {
            InputStream[] is = new InputStream[1];

            try {
                is[0] = UploadUtils.openStream(context, upload.getFileUri(), upload.getSize());
                return networker.uploads()
                        .uploadOwnerPhotoRx(server.getUrl(), is[0], listener)
                        .doFinally(() -> safelyClose(is[0]))
                        .flatMap(dto -> networker.vkDefault(accountId)
                                .photos()
                                .saveOwnerPhoto(dto.server, dto.hash, dto.photo)
                                .flatMap(response -> {
                                    if (response.postId == 0) {
                                        return Single.error(new NotFoundException("Post id=0"));
                                    }

                                    return walls.getById(accountId, ownerId, response.postId)
                                            .map(post -> new UploadResult<>(server, post));
                                }));
            } catch (Exception e) {
                safelyClose(is[0]);
                return Single.error(e);
            }
        });
    }
}