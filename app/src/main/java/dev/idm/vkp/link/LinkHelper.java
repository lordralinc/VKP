package dev.idm.vkp.link;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.fragment.fave.FaveTabsFragment;
import dev.idm.vkp.fragment.search.SearchContentType;
import dev.idm.vkp.fragment.search.criteria.NewsFeedCriteria;
import dev.idm.vkp.link.types.AbsLink;
import dev.idm.vkp.link.types.ArtistsLink;
import dev.idm.vkp.link.types.AudioPlaylistLink;
import dev.idm.vkp.link.types.AudioTrackLink;
import dev.idm.vkp.link.types.AudiosLink;
import dev.idm.vkp.link.types.BoardLink;
import dev.idm.vkp.link.types.DialogLink;
import dev.idm.vkp.link.types.DocLink;
import dev.idm.vkp.link.types.DomainLink;
import dev.idm.vkp.link.types.FaveLink;
import dev.idm.vkp.link.types.FeedSearchLink;
import dev.idm.vkp.link.types.OwnerLink;
import dev.idm.vkp.link.types.PageLink;
import dev.idm.vkp.link.types.PhotoAlbumLink;
import dev.idm.vkp.link.types.PhotoAlbumsLink;
import dev.idm.vkp.link.types.PhotoLink;
import dev.idm.vkp.link.types.PollLink;
import dev.idm.vkp.link.types.TopicLink;
import dev.idm.vkp.link.types.VideoLink;
import dev.idm.vkp.link.types.WallCommentLink;
import dev.idm.vkp.link.types.WallLink;
import dev.idm.vkp.link.types.WallPostLink;
import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.CommentedType;
import dev.idm.vkp.model.IdPair;
import dev.idm.vkp.model.Peer;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.mvp.view.IVkPhotosView;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.player.MusicPlaybackService;
import dev.idm.vkp.settings.CurrentTheme;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.CustomToast;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;

import static androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION;
import static dev.idm.vkp.util.Utils.isEmpty;
import static dev.idm.vkp.util.Utils.singletonArrayList;

public class LinkHelper {
    public static void openUrl(Activity context, int accountId, String link) {
        if (link == null || link.length() <= 0) {
            CustomToast.CreateCustomToast(context).showToastError(R.string.empty_clipboard_url);
            return;
        }
        if (link.contains("vk.cc")) {
            InteractorFactory.createUtilsInteractor().checkLink(accountId, link)
                    .compose(RxUtils.applySingleIOToMainSchedulers())
                    .subscribe(t -> {
                        if ("banned".equals(t.status)) {
                            Utils.showRedTopToast(context, R.string.link_banned);
                        } else {
                            if (!openVKlink(context, accountId, t.link)) {
                                PlaceFactory.getExternalLinkPlace(accountId, t.link).tryOpenWith(context);
                            }
                        }
                    }, e -> Utils.showErrorInAdapter(context, e));
        } else if (link.contains("vk.me")) {
            InteractorFactory.createUtilsInteractor().joinChatByInviteLink(accountId, link)
                    .compose(RxUtils.applySingleIOToMainSchedulers())
                    .subscribe(t -> {
                        PlaceFactory.getChatPlace(accountId, accountId, new Peer(Peer.fromChatId(t.chat_id))).tryOpenWith(context);
                    }, e -> Utils.showErrorInAdapter(context, e));
        } else {
            if (!openVKlink(context, accountId, link)) {
                PlaceFactory.getExternalLinkPlace(accountId, link).tryOpenWith(context);
            }
        }
    }

