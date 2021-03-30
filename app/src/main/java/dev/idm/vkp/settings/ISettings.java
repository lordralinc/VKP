package dev.idm.vkp.settings;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import dev.idm.vkp.Account_Types;
import dev.idm.vkp.api.model.LocalServerSettings;
import dev.idm.vkp.crypt.KeyLocationPolicy;
import dev.idm.vkp.model.Lang;
import dev.idm.vkp.model.PhotoSize;
import dev.idm.vkp.model.SwitchableCategory;
import dev.idm.vkp.model.drawer.RecentChat;
import dev.idm.vkp.place.Place;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;

public interface ISettings {

    IRecentChats recentChats();

    IDrawerSettings drawerSettings();

    IPushSettings pushSettings();

    ISecuritySettings security();

    IUISettings ui();

    INotificationSettings notifications();

    IMainSettings main();

    IAccountsSettings accounts();

    IIDMSettings idm();

    IOtherSettings other();

    interface IIDMSettings {
        boolean getShowCommandsOnDialog();

        void setShowCommandsOnDialog(boolean value);

        String getAccessToken(int accountId);

        void storeAccessToken(int accountId, String accessToken);

        void updateAccessToken(int accountId);
    }

    interface IOtherSettings {

        String getFeedSourceIds(int accountId);
        void setFeedSourceIds(int accountId, String sourceIds);
        void storeFeedScrollState(int accountId, String state);
        String restoreFeedScrollState(int accountId);
        String restoreFeedNextFrom(int accountId);
        void storeFeedNextFrom(int accountId, String nextFrom);

        Boolean isPinned(int peerId);
        void setPinState(int peerId, Boolean state);


        boolean getAudioBroadcastActive();
        void setAudioBroadcastActive(boolean active);

        int getMaxBitmapResolution();

        boolean isNativeParcel();

        boolean isExtraDebug();

        boolean isUseCoil();

        String getApiDomain();

        String getAuthDomain();

        boolean isUseOldVkApi();

        boolean isDisableHistory();

        boolean isShowWallCover();

        boolean isDeveloperMode();

        boolean isForceCache();

        boolean isKeepLongpoll();

        void setKeepLongpoll(boolean en);

        void setDisableErrorFCM(boolean en);

        boolean isDisabledErrorFCM();

        boolean isSettingsNoPush();

        boolean isCommentsDesc();

        boolean toggleCommentsDirection();

        boolean isInfoReading();

        boolean isAutoRead();

        boolean isNotUpdateDialogs();

        boolean isBeOnline();

        boolean isShowDonateAnim();

        int getColorChat();

        int getSecondColorChat();

        boolean isCustomChatColor();

        int getColorMyMessage();

        int getSecondColorMyMessage();

        boolean isCustomMyMessage();

        boolean isUseStopAudio();

        boolean isBlurForPlayer();

        boolean isShowMiniPlayer();

        boolean isEnableShowRecentDialogs();

        boolean isEnableShowAudioTop();

        boolean isUseInternalDownloader();

        boolean isEnableLastRead();

        boolean isNotReadShow();

        String getMusicDir();

        String getPhotoDir();

        String getVideoDir();

        String getDocDir();

        String getStickerDir();

        boolean isPhotoToUserDir();

        boolean isDeleteCacheImages();

        boolean isClickNextTrack();

        boolean isDisabledEncryption();

        boolean isDownloadPhotoTap();

        boolean isDisableSensoredVoice();

        boolean isAudioSaveModeButton();

        boolean isShowMutualCount();

        boolean isNotFriendShow();

        boolean isDoZoomPhoto();

        boolean isChangeUploadSize();

        boolean isShowPhotosLine();

        boolean isDisableLikes();

        void setDisableLikes(boolean disabled);

        boolean isDoAutoPlayVideo();

        boolean isVideoControllerToDecor();

        boolean isVideoSwipes();

        boolean isHintStickers();

        boolean isEnableNative();

        int getPaganSymbol();

        boolean isRunesShow();

        boolean isShowPaganSymbol();

        @NonNull
        String getKateGMSToken();

        int getMusicLifecycle();

        int getFFmpegPlugin();

        @Lang
        int getLanguage();

        int getEndListAnimation();

        void setSymbolSelectShow(boolean show);

        @NonNull
        LocalServerSettings getLocalServer();

        void setLocalServer(@NonNull LocalServerSettings settings);

        void setPlayer(String playerId);
        String getPlayer();
    }

    interface IAccountsSettings {
        int INVALID_ID = -1;

        Flowable<Integer> observeChanges();

        Flowable<IAccountsSettings> observeRegistered();

        List<Integer> getRegistered();

        int getCurrent();

        void setCurrent(int accountId);

        void remove(int accountId);

        void registerAccountId(int accountId, boolean setCurrent);

        void storeAccessToken(int accountId, String accessToken);

        void storeLogin(int accountId, String loginCombo);

        void removeDevice(int accountId);

        void storeDevice(int accountId, String deviceName);

        @Nullable
        String getDevice(int accountId);

        String getLogin(int accountId);

        void storeTokenType(int accountId, @Account_Types int type);

        String getAccessToken(int accountId);

        @Account_Types
        int getType(int accountId);

        void removeAccessToken(int accountId);

        void removeType(int accountId);

