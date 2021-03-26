package dev.idm.vkp.place;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.activity.VideoPlayerActivity;
import dev.idm.vkp.dialog.ResolveDomainDialog;
import dev.idm.vkp.fragment.AbsWallFragment;
import dev.idm.vkp.fragment.AudioCatalogFragment;
import dev.idm.vkp.fragment.AudioPlayerFragment;
import dev.idm.vkp.fragment.BrowserFragment;
import dev.idm.vkp.fragment.ChatUsersFragment;
import dev.idm.vkp.fragment.CommentsFragment;
import dev.idm.vkp.fragment.CreatePhotoAlbumFragment;
import dev.idm.vkp.fragment.CreatePollFragment;
import dev.idm.vkp.fragment.DocPreviewFragment;
import dev.idm.vkp.fragment.DocsFragment;
import dev.idm.vkp.fragment.FeedFragment;
import dev.idm.vkp.fragment.FeedbackFragment;
import dev.idm.vkp.fragment.FriendsByPhonesFragment;
import dev.idm.vkp.fragment.FwdsFragment;
import dev.idm.vkp.fragment.GifPagerFragment;
import dev.idm.vkp.fragment.LikesFragment;
import dev.idm.vkp.fragment.MarketViewFragment;
import dev.idm.vkp.fragment.MessagesLookFragment;
import dev.idm.vkp.fragment.NotReadMessagesFragment;
import dev.idm.vkp.fragment.PhotoPagerFragment;
import dev.idm.vkp.fragment.PollFragment;
import dev.idm.vkp.fragment.PreferencesFragment;
import dev.idm.vkp.fragment.SinglePhotoFragment;
import dev.idm.vkp.fragment.StoryPagerFragment;
import dev.idm.vkp.fragment.TopicsFragment;
import dev.idm.vkp.fragment.VKPhotosFragment;
import dev.idm.vkp.fragment.VideoAlbumsByVideoFragment;
import dev.idm.vkp.fragment.VideoPreviewFragment;
import dev.idm.vkp.fragment.VideosFragment;
import dev.idm.vkp.fragment.VideosTabsFragment;
import dev.idm.vkp.fragment.WallPostFragment;
import dev.idm.vkp.fragment.attachments.PostCreateFragment;
import dev.idm.vkp.fragment.attachments.RepostFragment;
import dev.idm.vkp.fragment.conversation.ConversationFragmentFactory;
import dev.idm.vkp.fragment.fave.FaveTabsFragment;
import dev.idm.vkp.fragment.friends.FriendsTabsFragment;
import dev.idm.vkp.fragment.search.SearchContentType;
import dev.idm.vkp.fragment.search.SearchTabsFragment;
import dev.idm.vkp.fragment.search.SingleTabSearchFragment;
import dev.idm.vkp.fragment.search.criteria.BaseSearchCriteria;
import dev.idm.vkp.model.AbsModel;
import dev.idm.vkp.model.Banned;
import dev.idm.vkp.model.Comment;
import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.Community;
import dev.idm.vkp.model.Document;
import dev.idm.vkp.model.EditingPostType;
import dev.idm.vkp.model.FriendsCounters;
import dev.idm.vkp.model.GroupSettings;
import dev.idm.vkp.model.LocalImageAlbum;
import dev.idm.vkp.model.Manager;
import dev.idm.vkp.model.Market;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.model.ModelsBundle;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.ParcelableOwnerWrapper;
import dev.idm.vkp.model.Peer;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.PhotoAlbum;
import dev.idm.vkp.model.PhotoAlbumEditor;
import dev.idm.vkp.model.Poll;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.model.Story;
import dev.idm.vkp.model.TmpSource;
import dev.idm.vkp.model.User;
import dev.idm.vkp.model.UserDetails;
import dev.idm.vkp.model.Video;
import dev.idm.vkp.model.WallEditorAttrs;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.Utils;

public class PlaceFactory {

    private PlaceFactory() {

    }