    public static boolean openVKLink(Activity activity, int accountId, AbsLink link) {
        switch (link.type) {

            case AbsLink.PLAYLIST:
                AudioPlaylistLink plLink = (AudioPlaylistLink) link;
                PlaceFactory.getAudiosInAlbumPlace(accountId, plLink.ownerId, plLink.playlistId, plLink.access_key).tryOpenWith(activity);
                break;

            case AbsLink.POLL:
                PollLink pollLink = (PollLink) link;
                openLinkInBrowser(activity, "https://vk.com/poll" + pollLink.ownerId + "_" + pollLink.Id);
                break;

            case AbsLink.WALL_COMMENT:
                WallCommentLink wallCommentLink = (WallCommentLink) link;

                Commented commented = new Commented(wallCommentLink.getPostId(), wallCommentLink.getOwnerId(), CommentedType.POST, null);
                PlaceFactory.getCommentsPlace(accountId, commented, wallCommentLink.getCommentId()).tryOpenWith(activity);
                break;

            case AbsLink.DIALOGS:
                PlaceFactory.getDialogsPlace(accountId, accountId, null).tryOpenWith(activity);
                break;

            case AbsLink.PHOTO:
                PhotoLink photoLink = (PhotoLink) link;

                Photo photo = new Photo()
                        .setId(photoLink.id)
                        .setOwnerId(photoLink.ownerId);

                PlaceFactory.getSimpleGalleryPlace(accountId, singletonArrayList(photo), 0, true).tryOpenWith(activity);
                break;

            case AbsLink.PHOTO_ALBUM:
                PhotoAlbumLink photoAlbumLink = (PhotoAlbumLink) link;
                PlaceFactory.getVKPhotosAlbumPlace(accountId, photoAlbumLink.ownerId,
                        photoAlbumLink.albumId, null).tryOpenWith(activity);
                break;

            case AbsLink.PROFILE:
            case AbsLink.GROUP:
                OwnerLink ownerLink = (OwnerLink) link;
                PlaceFactory.getOwnerWallPlace(accountId, ownerLink.ownerId, null).tryOpenWith(activity);
                break;

            case AbsLink.TOPIC:
                TopicLink topicLink = (TopicLink) link;
                PlaceFactory.getCommentsPlace(accountId, new Commented(topicLink.topicId, topicLink.ownerId,
                        CommentedType.TOPIC, null), null).tryOpenWith(activity);
                break;

            case AbsLink.WALL_POST:
                WallPostLink wallPostLink = (WallPostLink) link;
                PlaceFactory.getPostPreviewPlace(accountId, wallPostLink.postId, wallPostLink.ownerId)
                        .tryOpenWith(activity);
                break;

            case AbsLink.ALBUMS:
                PhotoAlbumsLink photoAlbumsLink = (PhotoAlbumsLink) link;
                PlaceFactory.getVKPhotoAlbumsPlace(accountId, photoAlbumsLink.ownerId, IVkPhotosView.ACTION_SHOW_PHOTOS, null).tryOpenWith(activity);
                break;

            case AbsLink.DIALOG:
                DialogLink dialogLink = (DialogLink) link;
                Peer peer = new Peer(dialogLink.peerId);
                PlaceFactory.getChatPlace(accountId, accountId, peer).tryOpenWith(activity);
                break;

            case AbsLink.WALL:
                WallLink wallLink = (WallLink) link;
                PlaceFactory.getOwnerWallPlace(accountId, wallLink.ownerId, null).tryOpenWith(activity);
                break;

            case AbsLink.VIDEO:
                VideoLink videoLink = (VideoLink) link;
                PlaceFactory.getVideoPreviewPlace(accountId, videoLink.ownerId, videoLink.videoId, null)
                        .tryOpenWith(activity);
                break;

            case AbsLink.AUDIOS:
                AudiosLink audiosLink = (AudiosLink) link;
                PlaceFactory.getAudiosPlace(accountId, audiosLink.ownerId).tryOpenWith(activity);
                break;

            case AbsLink.DOMAIN:
                DomainLink domainLink = (DomainLink) link;
                PlaceFactory.getResolveDomainPlace(accountId, domainLink.fullLink, domainLink.domain)
                        .tryOpenWith(activity);
                break;

            case AbsLink.PAGE:
                PlaceFactory.getWikiPagePlace(accountId, ((PageLink) link).getLink()).tryOpenWith(activity);
                break;

            case AbsLink.DOC:
                DocLink docLink = (DocLink) link;
                PlaceFactory.getDocPreviewPlace(accountId, docLink.docId, docLink.ownerId, null).tryOpenWith(activity);
                break;

            case AbsLink.FAVE:
                FaveLink faveLink = (FaveLink) link;
                int targetTab = FaveTabsFragment.getTabByLinkSection(faveLink.section);
                if (targetTab == FaveTabsFragment.TAB_UNKNOWN) {
                    return false;
                }

                PlaceFactory.getBookmarksPlace(accountId, targetTab).tryOpenWith(activity);
                break;

            case AbsLink.BOARD:
                BoardLink boardLink = (BoardLink) link;
                PlaceFactory.getTopicsPlace(accountId, -Math.abs(boardLink.getGroupId())).tryOpenWith(activity);
                break;

            case AbsLink.FEED_SEARCH:
                FeedSearchLink feedSearchLink = (FeedSearchLink) link;
                NewsFeedCriteria criteria = new NewsFeedCriteria(feedSearchLink.getQ());
                PlaceFactory.getSingleTabSearchPlace(accountId, SearchContentType.NEWS, criteria).tryOpenWith(activity);
                break;

            case AbsLink.ARTISTS:
                ArtistsLink artistSearchLink = (ArtistsLink) link;
                PlaceFactory.getArtistPlace(accountId, artistSearchLink.Id, false).tryOpenWith(activity);
                break;

            case AbsLink.AUDIO_TRACK:
                AudioTrackLink audioLink = (AudioTrackLink) link;
                InteractorFactory.createAudioInteractor().getById(accountId, Collections.singletonList(new IdPair(audioLink.trackId, audioLink.ownerId)))
                        .compose(RxUtils.applySingleIOToMainSchedulers())
                        .subscribe(t -> {
                            MusicPlaybackService.startForPlayList(activity, new ArrayList<>(t), 0, false);
                            PlaceFactory.getPlayerPlace(Settings.get().accounts().getCurrent()).tryOpenWith(activity);
                        }, e -> Utils.showErrorInAdapter(activity, e));
                break;

            default:
                return false;
        }

        return true;
    }

