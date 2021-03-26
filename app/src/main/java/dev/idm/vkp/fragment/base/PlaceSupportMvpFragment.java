package dev.idm.vkp.fragment.base;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import dev.idm.vkp.R;
import dev.idm.vkp.activity.SendAttachmentsActivity;
import dev.idm.vkp.adapter.AttachmentsViewBinder;
import dev.idm.vkp.adapter.listener.OwnerClickListener;
import dev.idm.vkp.dialog.PostShareDialog;
import dev.idm.vkp.domain.ILikesInteractor;
import dev.idm.vkp.fragment.search.SearchContentType;
import dev.idm.vkp.fragment.search.criteria.BaseSearchCriteria;
import dev.idm.vkp.link.LinkHelper;
import dev.idm.vkp.model.Article;
import dev.idm.vkp.model.Audio;
import dev.idm.vkp.model.AudioArtist;
import dev.idm.vkp.model.AudioPlaylist;
import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.CommentedType;
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
import dev.idm.vkp.model.Sticker;
import dev.idm.vkp.model.Story;
import dev.idm.vkp.model.Video;
import dev.idm.vkp.model.WallReply;
import dev.idm.vkp.model.WikiPage;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.presenter.base.PlaceSupportPresenter;
import dev.idm.vkp.mvp.view.IAttachmentsPlacesView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.player.MusicPlaybackService;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.AppPerms;
import dev.idm.vkp.util.AssertUtils;
import dev.idm.vkp.util.CustomToast;
import dev.idm.vkp.util.Utils;

