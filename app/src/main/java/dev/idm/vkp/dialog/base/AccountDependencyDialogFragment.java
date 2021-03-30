package dev.idm.vkp.dialog.base;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import dev.idm.vkp.Extra;
import dev.idm.vkp.Injection;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.AttachmentsViewBinder;
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
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.PhotoAlbum;
import dev.idm.vkp.model.Poll;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.model.Sticker;
import dev.idm.vkp.model.Story;
import dev.idm.vkp.model.Video;
import dev.idm.vkp.model.WallReply;
import dev.idm.vkp.model.WikiPage;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.player.MusicPlaybackService;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.AppPerms;
import dev.idm.vkp.util.CustomToast;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class AccountDependencyDialogFragment extends BaseDialogFragment
        implements AttachmentsViewBinder.OnAttachmentsActionCallback {

    private static final String ARGUMENT_INVALID_ACCOUNT_CONTEXT = "invalid_account_context";
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private final AppPerms.doRequestPermissions requestWritePermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> CustomToast.CreateCustomToast(requireActivity()).showToast(R.string.permission_all_granted_text));
    private int accountId;
    private boolean supportAccountHotSwap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getArguments().containsKey(Extra.ACCOUNT_ID)) {
            throw new IllegalArgumentException("Fragments args does not constains Extra.ACCOUNT_ID");
        }

        accountId = getArguments().getInt(Extra.ACCOUNT_ID);
        mCompositeDisposable.add(Settings.get()
                .accounts()
                .observeChanges()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::fireAccountChange));
    }

    private void fireAccountChange(int newAid) {
        int oldAid = accountId;

        if (!supportAccountHotSwap) {
            if (newAid != oldAid) {
                setInvalidAccountContext(true);
                onAccountContextInvalidState();
            } else {
                setInvalidAccountContext(false);
            }

            return;
        }

        if (newAid == oldAid) return;

        beforeAccountChange(oldAid, newAid);

        accountId = newAid;
        getArguments().putInt(Extra.ACCOUNT_ID, newAid);

        afterAccountChange(oldAid, newAid);
    }

    @Override
    public void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    protected void appendDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    protected void afterAccountChange(int oldAid, int newAid) {

    }

    protected void beforeAccountChange(int oldAid, int newAid) {

    }

    protected final int getAccountId() {
        return accountId;
    }

    @Override
    public void onPollOpen(@NonNull Poll poll) {
        ///PlaceManager.withContext(getContext())
        //        .toPoll()
        //        .withArguments(PollDialog.buildArgs(getAccountId(), poll, true))
        //       .open();
    }

    @Override
    public void onVideoPlay(@NonNull Video video) {
        PlaceFactory.getVideoPreviewPlace(getAccountId(), video).tryOpenWith(requireActivity());
    }

    @Override
    public void onAudioPlay(int position, @NonNull ArrayList<Audio> audios) {
        MusicPlaybackService.startForPlayList(requireActivity(), audios, position, false);
        if (!Settings.get().other().isShowMiniPlayer())
            PlaceFactory.getPlayerPlace(Settings.get().accounts().getCurrent()).tryOpenWith(requireActivity());
    }

    @Override
    public void onForwardMessagesOpen(@NonNull ArrayList<Message> messages) {
        PlaceFactory.getForwardMessagesPlace(getAccountId(), messages).tryOpenWith(requireActivity());
    }

    @Override
    public void onOpenOwner(int userId) {
        PlaceFactory.getOwnerWallPlace(getAccountId(), userId, null).tryOpenWith(requireActivity());
    }

    @Override
    public void onGoToMessagesLookup(@NonNull Message message) {
        PlaceFactory.getMessagesLookupPlace(getAccountId(), message.getPeerId(), message.getId(), null).tryOpenWith(requireActivity());
    }

    @Override
    public void onDocPreviewOpen(@NonNull Document document) {
        PlaceFactory.getDocPreviewPlace(getAccountId(), document).tryOpenWith(requireActivity());
    }

    @Override
    public void onPostOpen(@NonNull Post post) {
        PlaceFactory.getPostPreviewPlace(getAccountId(), post.getVkid(), post.getOwnerId(), post).tryOpenWith(requireActivity());
    }

    @Override
    public void onLinkOpen(@NonNull Link link) {
        LinkHelper.openLinkInBrowser(requireActivity(), link.getUrl());
    }

    @Override
    public void onUrlOpen(@NonNull String url) {
        LinkHelper.openLinkInBrowserInternal(requireActivity(), getAccountId(), url);
    }

    @Override
    public void onWikiPageOpen(@NonNull WikiPage page) {
        PlaceFactory.getWikiPagePlace(getAccountId(), page.getViewUrl()).tryOpenWith(requireActivity());
    }

    @Override
    public void onPhotosOpen(@NonNull ArrayList<Photo> photos, int index, boolean refresh) {
        PlaceFactory.getSimpleGalleryPlace(getAccountId(), photos, index, refresh).tryOpenWith(requireActivity());
    }

    @Override
    public void onStoryOpen(@NonNull Story story) {
        PlaceFactory.getHistoryVideoPreviewPlace(getAccountId(), new ArrayList<>(Collections.singleton(story)), 0).tryOpenWith(requireActivity());
    }

    @Override
    public void onUrlPhotoOpen(@NonNull String url, @NonNull String prefix, @NonNull String photo_prefix) {
        PlaceFactory.getSingleURLPhotoPlace(url, prefix, photo_prefix).tryOpenWith(requireActivity());
    }

    @Override
    public void onAudioPlaylistOpen(@NonNull AudioPlaylist playlist) {
        PlaceFactory.getAudiosInAlbumPlace(accountId, playlist.getOwnerId(), playlist.getId(), playlist.getAccess_key()).tryOpenWith(requireActivity());
    }

    @Override
    public void onWallReplyOpen(@NonNull WallReply reply) {
        PlaceFactory.getCommentsPlace(accountId, new Commented(reply.getPostId(), reply.getOwnerId(), CommentedType.POST, null), reply.getId())
                .tryOpenWith(requireActivity());
    }

    @Override
    public void onPhotoAlbumOpen(@NotNull PhotoAlbum album) {
        PlaceFactory.getVKPhotosAlbumPlace(accountId, album.getOwnerId(), album.getId(), null).tryOpenWith(requireActivity());
    }

    @Override
    public void onMarketAlbumOpen(@NonNull MarketAlbum market_album) {
        PlaceFactory.getMarketPlace(accountId, market_album.getOwner_id(), market_album.getId()).tryOpenWith(requireActivity());
    }

    @Override
    public void onMarketOpen(@NonNull Market market) {
        PlaceFactory.getMarketViewPlace(accountId, market).tryOpenWith(requireActivity());
    }

    @Override
    public void onArtistOpen(@NonNull AudioArtist artist) {
        PlaceFactory.getArtistPlace(accountId, artist.getId(), false).tryOpenWith(requireActivity());
    }

    @Override
    public void onStickerOpen(@NonNull Sticker sticker) {

    }

    @Override
    public void onFaveArticle(@NonNull Article article) {

    }

    @Override
    public void onRequestWritePermissions() {
        requestWritePermission.launch();
    }

    protected void onAccountContextInvalidState() {
        if (isAdded() && isResumed()) {
            getParentFragmentManager().popBackStack();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInvalidAccountContext()) {
            getParentFragmentManager().popBackStack();
        }
    }

    @SuppressWarnings("unused")
    public boolean isSupportAccountHotSwap() {
        return supportAccountHotSwap;
    }

    public void setSupportAccountHotSwap(boolean supportAccountHotSwap) {
        this.supportAccountHotSwap = supportAccountHotSwap;
    }

    public boolean isInvalidAccountContext() {
        return getArguments().getBoolean(ARGUMENT_INVALID_ACCOUNT_CONTEXT);
    }

    protected void setInvalidAccountContext(boolean invalidAccountContext) {
        getArguments().putBoolean(ARGUMENT_INVALID_ACCOUNT_CONTEXT, invalidAccountContext);
    }
}
