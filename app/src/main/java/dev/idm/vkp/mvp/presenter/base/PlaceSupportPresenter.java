package dev.idm.vkp.mvp.presenter.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dev.idm.vkp.domain.ILikesInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.fragment.search.SearchContentType;
import dev.idm.vkp.fragment.search.criteria.NewsFeedCriteria;
import dev.idm.vkp.model.Article;
import dev.idm.vkp.model.Audio;
import dev.idm.vkp.model.AudioArtist;
import dev.idm.vkp.model.AudioPlaylist;
import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.Document;
import dev.idm.vkp.model.Link;
import dev.idm.vkp.model.Market;
import dev.idm.vkp.model.MarketAlbum;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.PhotoAlbum;
import dev.idm.vkp.model.Poll;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.model.Story;
import dev.idm.vkp.model.Video;
import dev.idm.vkp.model.WallReply;
import dev.idm.vkp.model.WikiPage;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.IAttachmentsPlacesView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;
import dev.idm.vkp.util.RxUtils;

public abstract class PlaceSupportPresenter<V extends IMvpView & IAttachmentsPlacesView & IAccountDependencyView>
        extends AccountDependencyPresenter<V> {

    public PlaceSupportPresenter(int accountId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
    }

    public void fireLinkClick(@NonNull Link link) {
        getView().openLink(getAccountId(), link);
    }

    public void fireUrlClick(@NonNull String url) {
        getView().openUrl(getAccountId(), url);
    }

    public void fireWikiPageClick(@NonNull WikiPage page) {
        getView().openWikiPage(getAccountId(), page);
    }

    public void fireStoryClick(@NonNull Story story) {
        getView().openStory(getAccountId(), story);
    }

    public void firePhotoClick(@NonNull ArrayList<Photo> photos, int index, boolean refresh) {
        getView().openSimplePhotoGallery(getAccountId(), photos, index, refresh);
    }

    public void firePostClick(@NonNull Post post) {
        getView().openPost(getAccountId(), post);
    }

    public void fireDocClick(@NonNull Document document) {
        getView().openDocPreview(getAccountId(), document);
    }

    public void fireOwnerClick(int ownerId) {
        getView().openOwnerWall(getAccountId(), ownerId);
    }

    public void fireGoToMessagesLookup(@NonNull Message message) {
        getView().goToMessagesLookupFWD(getAccountId(), message.getPeerId(), message.getOriginalId());
    }

    public void fireGoToMessagesLookup(int peerId, int msgId) {
        getView().goToMessagesLookupFWD(getAccountId(), peerId, msgId);
    }

    public void fireForwardMessagesClick(@NonNull ArrayList<Message> messages) {
        getView().openForwardMessages(getAccountId(), messages);
    }

    public void fireAudioPlayClick(int position, @NonNull ArrayList<Audio> apiAudio) {
        getView().playAudioList(getAccountId(), position, apiAudio);
    }

    public void fireVideoClick(@NonNull Video apiVideo) {
        getView().openVideo(getAccountId(), apiVideo);
    }

    public void fireAudioPlaylistClick(@NotNull AudioPlaylist playlist) {
        getView().openAudioPlaylist(getAccountId(), playlist);
    }

    public void fireWallReplyOpen(@NotNull WallReply reply) {
        getView().goWallReplyOpen(getAccountId(), reply);
    }

    public void firePollClick(@NonNull Poll poll) {
        getView().openPoll(getAccountId(), poll);
    }

    public void fireHashtagClick(String hashTag) {
        getView().openSearch(getAccountId(), SearchContentType.NEWS, new NewsFeedCriteria(hashTag));
    }

    public void fireShareClick(Post post) {
        getView().repostPost(getAccountId(), post);
    }

    public void fireCommentsClick(Post post) {
        getView().openComments(getAccountId(), Commented.from(post), null);
    }

    public void firePhotoAlbumClick(@NotNull PhotoAlbum album) {
        getView().openPhotoAlbum(getAccountId(), album);
    }

    public void fireMarketAlbumClick(@NonNull MarketAlbum market_album) {
        getView().toMarketAlbumOpen(getAccountId(), market_album);
    }

    public void fireMarketClick(@NonNull Market market) {
        getView().toMarketOpen(getAccountId(), market);
    }

    public void fireArtistClick(@NonNull AudioArtist artist) {
        getView().toArtistOpen(getAccountId(), artist);
    }

    public void fireFaveArticleClick(@NotNull Article article) {
        if (!article.getIsFavorite()) {
            appendDisposable(InteractorFactory.createFaveInteractor().addArticle(getAccountId(), article.getURL())
                    .compose(RxUtils.applyCompletableIOToMainSchedulers())
                    .subscribe(RxUtils.dummy(), t -> {
                    }));
        } else {
            appendDisposable(InteractorFactory.createFaveInteractor().removeArticle(getAccountId(), article.getOwnerId(), article.getId())
                    .compose(RxUtils.applySingleIOToMainSchedulers())
                    .subscribe(i -> {
                    }, t -> {
                    }));
        }
    }

    public final void fireCopiesLikesClick(String type, int ownerId, int itemId, String filter) {
        if (ILikesInteractor.FILTER_LIKES.equals(filter)) {
            getView().goToLikes(getAccountId(), type, ownerId, itemId);
        } else if (ILikesInteractor.FILTER_COPIES.equals(filter)) {
            getView().goToReposts(getAccountId(), type, ownerId, itemId);
        }
    }
}