public abstract class PlaceSupportMvpFragment<P extends PlaceSupportPresenter<V>, V extends IMvpView & IAttachmentsPlacesView & IAccountDependencyView>
        extends BaseMvpFragment<P, V> implements AttachmentsViewBinder.OnAttachmentsActionCallback, IAttachmentsPlacesView, OwnerClickListener {

    private final AppPerms.doRequestPermissions requestWritePermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> CustomToast.CreateCustomToast(requireActivity()).showToast(R.string.permission_all_granted_text));

    @Override
    public void onOwnerClick(int ownerId) {
        getPresenter().fireOwnerClick(ownerId);
    }

    @Override
    public void openChatWith(int accountId, int messagesOwnerId, @NonNull Peer peer) {
        PlaceFactory.getChatPlace(accountId, messagesOwnerId, peer).tryOpenWith(requireActivity());
    }

    @Override
    public void onPollOpen(@NonNull Poll apiPoll) {
        getPresenter().firePollClick(apiPoll);
    }

    @Override
    public void onVideoPlay(@NonNull Video video) {
        getPresenter().fireVideoClick(video);
    }

    @Override
    public void onAudioPlay(int position, @NonNull ArrayList<Audio> apiAudio) {
        getPresenter().fireAudioPlayClick(position, apiAudio);
    }

    @Override
    public void onForwardMessagesOpen(@NonNull ArrayList<Message> messages) {
        getPresenter().fireForwardMessagesClick(messages);
    }

    @Override
    public void onOpenOwner(int ownerId) {
        getPresenter().fireOwnerClick(ownerId);
    }

    @Override
    public void onGoToMessagesLookup(@NonNull Message message) {
        getPresenter().fireGoToMessagesLookup(message);
    }

    @Override
    public void goToMessagesLookupFWD(int accountId, int peerId, int messageId) {
        PlaceFactory.getMessagesLookupPlace(accountId, peerId, messageId, null).tryOpenWith(requireActivity());
    }

    @Override
    public void goWallReplyOpen(int accountId, @NonNull WallReply reply) {
        PlaceFactory.getCommentsPlace(accountId, new Commented(reply.getPostId(), reply.getOwnerId(), CommentedType.POST, null), reply.getId())
                .tryOpenWith(requireActivity());
    }

    @Override
    public void onDocPreviewOpen(@NonNull Document document) {
        getPresenter().fireDocClick(document);
    }

    @Override
    public void onPostOpen(@NonNull Post post) {
        getPresenter().firePostClick(post);
    }

    @Override
    public void onLinkOpen(@NonNull Link link) {
        getPresenter().fireLinkClick(link);
    }

    @Override
    public void onUrlOpen(@NonNull String url) {
        getPresenter().fireUrlClick(url);
    }

    @Override
    public void onFaveArticle(@NonNull Article article) {
        getPresenter().fireFaveArticleClick(article);
    }

    @Override
    public void onWikiPageOpen(@NonNull WikiPage page) {
        getPresenter().fireWikiPageClick(page);
    }

    @Override
    public void onStoryOpen(@NotNull Story story) {
        getPresenter().fireStoryClick(story);
    }

    @Override
    public void onUrlPhotoOpen(@NonNull String url, @NonNull String prefix, @NonNull String photo_prefix) {
        PlaceFactory.getSingleURLPhotoPlace(url, prefix, photo_prefix).tryOpenWith(requireActivity());
    }

    @Override
    public void openStory(int accountId, @NotNull Story story) {
        PlaceFactory.getHistoryVideoPreviewPlace(accountId, new ArrayList<>(Collections.singleton(story)), 0).tryOpenWith(requireActivity());
    }

    @Override
    public void onAudioPlaylistOpen(@NotNull AudioPlaylist playlist) {
        getPresenter().fireAudioPlaylistClick(playlist);
    }

    @Override
    public void onWallReplyOpen(@NotNull WallReply reply) {
        getPresenter().fireWallReplyOpen(reply);
    }

    @Override
    public void openAudioPlaylist(int accountId, @NotNull AudioPlaylist playlist) {
        PlaceFactory.getAudiosInAlbumPlace(accountId, playlist.getOwnerId(), playlist.getId(), playlist.getAccess_key()).tryOpenWith(requireActivity());
    }

    @Override
    public void onPhotosOpen(@NonNull ArrayList<Photo> photos, int index, boolean refresh) {
        getPresenter().firePhotoClick(photos, index, refresh);
    }

    @Override
    public void openPhotoAlbum(int accountId, @NotNull PhotoAlbum album) {
        PlaceFactory.getVKPhotosAlbumPlace(accountId, album.getOwnerId(), album.getId(), null).tryOpenWith(requireActivity());
    }

    @Override
    public void onPhotoAlbumOpen(@NotNull PhotoAlbum album) {
        getPresenter().firePhotoAlbumClick(album);
    }

    @Override
    public void onMarketAlbumOpen(@NonNull MarketAlbum market_album) {
        getPresenter().fireMarketAlbumClick(market_album);
    }

    @Override
    public void onMarketOpen(@NonNull Market market) {
        getPresenter().fireMarketClick(market);
    }

    @Override
    public void onArtistOpen(@NonNull AudioArtist artist) {
        getPresenter().fireArtistClick(artist);
    }

    @Override
    public void openLink(int accountId, @NonNull Link link) {
        LinkHelper.openLinkInBrowser(requireActivity(), link.getUrl());
    }

    @Override
    public void openUrl(int accountId, @NonNull String url) {
        LinkHelper.openLinkInBrowserInternal(requireActivity(), accountId, url);
    }

    @Override
    public void openWikiPage(int accountId, @NonNull WikiPage page) {
        PlaceFactory.getWikiPagePlace(accountId, page.getViewUrl())
                .tryOpenWith(requireActivity());
    }

    @Override
    public void onStickerOpen(@NonNull Sticker sticker) {

    }

    @Override
    public void toMarketAlbumOpen(int accountId, @NonNull MarketAlbum market_album) {
        PlaceFactory.getMarketPlace(accountId, market_album.getOwner_id(), market_album.getId()).tryOpenWith(requireActivity());
    }

    @Override
    public void toArtistOpen(int accountId, @NonNull AudioArtist artist) {
        PlaceFactory.getArtistPlace(accountId, artist.getId(), false).tryOpenWith(requireActivity());
    }

    @Override
    public void toMarketOpen(int accountId, @NonNull Market market) {
        PlaceFactory.getMarketViewPlace(accountId, market).tryOpenWith(requireActivity());
    }

    @Override
    public void openSimplePhotoGallery(int accountId, @NonNull ArrayList<Photo> photos, int index, boolean needUpdate) {
        PlaceFactory.getSimpleGalleryPlace(accountId, photos, index, needUpdate).tryOpenWith(requireActivity());
    }

    @Override
    public void openPost(int accountId, @NonNull Post post) {
        PlaceFactory.getPostPreviewPlace(accountId, post.getVkid(), post.getOwnerId(), post).tryOpenWith(requireActivity());
    }

    @Override
    public void openDocPreview(int accountId, @NonNull Document document) {
        PlaceFactory.getDocPreviewPlace(accountId, document).tryOpenWith(requireActivity());
    }

    @Override
    public void openOwnerWall(int accountId, int ownerId) {
        PlaceFactory.getOwnerWallPlace(accountId, ownerId, null).tryOpenWith(requireActivity());
    }

    @Override
    public void openForwardMessages(int accountId, @NonNull ArrayList<Message> messages) {
        PlaceFactory.getForwardMessagesPlace(accountId, messages).tryOpenWith(requireActivity());
    }

    @Override
    public void playAudioList(int accountId, int position, @NonNull ArrayList<Audio> apiAudio) {
        MusicPlaybackService.startForPlayList(requireActivity(), apiAudio, position, false);
        if (!Settings.get().other().isShow_mini_player())
            PlaceFactory.getPlayerPlace(Settings.get().accounts().getCurrent()).tryOpenWith(requireActivity());
    }

    @Override
    public void openVideo(int accountId, @NonNull Video apiVideo) {
        PlaceFactory.getVideoPreviewPlace(accountId, apiVideo).tryOpenWith(requireActivity());
    }

    @Override
    public void openHistoryVideo(int accountId, @NonNull ArrayList<Story> stories, int index) {
        PlaceFactory.getHistoryVideoPreviewPlace(accountId, stories, index).tryOpenWith(requireActivity());
    }

    @Override
    public void openPoll(int accountId, @NonNull Poll poll) {
        PlaceFactory.getPollPlace(accountId, poll)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void openComments(int accountId, Commented commented, Integer focusToCommentId) {
        PlaceFactory.getCommentsPlace(accountId, commented, focusToCommentId)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void openSearch(int accountId, @SearchContentType int type, @Nullable BaseSearchCriteria criteria) {
        PlaceFactory.getSingleTabSearchPlace(accountId, type, criteria).tryOpenWith(requireActivity());
    }

    @Override
    public void goToLikes(int accountId, String type, int ownerId, int id) {
        PlaceFactory.getLikesCopiesPlace(accountId, type, ownerId, id, ILikesInteractor.FILTER_LIKES)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void goToReposts(int accountId, String type, int ownerId, int id) {
        PlaceFactory.getLikesCopiesPlace(accountId, type, ownerId, id, ILikesInteractor.FILTER_COPIES)
                .tryOpenWith(requireActivity());
    }

    @Override
    public void repostPost(int accountId, @NonNull Post post) {
        PostShareDialog dialog = PostShareDialog.newInstance(accountId, post);
        getParentFragmentManager().setFragmentResultListener(PostShareDialog.REQUEST_POST_SHARE, dialog, (requestKey, result) -> {
            int method = PostShareDialog.extractMethod(result);
            int accountId1 = PostShareDialog.extractAccountId(result);
            Post post1 = PostShareDialog.extractPost(result);

            AssertUtils.requireNonNull(post1);

            switch (method) {
                case PostShareDialog.Methods.SHARE_LINK:
                    Utils.shareLink(requireActivity(), post1.generateVkPostLink(), post1.getText());
                    break;
                case PostShareDialog.Methods.REPOST_YOURSELF:
                    PlaceFactory.getRepostPlace(accountId1, null, post1).tryOpenWith(requireActivity());
                    break;
                case PostShareDialog.Methods.SEND_MESSAGE:
                    SendAttachmentsActivity.startForSendAttachments(requireActivity(), accountId1, post1);
                    break;
                case PostShareDialog.Methods.REPOST_GROUP:
                    int ownerId = PostShareDialog.extractOwnerId(result);
                    PlaceFactory.getRepostPlace(accountId1, Math.abs(ownerId), post1).tryOpenWith(requireActivity());
                    break;
            }
        });
        dialog.show(getParentFragmentManager(), "post-sharing");
    }

    @Override
    public void onRequestWritePermissions() {
        requestWritePermission.launch();
    }
}