    public static Place getUserDetailsPlace(int accountId, @NonNull User user, @NonNull UserDetails details) {
        return new Place(Place.USER_DETAILS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withParcelableExtra(Extra.USER, user)
                .withParcelableExtra("details", details);
    }

    public static Place getDrawerEditPlace() {
        return new Place(Place.DRAWER_EDIT);
    }

    public static Place getProxyAddPlace() {
        return new Place(Place.PROXY_ADD);
    }

    public static Place getUserBlackListPlace(int accountId) {
        return new Place(Place.USER_BLACKLIST)
                .withIntExtra(Extra.ACCOUNT_ID, accountId);
    }

    public static Place getRequestExecutorPlace(int accountId) {
        return new Place(Place.REQUEST_EXECUTOR)
                .withIntExtra(Extra.ACCOUNT_ID, accountId);
    }

    public static Place getCommunityManagerEditPlace(int accountId, int groupId, Manager manager) {
        return new Place(Place.COMMUNITY_MANAGER_EDIT)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.GROUP_ID, groupId)
                .withParcelableExtra(Extra.MANAGER, manager);
    }

    public static Place getCommunityManagerAddPlace(int accountId, int groupId, ArrayList<User> users) {
        Place place = new Place(Place.COMMUNITY_MANAGER_ADD)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.GROUP_ID, groupId);

