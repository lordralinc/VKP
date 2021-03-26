package dev.idm.vkp.api.interfaces;

import androidx.annotation.NonNull;

import java.io.InputStream;

import dev.idm.vkp.api.PercentagePublisher;
import dev.idm.vkp.api.model.response.BaseResponse;
import dev.idm.vkp.api.model.upload.UploadAudioDto;
import dev.idm.vkp.api.model.upload.UploadChatPhotoDto;
import dev.idm.vkp.api.model.upload.UploadDocDto;
import dev.idm.vkp.api.model.upload.UploadOwnerPhotoDto;
import dev.idm.vkp.api.model.upload.UploadPhotoToAlbumDto;
import dev.idm.vkp.api.model.upload.UploadPhotoToMessageDto;
import dev.idm.vkp.api.model.upload.UploadPhotoToWallDto;
import dev.idm.vkp.api.model.upload.UploadStoryDto;
import dev.idm.vkp.api.model.upload.UploadVideoDto;
import io.reactivex.rxjava3.core.Single;

public interface IUploadApi {
    Single<UploadDocDto> uploadDocumentRx(String server, String filename, @NonNull InputStream doc, PercentagePublisher listener);

    Single<UploadAudioDto> uploadAudioRx(String server, String filename, @NonNull InputStream is, PercentagePublisher listener);

    Single<BaseResponse<UploadStoryDto>> uploadStoryRx(String server, String filename, @NonNull InputStream is, PercentagePublisher listener, boolean isVideo);

    Single<UploadVideoDto> uploadVideoRx(String server, String filename, @NonNull InputStream video, PercentagePublisher listener);

    Single<UploadOwnerPhotoDto> uploadOwnerPhotoRx(String server, @NonNull InputStream photo, PercentagePublisher listener);

    Single<UploadChatPhotoDto> uploadChatPhotoRx(String server, @NonNull InputStream photo, PercentagePublisher listener);

    Single<UploadPhotoToWallDto> uploadPhotoToWallRx(String server, @NonNull InputStream photo, PercentagePublisher listener);

    Single<UploadPhotoToMessageDto> uploadPhotoToMessageRx(String server, @NonNull InputStream is, PercentagePublisher listener);

    Single<UploadPhotoToAlbumDto> uploadPhotoToAlbumRx(String server, @NonNull InputStream file1, PercentagePublisher listener);
}
