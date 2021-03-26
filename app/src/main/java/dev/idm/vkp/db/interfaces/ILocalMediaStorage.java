package dev.idm.vkp.db.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import dev.idm.vkp.model.Audio;
import dev.idm.vkp.model.LocalImageAlbum;
import dev.idm.vkp.model.LocalPhoto;
import dev.idm.vkp.model.LocalVideo;
import dev.idm.vkp.picasso.Content_Local;
import io.reactivex.rxjava3.core.Single;


public interface ILocalMediaStorage extends IStorage {

    Single<List<LocalPhoto>> getPhotos(long albumId);

    Single<List<LocalPhoto>> getPhotos();

    Single<List<LocalImageAlbum>> getImageAlbums();

    Single<List<LocalVideo>> getVideos();

    Single<List<Audio>> getAudios(int accountId);

    @Nullable
    Bitmap getMetadataAudioThumbnail(@NonNull Uri uri, int x, int y);

    @Nullable
    Bitmap getOldThumbnail(@Content_Local int type, long content_Id);

    @Nullable
    Bitmap getThumbnail(Uri uri, int x, int y);
}
