package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dev.idm.vkp.fragment.search.SearchContentType;
import dev.idm.vkp.fragment.search.criteria.BaseSearchCriteria;
import dev.idm.vkp.model.Audio;
import dev.idm.vkp.model.AudioArtist;
import dev.idm.vkp.model.AudioPlaylist;
import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.Document;
import dev.idm.vkp.model.Link;
import dev.idm.vkp.model.Market;
import dev.idm.vkp.model.MarketAlbum;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.model.Peer;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.PhotoAlbum;
import dev.idm.vkp.model.Poll;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.model.Story;
import dev.idm.vkp.model.Video;
import dev.idm.vkp.model.WallReply;
import dev.idm.vkp.model.WikiPage;


public interface IAttachmentsPlacesView {

    void openChatWith(int accountId, int messagesOwnerId, @NonNull Peer peer);

    void openLink(int accountId, @NonNull Link link);

    void openUrl(int accountId, @NonNull String url);

    void openWikiPage(int accountId, @NonNull WikiPage page);

    void openSimplePhotoGallery(int accountId, @NonNull ArrayList<Photo> photos, int index, boolean needUpdate);

    void openPost(int accountId, @NonNull Post post);

    void goToMessagesLookupFWD(int accountId, int peerId, int messageId);

    void goWallReplyOpen(int accountId, WallReply reply);

    void openDocPreview(int accountId, @NonNull Document document);

    void openOwnerWall(int accountId, int ownerId);

    void openForwardMessages(int accountId, @NonNull ArrayList<Message> messages);

    void playAudioList(int accountId, int position, @NonNull ArrayList<Audio> apiAudio);

    void openVideo(int accountId, @NonNull Video apiVideo);

    void openHistoryVideo(int accountId, @NonNull ArrayList<Story> stories, int index);

    void openPoll(int accoountId, @NonNull Poll apiPoll);

    void openSearch(int accountId, @SearchContentType int type, @Nullable BaseSearchCriteria criteria);

    void openComments(int accountId, Commented commented, Integer focusToCommentId);

    void goToLikes(int accountId, String type, int ownerId, int id);

    void goToReposts(int accountId, String type, int ownerId, int id);

    void repostPost(int accountId, @NonNull Post post);

    void openStory(int accountId, @NotNull Story story);

    void openAudioPlaylist(int accountId, @NotNull AudioPlaylist playlist);

    void openPhotoAlbum(int accountId, @NotNull PhotoAlbum album);

    void toMarketAlbumOpen(int accountId, @NonNull MarketAlbum market_album);

    void toMarketOpen(int accountId, @NonNull Market market);

    void toArtistOpen(int accountId, @NonNull AudioArtist artist);
}