    private static boolean openVKlink(Activity activity, int accountId, String url) {
        AbsLink link = VkLinkParser.parse(url);
        return link != null && openVKLink(activity, accountId, link);
    }

    public static ArrayList<ResolveInfo> getCustomTabsPackages(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));

        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        ArrayList<ResolveInfo> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info);
            }
        }
        return packagesSupportingCustomTabs;
    }

    public static void openLinkInBrowser(Context context, String url) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(CurrentTheme.getColorPrimary(context)).setSecondaryToolbarColor(CurrentTheme.getColorSecondary(context)).build());
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        getCustomTabsPackages(context);
        if (!getCustomTabsPackages(context).isEmpty()) {
            customTabsIntent.intent.setPackage(getCustomTabsPackages(context).get(0).resolvePackageName);
        }
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    public static void openLinkInBrowserInternal(Context context, int accountId, String url) {
        if (isEmpty(url))
            return;
        PlaceFactory.getExternalLinkPlace(accountId, url).tryOpenWith(context);
    }

    public static Commented findCommentedFrom(String url) {
        AbsLink link = VkLinkParser.parse(url);
        Commented commented = null;
        if (link != null) {
            switch (link.type) {
                case AbsLink.WALL_POST:
                    WallPostLink wallPostLink = (WallPostLink) link;
                    commented = new Commented(wallPostLink.postId, wallPostLink.ownerId, CommentedType.POST, null);
                    break;
                case AbsLink.PHOTO:
                    PhotoLink photoLink = (PhotoLink) link;
                    commented = new Commented(photoLink.id, photoLink.ownerId, CommentedType.PHOTO, null);
                    break;
                case AbsLink.VIDEO:
                    VideoLink videoLink = (VideoLink) link;
                    commented = new Commented(videoLink.videoId, videoLink.ownerId, CommentedType.VIDEO, null);
                    break;
                case AbsLink.TOPIC:
                    TopicLink topicLink = (TopicLink) link;
                    commented = new Commented(topicLink.topicId, topicLink.ownerId, CommentedType.TOPIC, null);
                    break;
            }
        }

        return commented;
    }
}