        place.getArgs().putParcelableArrayList(Extra.USERS, users);
        return place;
    }

    public static Place getTmpSourceGalleryPlace(int accountId, @NonNull TmpSource source, int index) {
        return new Place(Place.VK_PHOTO_TMP_SOURCE)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.INDEX, index)
                .withParcelableExtra(Extra.SOURCE, source);
    }

    public static Place getCommunityAddBanPlace(int accountId, int groupId, ArrayList<User> users) {
        Place place = new Place(Place.COMMUNITY_ADD_BAN)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.GROUP_ID, groupId);
        place.getArgs().putParcelableArrayList(Extra.USERS, users);
        return place;
    }

    public static Place getCommunityBanEditPlace(int accountId, int groupId, Banned banned) {
        return new Place(Place.COMMUNITY_BAN_EDIT)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.GROUP_ID, groupId)
                .withParcelableExtra(Extra.BANNED, banned);
    }

    public static Place getCommunityControlPlace(int accountId, Community community, GroupSettings settings) {
        return new Place(Place.COMMUNITY_CONTROL)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withParcelableExtra(Extra.SETTINGS, settings)
                .withParcelableExtra(Extra.OWNER, community);
    }

    public static Place getShowComunityInfoPlace(int accountId, Community community) {
        return new Place(Place.COMMUNITY_INFO)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withParcelableExtra(Extra.OWNER, community);
    }

    public static Place getShowComunityLinksInfoPlace(int accountId, Community community) {
        return new Place(Place.COMMUNITY_INFO_LINKS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withParcelableExtra(Extra.OWNER, community);
    }

    public static Place getNewsfeedCommentsPlace(int accountId) {
        return new Place(Place.NEWSFEED_COMMENTS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId);
    }

    public static Place getSingleTabSearchPlace(int accountId, @SearchContentType int type, @Nullable BaseSearchCriteria criteria) {
        return new Place(Place.SINGLE_SEARCH)
                .setArguments(SingleTabSearchFragment.buildArgs(accountId, type, criteria));
    }

    public static Place getLogsPlace() {
        return new Place(Place.LOGS);
    }

    public static Place getLocalImageAlbumPlace(LocalImageAlbum album) {
        return new Place(Place.LOCAL_IMAGE_ALBUM)
                .withParcelableExtra(Extra.ALBUM, album);
    }

    public static Place getCommentCreatePlace(int accountId, int commentId, int sourceOwnerId, String body) {
        return new Place(Place.COMMENT_CREATE)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.COMMENT_ID, commentId)
                .withIntExtra(Extra.OWNER_ID, sourceOwnerId)
                .withStringExtra(Extra.BODY, body);
    }

    public static Place getPhotoAlbumGalleryPlace(int accountId, int albumId, int ownerId, ArrayList<Photo> photos, int position) {
        return new Place(Place.VK_PHOTO_ALBUM_GALLERY)
                .setArguments(PhotoPagerFragment.buildArgsForAlbum(accountId, albumId, ownerId, photos, position));
    }

    public static Place getSimpleGalleryPlace(int accountId, ArrayList<Photo> photos, int position, boolean needRefresh) {
        return new Place(Place.SIMPLE_PHOTO_GALLERY)
                .setArguments(PhotoPagerFragment.buildArgsForSimpleGallery(accountId, position, photos, needRefresh));
    }

    public static Place getFavePhotosGallery(int accountId, ArrayList<Photo> photos, int position) {
        return new Place(Place.FAVE_PHOTOS_GALLERY)
                .setArguments(PhotoPagerFragment.buildArgsForFave(accountId, photos, position));
    }

    public static Place getCreatePollPlace(int accountId, int ownerId) {
        return new Place(Place.CREATE_POLL).setArguments(CreatePollFragment.buildArgs(accountId, ownerId));
    }

    public static Place getSettingsThemePlace() {
        return new Place(Place.SETTINGS_THEME);
    }

    public static Place getPollPlace(int accountId, @NonNull Poll poll) {
        return new Place(Place.POLL).setArguments(PollFragment.buildArgs(accountId, poll));
    }

    public static Place getGifPagerPlace(int accountId, @NonNull ArrayList<Document> documents, int index) {
        Place place = new Place(Place.GIF_PAGER);
        place.setArguments(GifPagerFragment.buildArgs(accountId, documents, index));
        return place;
    }

    public static Place getWallAttachmentsPlace(int accountId, int ownerId, String type) {
        return new Place(Place.WALL_ATTACHMENTS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.OWNER_ID, ownerId)
                .withStringExtra(Extra.TYPE, type);
    }

    public static Place getMessagesLookupPlace(int aid, int peerId, int focusMessageId, @Nullable Message message) {
        return new Place(Place.MESSAGE_LOOKUP)
                .setArguments(MessagesLookFragment.buildArgs(aid, peerId, focusMessageId, message));
    }

    public static Place getUnreadMessagesPlace(int aid, int focusMessageId, int incoming, int outgoing, int unreadCount, @NonNull Peer peer) {
        return new Place(Place.UNREAD_MESSAGES)
                .setArguments(NotReadMessagesFragment.buildArgs(aid, focusMessageId, incoming, outgoing, unreadCount, peer));
    }

    public static Place getEditPhotoAlbumPlace(int aid, @NonNull PhotoAlbum album, @NonNull PhotoAlbumEditor editor) {
        return new Place(Place.EDIT_PHOTO_ALBUM)
                .setArguments(CreatePhotoAlbumFragment.buildArgsForEdit(aid, album, editor));
    }

    public static Place getCreatePhotoAlbumPlace(int aid, int ownerId) {
        return new Place(Place.CREATE_PHOTO_ALBUM)
                .setArguments(CreatePhotoAlbumFragment.buildArgsForCreate(aid, ownerId));
    }

    public static Place getMarketAlbumPlace(int accountId, int ownerId) {
        return new Place(Place.MARKET_ALBUMS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.OWNER_ID, ownerId);
    }

    public static Place getMarketPlace(int accountId, int ownerId, int albumId) {
        return new Place(Place.MARKETS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.OWNER_ID, ownerId)
                .withIntExtra(Extra.ALBUM_ID, albumId);
    }

    public static Place getPhotoAllCommentsPlace(int accountId, int ownerId) {
        return new Place(Place.PHOTO_ALL_COMMENT)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.OWNER_ID, ownerId);
    }

    public static Place getMarketViewPlace(int accountId, @NonNull Market market) {
        return new Place(Place.MARKET_VIEW).setArguments(MarketViewFragment.buildArgs(accountId, market));
    }

    public static Place getNotificationSettingsPlace() {
        return new Place(Place.NOTIFICATION_SETTINGS);
    }

    public static Place getSecuritySettingsPlace() {
        return new Place(Place.SECURITY);
    }

    public static Place getVkInternalPlayerPlace(Video video, int size, boolean isLocal) {
        Place place = new Place(Place.VK_INTERNAL_PLAYER);
        place.prepareArguments().putParcelable(VideoPlayerActivity.EXTRA_VIDEO, video);
        place.prepareArguments().putInt(VideoPlayerActivity.EXTRA_SIZE, size);
        place.prepareArguments().putBoolean(VideoPlayerActivity.EXTRA_LOCAL, isLocal);
        return place;
    }

    public static Place getResolveDomainPlace(int aid, String url, String domain) {
        return new Place(Place.RESOLVE_DOMAIN).setArguments(ResolveDomainDialog.buildArgs(aid, url, domain));
    }

    public static Place getBookmarksPlace(int aid, int tab) {
        return new Place(Place.BOOKMARKS).setArguments(FaveTabsFragment.buildArgs(aid, tab));
    }

    public static Place getNotificationsPlace(int aid) {
        return new Place(Place.NOTIFICATIONS).setArguments(FeedbackFragment.buildArgs(aid));
    }

    public static Place getFeedPlace(int aid) {
        return new Place(Place.FEED).setArguments(FeedFragment.buildArgs(aid));
    }

    public static Place getDocumentsPlace(int aid, int ownerId, String action) {
        return new Place(Place.DOCS).setArguments(DocsFragment.buildArgs(aid, ownerId, action));
    }

    public static Place getPreferencesPlace(int aid) {
        return new Place(Place.PREFERENCES).setArguments(PreferencesFragment.buildArgs(aid));
    }

    public static Place getDialogsPlace(int accountId, int dialogsOwnerId, @Nullable String subtitle) {
        return new Place(Place.DIALOGS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.OWNER_ID, dialogsOwnerId)
                .withStringExtra(Extra.SUBTITLE, subtitle);
    }

    public static Place getChatPlace(int accountId, int messagesOwnerId, @NonNull Peer peer) {
        return new Place(Place.CHAT)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.OWNER_ID, messagesOwnerId)
                .withParcelableExtra(Extra.PEER, peer);
    }

    public static Place getVKPhotosAlbumPlace(int accountId, int ownerId, int albumId, String action) {
        return new Place(Place.VK_PHOTO_ALBUM).setArguments(VKPhotosFragment.buildArgs(accountId, ownerId, albumId, action));
    }

    public static Place getVKPhotoAlbumsPlace(int accountId, int ownerId, String action, ParcelableOwnerWrapper ownerWrapper) {
        return new Place(Place.VK_PHOTO_ALBUMS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.OWNER_ID, ownerId)
                .withStringExtra(Extra.ACTION, action)
                .withParcelableExtra(Extra.OWNER, ownerWrapper);
    }

    public static Place getWikiPagePlace(int accountId, String url) {
        return new Place(Place.WIKI_PAGE).setArguments(BrowserFragment.buildArgs(accountId, url));
    }

    public static Place getExternalLinkPlace(int accountId, String url) {
        return new Place(Place.EXTERNAL_LINK).setArguments(BrowserFragment.buildArgs(accountId, url));
    }

    public static Place getRepostPlace(int accountId, Integer gid, Post post) {
        return new Place(Place.REPOST).setArguments(RepostFragment.buildArgs(accountId, gid, post));
    }

    public static Place getEditCommentPlace(int accountId, Comment comment, Integer commemtId) {
        Place ret = new Place(Place.EDIT_COMMENT)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withParcelableExtra(Extra.COMMENT, comment);
        if (commemtId != null)
            ret.withIntExtra(Extra.COMMENT_ID, commemtId);
        return ret;
    }

    public static Place getAudiosPlace(int accountId, int ownerId) {
        return new Place(Place.AUDIOS).withIntExtra(Extra.ACCOUNT_ID, accountId).withIntExtra(Extra.OWNER_ID, ownerId);
    }

    public static Place getAudiosInCatalogBlock(int accountId, String block_Id, String title) {
        return new Place(Place.CATALOG_BLOCK_AUDIOS).withIntExtra(Extra.ACCOUNT_ID, accountId).withStringExtra(Extra.ID, block_Id).withStringExtra(Extra.TITLE, title);
    }

    public static Place getPlaylistsInCatalogBlock(int accountId, String block_Id, String title) {
        return new Place(Place.CATALOG_BLOCK_PLAYLISTS).withIntExtra(Extra.ACCOUNT_ID, accountId).withStringExtra(Extra.ID, block_Id).withStringExtra(Extra.TITLE, title);
    }

    public static Place getVideosInCatalogBlock(int accountId, String block_Id, String title) {
        return new Place(Place.CATALOG_BLOCK_VIDEOS).withIntExtra(Extra.ACCOUNT_ID, accountId).withStringExtra(Extra.ID, block_Id).withStringExtra(Extra.TITLE, title);
    }

    public static Place getLinksInCatalogBlock(int accountId, String block_Id, String title) {
        return new Place(Place.CATALOG_BLOCK_LINKS).withIntExtra(Extra.ACCOUNT_ID, accountId).withStringExtra(Extra.ID, block_Id).withStringExtra(Extra.TITLE, title);
    }

    public static Place getShortLinks(int accountId) {
        return new Place(Place.SHORT_LINKS).withIntExtra(Extra.ACCOUNT_ID, accountId);
    }

    public static Place getImportantMessages(int accountId) {
        return new Place(Place.IMPORTANT_MESSAGES).withIntExtra(Extra.ACCOUNT_ID, accountId);
    }

    public static Place getOwnerArticles(int accountId, int ownerId) {
        return new Place(Place.OWNER_ARTICLES).withIntExtra(Extra.ACCOUNT_ID, accountId).withIntExtra(Extra.OWNER_ID, ownerId);
    }

    public static Place getMentionsPlace(int accountId, int ownerId) {
        return new Place(Place.MENTIONS).withIntExtra(Extra.ACCOUNT_ID, accountId).withIntExtra(Extra.OWNER_ID, ownerId);
    }

    public static Place getAudiosInAlbumPlace(int accountId, int ownerId, int album_id, String access_key) {
        return new Place(Place.AUDIOS_IN_ALBUM).withIntExtra(Extra.ACCOUNT_ID, accountId).withIntExtra(Extra.OWNER_ID, ownerId).withIntExtra(Extra.ID, album_id).withStringExtra(Extra.ACCESS_KEY, access_key);
    }

    public static Place SearchByAudioPlace(int accountId, int audio_ownerId, int audio_id) {
        return new Place(Place.SEARCH_BY_AUDIO).withIntExtra(Extra.ACCOUNT_ID, accountId).withIntExtra(Extra.OWNER_ID, audio_ownerId).withIntExtra(Extra.ID, audio_id);
    }

    public static Place getPlayerPlace(int accountId) {
        return new Place(Place.PLAYER).setArguments(AudioPlayerFragment.buildArgs(accountId));
    }

    public static Place getVideosPlace(int accountId, int ownerId, String action) {
        return new Place(Place.VIDEOS).setArguments(VideosTabsFragment.buildArgs(accountId, ownerId, action));
    }

    public static Place getVideoAlbumPlace(int accountId, int ownerId, int albumId, String action, @Nullable String albumTitle) {
        return new Place(Place.VIDEO_ALBUM)
                .setArguments(VideosFragment.buildArgs(accountId, ownerId, albumId, action, albumTitle));
    }

    public static Place getVideoPreviewPlace(int accountId, @NonNull Video video) {
        return new Place(Place.VIDEO_PREVIEW)
                .setArguments(VideoPreviewFragment.buildArgs(accountId, video.getOwnerId(), video.getId(), video));
    }

    public static Place getHistoryVideoPreviewPlace(int accountId, @NonNull ArrayList<Story> stories, int index) {
        return new Place(Place.STORY_PLAYER)
                .setArguments(StoryPagerFragment.buildArgs(accountId, stories, index));
    }

    public static Place getVideoPreviewPlace(int accountId, int ownerId, int videoId, @Nullable Video video) {
        return new Place(Place.VIDEO_PREVIEW)
                .setArguments(VideoPreviewFragment.buildArgs(accountId, ownerId, videoId, video));
    }

    public static Place getSingleURLPhotoPlace(String url, String prefix, String photo_prefix) {
        return new Place(Place.SINGLE_PHOTO)
                .setArguments(SinglePhotoFragment.buildArgs(url, prefix, photo_prefix));
    }

    public static Place getLikesCopiesPlace(int accountId, String type, int ownerId, int itemId, String filter) {
        return new Place(Place.LIKES_AND_COPIES)
                .setArguments(LikesFragment.buildArgs(accountId, type, ownerId, itemId, filter));
    }

    public static Place getCommunitiesPlace(int accountId, int userId) {
        return new Place(Place.COMMUNITIES)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.USER_ID, userId);
    }

    public static Place getFriendsFollowersPlace(int accountId, int userId, int tab, FriendsCounters counters) {
        return new Place(Place.FRIENDS_AND_FOLLOWERS)
                .setArguments(FriendsTabsFragment.buildArgs(accountId, userId, tab, counters));
    }

    public static Place getChatMembersPlace(int accountId, int chatId) {
        return new Place(Place.CHAT_MEMBERS).setArguments(ChatUsersFragment.buildArgs(accountId, chatId));
    }

    public static Place getOwnerWallPlace(int accountId, @NonNull Owner owner) {
        int ownerId = owner.getOwnerId();
        return getOwnerWallPlace(accountId, ownerId, owner);
    }

    public static Place getOwnerWallPlace(int accountId, int ownerId, @Nullable Owner owner) {
        return new Place(Place.WALL).setArguments(AbsWallFragment.buildArgs(accountId, ownerId, owner));
    }

    public static Place getTopicsPlace(int accountId, int ownerId) {
        return new Place(Place.TOPICS).setArguments(TopicsFragment.buildArgs(accountId, ownerId));
    }

    public static Place getSearchPlace(int accountId, int tab) {
        return new Place(Place.SEARCH).setArguments(SearchTabsFragment.buildArgs(accountId, tab));
    }

    public static Place getCreatePostPlace(int accountId, int ownerId, @EditingPostType int editingType,
                                           @Nullable List<AbsModel> input, @NonNull WallEditorAttrs attrs,
                                           @Nullable ArrayList<Uri> streams, @Nullable String body, @Nullable String mime) {
        ModelsBundle bundle = new ModelsBundle(Utils.safeCountOf(input));
        if (Objects.nonNull(input)) {
            bundle.append(input);
        }

        return new Place(Place.BUILD_NEW_POST)
                .setArguments(PostCreateFragment.buildArgs(accountId, ownerId, editingType, bundle, attrs, streams, body, mime));
    }

    public static Place getForwardMessagesPlace(int accountId, ArrayList<Message> messages) {
        return new Place(Place.FORWARD_MESSAGES).setArguments(FwdsFragment.buildArgs(accountId, messages));
    }

    public static Place getEditPostPlace(int accountId, @NonNull Post post, @NonNull WallEditorAttrs attrs) {
        return new Place(Place.EDIT_POST)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withParcelableExtra(Extra.POST, post)
                .withParcelableExtra(Extra.ATTRS, attrs);
    }

    public static Place getPostPreviewPlace(int accountId, int postId, int ownerId) {
        return getPostPreviewPlace(accountId, postId, ownerId, null);
    }

    public static Place getPostPreviewPlace(int accountId, int postId, int ownerId, Post post) {
        return new Place(Place.WALL_POST)
                .setArguments(WallPostFragment.buildArgs(accountId, postId, ownerId, post));
    }

    public static Place getAlbumsByVideoPlace(int accountId, int ownerId, int video_ownerId, int video_Id) {
        return new Place(Place.ALBUMS_BY_VIDEO)
                .setArguments(VideoAlbumsByVideoFragment.buildArgs(accountId, ownerId, video_ownerId, video_Id));
    }

    public static Place getDocPreviewPlace(int accountId, int docId, int ownerId, @Nullable Document document) {
        Place place = new Place(Place.DOC_PREVIEW);
        place.setArguments(DocPreviewFragment.buildArgs(accountId, docId, ownerId, document));
        return place;
    }

    public static Place getDocPreviewPlace(int accountId, @NonNull Document document) {
        return getDocPreviewPlace(accountId, document.getId(), document.getOwnerId(), document);
    }

    public static Place getConversationAttachmentsPlace(int accountId, int peerId, String type) {
        return new Place(Place.CONVERSATION_ATTACHMENTS)
                .setArguments(ConversationFragmentFactory.buildArgs(accountId, peerId, type));
    }

    public static Place getCommentsPlace(int accountId, Commented commented, Integer focusToCommentId) {
        return new Place(Place.COMMENTS)
                .setArguments(CommentsFragment.buildArgs(accountId, commented, focusToCommentId, null));
    }

    public static Place getCommentsThreadPlace(int accountId, Commented commented, Integer focusToCommentId, Integer commemtId) {
        return new Place(Place.COMMENTS)
                .setArguments(CommentsFragment.buildArgs(accountId, commented, focusToCommentId, commemtId));
    }

    public static Place getArtistPlace(int accountId, String id, boolean isHideToolbar) {
        return new Place(Place.ARTIST)
                .setArguments(AudioCatalogFragment.buildArgs(accountId, id, isHideToolbar));
    }

    public static Place getFriendsByPhonesPlace(int accountId) {
        return new Place(Place.FRIENDS_BY_PHONES)
                .setArguments(FriendsByPhonesFragment.buildArgs(accountId));
    }

    public static Place getGiftsPlace(int accountId, int ownerId) {
        return new Place(Place.GIFTS)
                .withIntExtra(Extra.ACCOUNT_ID, accountId)
                .withIntExtra(Extra.OWNER_ID, ownerId);
    }
}
