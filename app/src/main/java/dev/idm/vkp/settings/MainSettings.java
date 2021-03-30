package dev.idm.vkp.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import dev.idm.vkp.model.PhotoSize;
import dev.idm.vkp.upload.Upload;
import dev.idm.vkp.util.Optional;

class MainSettings implements ISettings.IMainSettings {

    private static final String KEY_IMAGE_SIZE = "image_size";
    private static final String KEY_CUSTOM_TABS = "custom_tabs";

    private final Context app;

    private Optional<Integer> prefferedPhotoPreviewSize;

    MainSettings(Context context) {
        app = context.getApplicationContext();
        prefferedPhotoPreviewSize = Optional.empty();
    }

    @Override
    public boolean isSendByEnter() {
        return getDefaultPreferences().getBoolean("send_by_enter", false);
    }

    @Override
    public boolean isAmoledTheme() {
        return getDefaultPreferences().getBoolean("amoled_theme", false);
    }

    @Override
    public boolean isAudioRoundIcon() {
        return getDefaultPreferences().getBoolean("audio_round_icon", true);
    }

    @Override
    public boolean isUseLongClickDownload() {
        return getDefaultPreferences().getBoolean("use_long_click_download", false);
    }

    @Override
    public boolean isPlayerSupportVolume() {
        return getDefaultPreferences().getBoolean("is_player_support_volume", false);
    }

    @Override
    public boolean isShowBotKeyboard() {
        return getDefaultPreferences().getBoolean("show_bot_keyboard", true);
    }

    @Override
    public boolean isMyMessageNoColor() {
        return getDefaultPreferences().getBoolean("my_message_no_color", false);
    }

    @Override
    public boolean isSmoothChat() {
        return getDefaultPreferences().getBoolean("smooth_chat", false);
    }

    @Override
    public boolean isMessagesMenuDown() {
        return getDefaultPreferences().getBoolean("messages_menu_down", false);
    }

    @Nullable
    @Override
    public Integer getUploadImageSize() {
        String i = getDefaultPreferences().getString(KEY_IMAGE_SIZE, "0");
        switch (i) {
            case "1":
                return Upload.IMAGE_SIZE_800;
            case "2":
                return Upload.IMAGE_SIZE_1200;
            case "3":
                return Upload.IMAGE_SIZE_FULL;
            case "4":
                return Upload.IMAGE_SIZE_CROPPING;
            default:
                return null;
        }
    }

    @Override
    public void setUploadImageSize(Integer size) {
        getDefaultPreferences().edit().putString(KEY_IMAGE_SIZE, String.valueOf(size)).apply();
    }

    @Override
    public int getUploadImageSizePref() {
        return Integer.parseInt(Objects.requireNonNull(getDefaultPreferences().getString(KEY_IMAGE_SIZE, "0")));
    }

    @Override
    public int getStartNewsMode() {
        try {
            return Integer.parseInt(Objects.requireNonNull(getDefaultPreferences().getString("start_news", "2")));
        } catch (Exception e) {
            return 2;
        }
    }

    @Override
    public int getPrefPreviewImageSize() {
        if (prefferedPhotoPreviewSize.isEmpty()) {
            prefferedPhotoPreviewSize = Optional.wrap(restorePhotoPreviewSize());
        }

        return prefferedPhotoPreviewSize.get();
    }

    @Override
    public int cryptVersion() {
        try {
            return Integer.parseInt(Objects.requireNonNull(getDefaultPreferences().getString("crypt_version", "1")));
        } catch (Exception e) {
            return 1;
        }
    }

    @PhotoSize
    private int restorePhotoPreviewSize() {
        try {
            return Integer.parseInt(getDefaultPreferences().getString("photo_preview_size", String.valueOf(PhotoSize.X)));
        } catch (Exception e) {
            return PhotoSize.X;
        }
    }

    private SharedPreferences getDefaultPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Override
    public void notifyPrefPreviewSizeChanged() {
        prefferedPhotoPreviewSize = Optional.empty();
    }

    @PhotoSize
    @Override
    public int getPrefDisplayImageSize(@PhotoSize int byDefault) {
        return getDefaultPreferences().getInt("pref_display_photo_size", byDefault);
    }

    @Override
    public int getPhotoRoundMode() {
        return Integer.parseInt(Objects.requireNonNull(getDefaultPreferences().getString("photo_rounded_view", "0")));
    }

    @Override
    public int getFontSize() {
        return Integer.parseInt(Objects.requireNonNull(getDefaultPreferences().getString("font_size", "0")));
    }

    @Override
    public void setPrefDisplayImageSize(@PhotoSize int size) {
        getDefaultPreferences()
                .edit()
                .putInt("pref_display_photo_size", size)
                .apply();
    }

    @Override
    public boolean isCustomTabEnabled() {
        return getDefaultPreferences().getBoolean(KEY_CUSTOM_TABS, false);
    }

    @Override
    public boolean isWebViewNightMode() {
        return getDefaultPreferences().getBoolean("webview_night_mode", true);
    }

    @Override
    public boolean isLoadHistoryNotif() {
        return getDefaultPreferences().getBoolean("load_history_notif", false);
    }

    @Override
    public boolean isSnow_mode() {
        return getDefaultPreferences().getBoolean("snow_mode", false);
    }

    @Override
    public boolean isDoNotWrite() {
        return getDefaultPreferences().getBoolean("dont_write", false);
    }

    @Override
    public boolean isOverTenAttach() {
        return getDefaultPreferences().getBoolean("over_ten_attach", false);
    }
}
