package dev.idm.vkp.domain;

import java.util.List;

import dev.idm.vkp.fragment.search.criteria.VideoSearchCriteria;
import dev.idm.vkp.model.Video;
import dev.idm.vkp.model.VideoAlbum;
import dev.idm.vkp.util.FindAt;
import dev.idm.vkp.util.Pair;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


public interface IVideosInteractor {
    Single<List<Video>> get(int accountId, int ownerId, int albumId, int count, int offset);

    Single<Pair<FindAt, List<Video>>> search_owner_video(int accountId, String q, int ownerId, int albumId, int count, int offset, int loaded);

    Single<List<Video>> getCachedVideos(int accountId, int ownerId, int albumId);

    Single<Video> getById(int accountId, int ownerId, int videoId, String accessKey, boolean cache);

    Completable addToMy(int accountId, int targetOwnerId, int videoOwnerId, int videoId);

    Single<Pair<Integer, Boolean>> likeOrDislike(int accountId, int ownerId, int videoId, String accessKey, boolean like);

    Single<Boolean> isLiked(int accountId, int ownerId, int videoId);

    Single<Integer> checkAndAddLike(int accountId, int ownerId, int videoId, String accessKey);

    Single<List<VideoAlbum>> getCachedAlbums(int accountId, int ownerId);

    Single<List<VideoAlbum>> getActualAlbums(int accountId, int ownerId, int count, int offset);

    Single<List<VideoAlbum>> getAlbumsByVideo(int accountId, int target_id, int owner_id, int video_id);

    Single<List<Video>> search(int accountId, VideoSearchCriteria criteria, int count, int offset);

    Completable edit(int accountId, Integer ownerId, int video_id, String name, String desc);

    Completable delete(int accountId, Integer videoId, Integer ownerId, Integer targetId);
}
