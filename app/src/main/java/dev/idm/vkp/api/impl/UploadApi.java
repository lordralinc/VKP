package dev.idm.vkp.api.impl;

import androidx.annotation.NonNull;

import java.io.InputStream;

import dev.idm.vkp.api.IUploadRetrofitProvider;
import dev.idm.vkp.api.PercentagePublisher;
import dev.idm.vkp.api.interfaces.IUploadApi;
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
import dev.idm.vkp.api.services.IUploadService;
import dev.idm.vkp.api.util.ProgressRequestBody;
import dev.idm.vkp.util.Objects;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;


public class UploadApi implements IUploadApi {

    private final IUploadRetrofitProvider provider;

    UploadApi(IUploadRetrofitProvider provider) {
        this.provider = provider;
    }

    private static ProgressRequestBody.UploadCallbacks wrapPercentageListener(PercentagePublisher listener) {
        return percentage -> {
            if (Objects.nonNull(listener)) {
                listener.onProgressChanged(percentage);
            }
        };
    }

    private IUploadService service() {
        return provider.provideUploadRetrofit().blockingGet().create(IUploadService.class);
    }

    @Override
    public Single<UploadDocDto> uploadDocumentRx(String server, String filename, @NonNull InputStream is, PercentagePublisher listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("*/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", filename, body);
        return service().uploadDocumentRx(server, part);
    }

    @Override
    public Single<UploadAudioDto> uploadAudioRx(String server, String filename, @NonNull InputStream is, PercentagePublisher listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("*/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", filename, body);
        return service().uploadAudioRx(server, part);
    }

    @Override
    public Single<BaseResponse<UploadStoryDto>> uploadStoryRx(String server, String filename, @NonNull InputStream is, PercentagePublisher listener, boolean isVideo) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("*/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData(!isVideo ? "photo" : "video_file", filename, body);
        return service().uploadStoryRx(server, part);
    }

    @Override
    public Single<UploadVideoDto> uploadVideoRx(String server, String filename, @NonNull InputStream video, PercentagePublisher listener) {
        ProgressRequestBody body = new ProgressRequestBody(video, wrapPercentageListener(listener), MediaType.parse("*/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", filename, body);
        return service().uploadVideoRx(server, part);
    }

    @Override
    public Single<UploadOwnerPhotoDto> uploadOwnerPhotoRx(String server, @NonNull InputStream is, PercentagePublisher listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", "photo.jpg", body);
        return service().uploadOwnerPhotoRx(server, part);
    }

    @Override
    public Single<UploadChatPhotoDto> uploadChatPhotoRx(String server, @NonNull InputStream is, PercentagePublisher listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", "photo.jpg", body);
        return service().uploadChatPhotoRx(server, part);
    }

    @Override
    public Single<UploadPhotoToWallDto> uploadPhotoToWallRx(String server, @NonNull InputStream is, PercentagePublisher listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", "photo.jpg", body);
        return service().uploadPhotoToWallRx(server, part);
    }

    @Override
    public Single<UploadPhotoToMessageDto> uploadPhotoToMessageRx(String server, @NonNull InputStream is, PercentagePublisher listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", "photo.jpg", body);
        return service().uploadPhotoToMessageRx(server, part);
    }

    @Override
    public Single<UploadPhotoToAlbumDto> uploadPhotoToAlbumRx(String server, @NonNull InputStream is, PercentagePublisher listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file1", "photo.jpg", body);
        return service().uploadPhotoToAlbumRx(server, part);
    }
}