        void removeLogin(int accountId);
    }

    interface IMainSettings {

        boolean isSendByEnter();

        boolean isMyMessageNoColor();

        boolean isSmoothChat();

        boolean isMessagesMenuDown();

        boolean isAmoledTheme();

        boolean isAudioRoundIcon();

        boolean isUseLongClickDownload();

        boolean isShowBotKeyboard();

        boolean isPlayerSupportVolume();

        boolean isCustomTabEnabled();

        @Nullable
        Integer getUploadImageSize();

        void setUploadImageSize(Integer size);

        int getUploadImageSizePref();

        @PhotoSize
        int getPrefPreviewImageSize();

        void notifyPrefPreviewSizeChanged();

        @PhotoSize
        int getPrefDisplayImageSize(@PhotoSize int byDefault);

        int getStartNewsMode();

        void setPrefDisplayImageSize(@PhotoSize int size);

        boolean isWebViewNightMode();

        boolean isSnow_mode();

        int getPhotoRoundMode();

        int getFontSize();

        boolean isLoadHistoryNotif();

        boolean isDoNotWrite();

        boolean isOverTenAttach();

        int cryptVersion();
    }

    interface INotificationSettings {
        int FLAG_SOUND = 1;
        int FLAG_VIBRO = 2;
        int FLAG_LED = 4;
        int FLAG_SHOW_NOTIF = 8;
        int FLAG_HIGH_PRIORITY = 16;
        int FLAG_PUSH = 32;

        int getNotifPref(int aid, int peerId);

        void setDefault(int aid, int peerId);

        void setNotifPref(int aid, int peerId, int flag);

        int getOtherNotificationMask();

        boolean isCommentsNotificationsEnabled();

        boolean isFriendRequestAcceptationNotifEnabled();

        boolean isNewFollowerNotifEnabled();

        boolean isWallPublishNotifEnabled();

        boolean isGroupInvitedNotifEnabled();

        boolean isReplyNotifEnabled();

        boolean isNewPostOnOwnWallNotifEnabled();

        boolean isNewPostsNotificationEnabled();

        boolean isLikeNotificationEnable();

        Uri getFeedbackRingtoneUri();

        String getDefNotificationRingtone();

        String getNotificationRingtone();

        void setNotificationRingtoneUri(String path);

        long[] getVibrationLength();

        boolean isQuickReplyImmediately();

        boolean isBirthdayNotifEnabled();

        int getPush(int accountId, int peerId);

        void delPush(int accountId, int peerId);

        void setPush(int accountId, int peerId, int messageId);
    }

    interface IRecentChats {
        List<RecentChat> get(int accountId);

        void store(int accountId, List<RecentChat> chats);
    }

    interface IDrawerSettings {
        boolean isCategoryEnabled(@SwitchableCategory int category);

        void setCategoriesOrder(@SwitchableCategory int[] order, boolean[] active);

        int[] getCategoriesOrder();

        Observable<Object> observeChanges();
    }

    interface IPushSettings {
        void savePushRegistrations(Collection<VkPushRegistration> data);

        List<VkPushRegistration> getRegistrations();
    }

    interface ISecuritySettings {
        boolean isKeyEncryptionPolicyAccepted();

        void setKeyEncryptionPolicyAccepted(boolean accepted);

        boolean isPinValid(@NonNull int[] values);

        void setPin(@Nullable int[] pin);

        boolean isUsePinForEntrance();

        boolean isUsePinForSecurity();

        boolean isEntranceByFingerprintAllowed();

        @KeyLocationPolicy
        int getEncryptionLocationPolicy(int accountId, int peerId);

        void disableMessageEncryption(int accountId, int peerId);

        boolean isMessageEncryptionEnabled(int accountId, int peerId);

        void enableMessageEncryption(int accountId, int peerId, @KeyLocationPolicy int policy);

        void firePinAttemptNow();

        void clearPinHistory();

        List<Long> getPinEnterHistory();

        boolean hasPinHash();

        int getPinHistoryDepth();

        boolean needHideMessagesBodyForNotif();

        boolean addValueToSet(int value, String arrayName);

        boolean removeValueFromSet(int value, String arrayName);

        int getSetSize(String arrayName);

        Set<Integer> loadSet(String arrayName);

        boolean containsValuesInSet(int[] values, String arrayName);

        boolean containsValueInSet(int value, String arrayName);

        boolean getShowHiddenDialogs();

        void setShowHiddenDialogs(boolean showHiddenDialogs);

        boolean isShowHiddenAccounts();
    }

    interface IUISettings {
        @StyleRes
        int getMainTheme();

        void setMainTheme(String key);

        void switchNightMode(@NightMode int key);

        String getMainThemeKey();

        @AvatarStyle
        int getAvatarStyle();

        void storeAvatarStyle(@AvatarStyle int style);

        boolean isDarkModeEnabled(Context context);

        int getNightMode();

        Place getDefaultPage(int accountId);

        void notifyPlaceResumed(int type);

        boolean isSystemEmoji();

        boolean isEmojisFullScreen();

        boolean isStickersByTheme();

        boolean isStickersByNew();

        int isPhotoSwipeTriggeredPos();

        boolean isShowProfileInAdditionalPage();

        @SwipesChatMode
        int getSwipesChatMode();

        boolean isDisplayWriting();
    }
}
