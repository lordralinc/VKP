package dev.idm.vkp.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.BitmapSafeResize;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import dev.idm.vkp.Account_Types;
import dev.idm.vkp.CheckDonate;
import dev.idm.vkp.Constants;
import dev.idm.vkp.Extra;
import dev.idm.vkp.Injection;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.activity.EnterPinActivity;
import dev.idm.vkp.activity.PhotosActivity;
import dev.idm.vkp.activity.ProxyManagerActivity;
import dev.idm.vkp.api.model.LocalServerSettings;
import dev.idm.vkp.db.DBHelper;
import dev.idm.vkp.filepicker.model.DialogConfigs;
import dev.idm.vkp.filepicker.model.DialogProperties;
import dev.idm.vkp.filepicker.view.FilePickerDialog;
import dev.idm.vkp.idmapi.IdmApiService;
import dev.idm.vkp.idmapi.requests.GetTokenByVKToken;
import dev.idm.vkp.listener.OnSectionResumeCallback;
import dev.idm.vkp.model.LocalPhoto;
import dev.idm.vkp.model.SwitchableCategory;
import dev.idm.vkp.picasso.PicassoInstance;
import dev.idm.vkp.picasso.transforms.EllipseTransformation;
import dev.idm.vkp.picasso.transforms.RoundTransformation;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.service.KeepLongpollService;
import dev.idm.vkp.settings.AvatarStyle;
import dev.idm.vkp.settings.ISettings;
import dev.idm.vkp.settings.NightMode;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.settings.VkPushRegistration;
import dev.idm.vkp.util.AppPerms;
import dev.idm.vkp.util.CustomToast;
import dev.idm.vkp.util.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static dev.idm.vkp.util.Utils.isEmpty;

public class PreferencesFragment extends PreferenceFragmentCompat {
    public static final String KEY_ACCOUNT_CACHE_CLEANER = "account_cache_cleaner";
    public static final String KEY_ADDITIONAL_DEBUG = "additional_debug";
    public static final String KEY_AMOLED_THEME = "amoled_theme";
    public static final String KEY_APP_THEME = "app_theme";
    public static final String KEY_AUDIO_ROUND_ICON = "audio_round_icon";
    public static final String KEY_BLACKLIST = "blacklist";
    public static final String KEY_CHAT_BACKGROUND = "chat_background";
    public static final String KEY_CHAT_DARK_BACKGROUND = "chat_dark_background";
    public static final String KEY_CHAT_LIGHT_BACKGROUND = "chat_light_background";
    public static final String KEY_CHECK_UPDATES = "check_updates";
    public static final String KEY_DEFAULT_CATEGORY = "default_category";
    public static final String KEY_DOCS_DIR = "docs_dir";
    public static final String KEY_DO_ZOOM_PHOTO = "do_zoom_photo";
    public static final String KEY_DRAWER_CATEGORIES = "drawer_categories";
    public static final String KEY_FONT_SIZE = "font_size";
    public static final String KEY_FRIENDS_BY_PHONE = "friends_by_phone";
    public static final String KEY_KATE_GMS_TOKEN = "kate_gms_token";
    public static final String KEY_KEEP_LONGPOLL = "keep_longpoll";
    public static final String KEY_LANGUAGE_UI = "language_ui";
    public static final String KEY_LOCAL_MEDIA_SERVER = "local_media_server";
    public static final String KEY_MAX_BITMAP_RESOLUTION = "max_bitmap_resolution";
    public static final String KEY_MESSAGES_MENU_DOWN = "messages_menu_down";
    public static final String KEY_MUSIC_DIR = "music_dir";
    public static final String KEY_NIGHT_SWITCH = "night_switch";
    public static final String KEY_NOTIFICATIONS = "notifications";
    public static final String KEY_PHOTO_DIR = "photo_dir";
    public static final String KEY_PHOTO_PREVIEW_SIZE = "photo_preview_size";
    public static final String KEY_PLAYER_TO_USE = "player_to_use";
    public static final String KEY_PICTURE_CACHE_CLEANER = "picture_cache_cleaner";
    public static final String KEY_PROXY = "proxy";
    public static final String KEY_REQUEST_EXECUTOR = "request_executor";
    public static final String KEY_RESET_CHAT_BACKGROUND = "reset_chat_background";
    public static final String KEY_SECURITY = "security";
    public static final String KEY_SELECT_CUSTOM_ICON = "select_custom_icon";
    public static final String KEY_SETTINGS_IDM_SHOW_COMMANDS_ON_DIALOG = "settings_idm_show_commands_on_dialog";
    public static final String KEY_SHOW_LOGS = "show_logs";
    public static final String KEY_SHOW_MINI_PLAYER = "show_mini_player";
    public static final String KEY_SHOW_MODE = "show_mode";
    public static final String KEY_SHOW_PROFILE_IN_ADDITIONAL_PAGE = "show_profile_in_additional_page";
    public static final String KEY_SHOW_RECENT_DIALOGS = "show_profile_in_additional_page";
    public static final String KEY_STICKER_DIR = "sticker_dir";
    public static final String KEY_UPDATE_IDM_TOKEN = "update_idm_token";
    public static final String KEY_UPDATE_VERIFICATIONS = "update_verifications";
    public static final String KEY_VERSION = "version";
    public static final String KEY_VIDEO_DIR = "video_dir";
    public static final String KEY_VK_API_DOMAIN = "vk_api_domain";
    public static final String KEY_VK_AUTH_DOMAIN = "vk_auth_domain";
    public static final String KEY_AVATAR_STYLE = "avatar_style";

    private static final String TAG = PreferencesFragment.class.getSimpleName();

    private final ActivityResultLauncher<Intent> requestLightBackground = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    changeDrawerBackground(false, result.getData());
                    //requireActivity().recreate();
                }
            }
    );

    private final ActivityResultLauncher<Intent> requestDarkBackground = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    changeDrawerBackground(true, result.getData());
                    //requireActivity().recreate();
                }
            }
    );

    private final ActivityResultLauncher<Intent> requestPin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    PlaceFactory.getSecuritySettingsPlace().tryOpenWith(requireActivity());
                }
            }
    );

    private final AppPerms.doRequestPermissions requestContactsPermission = AppPerms.requestPermissions(
            this,
            new String[]{Manifest.permission.READ_CONTACTS},
            () -> PlaceFactory.getFriendsByPhonesPlace(getAccountId()).tryOpenWith(requireActivity())
    );

    private final AppPerms.doRequestPermissions requestReadPermission = AppPerms.requestPermissions(
            this,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> CustomToast.CreateCustomToast(requireActivity()).showToast(R.string.permission_all_granted_text)
    );

    public static Bundle buildArgs(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        return args;
    }

    public static PreferencesFragment newInstance(Bundle args) {
        PreferencesFragment fragment = new PreferencesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static File getDrawerBackgroundFile(Context context, boolean light) {
        return new File(context.getFilesDir(), light ? "chat_light.jpg" : "chat_dark.jpg");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void CleanImageCache(Context context, boolean notify) {
        try {
            PicassoInstance.clear_cache();
            File cache = new File(context.getCacheDir(), "notif-cache");
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    new File(cache, child).delete();
                }
            }
            cache = new File(context.getCacheDir(), "lottie_network_cache");
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    new File(cache, child).delete();
                }
            }
            cache = context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES);
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    new File(cache, child).delete();
                }
            }
            if (notify)
                CustomToast.CreateCustomToast(context).showToast(R.string.success);
        } catch (IOException e) {
            e.printStackTrace();
            if (notify)
                CustomToast.CreateCustomToast(context).showToastError(e.getLocalizedMessage());
        }
    }

    private void selectLocalImage(boolean isDark) {
        if (!AppPerms.hasReadStoragePermission(requireActivity())) {
            requestReadPermission.launch();
            return;
        }

        Intent intent = new Intent(getActivity(), PhotosActivity.class);
        intent.putExtra(PhotosActivity.EXTRA_MAX_SELECTION_COUNT, 1);
        if (isDark) {
            requestDarkBackground.launch(intent);
        } else {
            requestLightBackground.launch(intent);
        }
    }

    private void EnableChatPhotoBackground(int index) {
        boolean bEnable;
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
                bEnable = false;
                break;
            default:
                bEnable = true;
                break;
        }
        Preference prefLightChat = findPreference(PreferencesFragment.KEY_CHAT_LIGHT_BACKGROUND);
        Preference prefDarkChat = findPreference(PreferencesFragment.KEY_CHAT_DARK_BACKGROUND);
        Preference prefResetPhotoChat = findPreference(PreferencesFragment.KEY_RESET_CHAT_BACKGROUND);
        if (prefDarkChat == null || prefLightChat == null || prefResetPhotoChat == null)
            return;
        prefDarkChat.setEnabled(bEnable);
        prefLightChat.setEnabled(bEnable);
        prefResetPhotoChat.setEnabled(bEnable);
    }

    private boolean vkxIsInstalled(){
        try {
            requireActivity().getPackageManager().getPackageInfo("ua.itaysonlab.vkx", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @SuppressLint("CheckResult")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        ListPreference prefNightSwitch = findPreference(PreferencesFragment.KEY_NIGHT_SWITCH);
        if (prefNightSwitch != null) {
            prefNightSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                switch (Integer.parseInt(newValue.toString())) {
                    case NightMode.DISABLE:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case NightMode.ENABLE:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case NightMode.AUTO:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                        break;
                    case NightMode.FOLLOW_SYSTEM:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                    default:
                        break;
                }

                return true;
            });
        }

        SwitchPreference prefMessagesMenuDown = findPreference(PreferencesFragment.KEY_MESSAGES_MENU_DOWN);
        if (prefMessagesMenuDown != null) {
            prefMessagesMenuDown.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        SwitchPreference prefAMOLEDTheme = findPreference(PreferencesFragment.KEY_AMOLED_THEME);
        if (prefAMOLEDTheme != null) {
            prefAMOLEDTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        SwitchPreference prefShowMiniPlayer = findPreference(PreferencesFragment.KEY_SHOW_MINI_PLAYER);
        if (prefShowMiniPlayer != null) {
            prefShowMiniPlayer.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        Preference prefVKAuthDomain = findPreference(PreferencesFragment.KEY_VK_AUTH_DOMAIN);
        if (prefVKAuthDomain != null) {
            prefVKAuthDomain.setOnPreferenceChangeListener((preference, newValue) -> {
                Injection.provideProxySettings().setActive(Injection.provideProxySettings().getActiveProxy());
                return true;
            });
        }

        Preference prefVKAPIDomain = findPreference(PreferencesFragment.KEY_VK_API_DOMAIN);
        if (prefVKAPIDomain != null) {
            prefVKAPIDomain.setOnPreferenceChangeListener((preference, newValue) -> {
                Injection.provideProxySettings().setActive(Injection.provideProxySettings().getActiveProxy());
                return true;
            });
        }

        Preference prefLocalMediaServer = findPreference(PreferencesFragment.KEY_LOCAL_MEDIA_SERVER);
        if (prefLocalMediaServer != null) {
            prefLocalMediaServer.setOnPreferenceClickListener((newValue) -> {
                if (!CheckDonate.isFullVersion(requireActivity())) {
                    return false;
                }
                View view = View.inflate(requireActivity(), R.layout.entry_local_server, null);
                TextInputEditText url = view.findViewById(R.id.edit_url);
                TextInputEditText password = view.findViewById(R.id.edit_password);
                MaterialCheckBox enabled = view.findViewById(R.id.edit_enabled);
                LocalServerSettings settings = Settings.get().other().getLocalServer();
                url.setText(settings.url);
                password.setText(settings.password);
                enabled.setChecked(settings.enabled);

                new MaterialAlertDialogBuilder(requireActivity())
                        .setView(view)
                        .setCancelable(true)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_ok, (dialog, which) -> {
                            boolean en_vl = enabled.isChecked();
                            String url_vl = url.getEditableText().toString();
                            String psv_vl = password.getEditableText().toString();
                            if (en_vl && (isEmpty(url_vl) || isEmpty(psv_vl))) {
                                return;
                            }
                            LocalServerSettings srv = new LocalServerSettings();
                            srv.enabled = en_vl;
                            srv.password = psv_vl;
                            srv.url = url_vl;
                            Settings.get().other().setLocalServer(srv);
                            Injection.provideProxySettings().setActive(Injection.provideProxySettings().getActiveProxy());
                        })
                        .show();
                return true;
            });
        }

        EditTextPreference prefMaxBitmapResolution = findPreference(PreferencesFragment.KEY_MAX_BITMAP_RESOLUTION);
        if (prefMaxBitmapResolution != null) {
            prefMaxBitmapResolution.setOnPreferenceChangeListener((preference, newValue) -> {
                int sz = -1;
                try {
                    sz = Integer.parseInt(newValue.toString().trim());
                } catch (NumberFormatException ignored) {
                }
                if (BitmapSafeResize.isOverflowCanvas(sz) || sz < 100 && sz >= 0) {
                    return false;
                } else {
                    BitmapSafeResize.setMaxResolution(sz);
                }
                requireActivity().recreate();
                return true;
            });
        }

        SwitchPreference prefAudioRoundIcon = findPreference(PreferencesFragment.KEY_AUDIO_ROUND_ICON);
        if (prefAudioRoundIcon != null) {
            prefAudioRoundIcon.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        SwitchPreference prefShowProfileInAdditionalPage = findPreference(PreferencesFragment.KEY_SHOW_PROFILE_IN_ADDITIONAL_PAGE);
        if (prefShowProfileInAdditionalPage != null) {
            prefShowProfileInAdditionalPage.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        SwitchPreference prefShowRecentDialogs = findPreference(PreferencesFragment.KEY_SHOW_RECENT_DIALOGS);
        if (prefShowRecentDialogs != null) {
            prefShowRecentDialogs.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        SwitchPreference prefDoZoomPhoto = findPreference(PreferencesFragment.KEY_DO_ZOOM_PHOTO);
        if (prefDoZoomPhoto != null) {
            prefDoZoomPhoto.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        ListPreference prefFontSize = findPreference(PreferencesFragment.KEY_FONT_SIZE);
        if (prefFontSize != null) {
            prefFontSize.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        ListPreference prefLanguageUI = findPreference(PreferencesFragment.KEY_LANGUAGE_UI);
        if (prefLanguageUI != null) {
            prefLanguageUI.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        SwitchPreference prefShowMode = findPreference(PreferencesFragment.KEY_SHOW_MODE);
        if (prefShowMode != null) {
            prefShowMode.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }

        ListPreference prefPhotoPreviewSize = findPreference(PreferencesFragment.KEY_PHOTO_PREVIEW_SIZE);
        if (prefPhotoPreviewSize != null) {
            prefPhotoPreviewSize.setOnPreferenceChangeListener((preference, newValue) -> {
                Settings.get().main().notifyPrefPreviewSizeChanged();
                return true;
            });
        }

        ListPreference prefDefaultCategory = findPreference(PreferencesFragment.KEY_DEFAULT_CATEGORY);
        if (prefDefaultCategory != null) {
            initStartPagePreference(prefDefaultCategory);
        }

        Preference prefNotifications = findPreference(PreferencesFragment.KEY_NOTIFICATIONS);
        if (prefNotifications != null) {
            prefNotifications.setOnPreferenceClickListener(preference -> {
                if (Utils.hasOreo()) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", requireContext().getPackageName());
                    requireContext().startActivity(intent);
                } else {
                    PlaceFactory.getNotificationSettingsPlace().tryOpenWith(requireActivity());
                }
                return true;
            });
        }

        Preference prefSecurity = findPreference(PreferencesFragment.KEY_SECURITY);
        if (prefSecurity != null) {
            prefSecurity.setOnPreferenceClickListener(preference -> {
                onSecurityClick();
                return true;
            });
        }

        Preference prefDrawerCategories = findPreference(PreferencesFragment.KEY_DRAWER_CATEGORIES);
        if (prefDrawerCategories != null) {
            prefDrawerCategories.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getDrawerEditPlace().tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference prefAvatarStyle = findPreference(PreferencesFragment.KEY_AVATAR_STYLE);
        if (prefAvatarStyle != null) {
            prefAvatarStyle.setOnPreferenceClickListener(preference -> {
                showAvatarStyleDialog();
                return true;
            });
        }

        Preference prefAppTheme = findPreference(PreferencesFragment.KEY_APP_THEME);
        if (prefAppTheme != null) {
            prefAppTheme.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getSettingsThemePlace().tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference check_updates = findPreference(PreferencesFragment.KEY_CHECK_UPDATES);
        if (check_updates != null) {
            check_updates.setOnPreferenceClickListener(preference -> onCheckUpdates());
        }

        Preference pefVersion = findPreference(PreferencesFragment.KEY_VERSION);
        if (pefVersion != null) {
            pefVersion.setSummary(Utils.getAppVersionName(requireActivity()) + ", VK API " + Constants.API_VERSION);
            pefVersion.setOnPreferenceClickListener(preference -> {
                View view = View.inflate(requireActivity(), R.layout.dialog_about_us, null);
                new MaterialAlertDialogBuilder(requireActivity())
                        .setView(view)
                        .show();
                return true;
            });
        }

        Preference prefAdditionalDebug = findPreference(PreferencesFragment.KEY_ADDITIONAL_DEBUG);
        if (prefAdditionalDebug != null) {
            prefAdditionalDebug.setOnPreferenceClickListener(preference -> {
                ShowAdditionalInfo();
                return true;
            });
        }

        SwitchPreference prefSettingsIDMShowCommandsOnDialog = findPreference(PreferencesFragment.KEY_SETTINGS_IDM_SHOW_COMMANDS_ON_DIALOG);
        if (prefSettingsIDMShowCommandsOnDialog != null) {
            prefSettingsIDMShowCommandsOnDialog.setChecked(Settings.get().idm().getShowCommandsOnDialog());
            prefSettingsIDMShowCommandsOnDialog.setOnPreferenceChangeListener((preference, newValue) -> {
                Settings.get().idm().setShowCommandsOnDialog((boolean) newValue);
                return true;
            });
        }

        Preference prefUpdateIDMToken = findPreference(PreferencesFragment.KEY_UPDATE_IDM_TOKEN);
        if (prefUpdateIDMToken != null) {
            int accountId = Settings.get().accounts().getCurrent();
            prefUpdateIDMToken.setOnPreferenceClickListener(preference -> {
                IdmApiService.Factory.create()
                        .getTokenByVKToken(new GetTokenByVKToken(
                                Settings.get().accounts().getAccessToken(accountId)
                                )
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                response -> {
                                    String idmToken = response.getResponse();
                                    if (idmToken != null){
                                        Settings.get().idm().storeAccessToken(accountId, idmToken);
                                        Toast.makeText(requireContext(), "Токен обновлен", Toast.LENGTH_LONG).show();
                                    }
                                    Toast.makeText(requireContext(), "Не удалось обновить токен", Toast.LENGTH_LONG).show();
                                },
                                error -> {
                                    Log.e("UpdateIDMToken", error.getMessage(), error);
                                    Toast.makeText(requireContext(), "Не удалось обновить токен", Toast.LENGTH_LONG).show();
                                }
                        );
                return true;
            });
        }

        Preference prefSelectCustomIcon = findPreference(PreferencesFragment.KEY_SELECT_CUSTOM_ICON);
        if (prefSelectCustomIcon != null) {
            prefSelectCustomIcon.setVisible(false);
        }

        ListPreference prefChatBackground = findPreference(PreferencesFragment.KEY_CHAT_BACKGROUND);
        if (prefChatBackground != null) {
            prefChatBackground.setOnPreferenceChangeListener((preference, newValue) -> {
                String val = newValue.toString();
                int index = Integer.parseInt(val);
                EnableChatPhotoBackground(index);
                return true;
            });
            EnableChatPhotoBackground(Integer.parseInt(prefChatBackground.getValue()));
        }

        Preference prefChatLightBackground = findPreference(PreferencesFragment.KEY_CHAT_LIGHT_BACKGROUND);
        if (prefChatLightBackground != null) {
            prefChatLightBackground.setOnPreferenceClickListener(preference -> {
                selectLocalImage(false);
                return true;
            });
            File bitmap = getDrawerBackgroundFile(requireActivity(), true);
            if (bitmap.exists()) {
                Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                prefChatLightBackground.setIcon(d);
            } else {
                prefChatLightBackground.setIcon(R.drawable.dir_photo);
            }
        }

        Preference prefChatDarkBackground = findPreference(PreferencesFragment.KEY_CHAT_DARK_BACKGROUND);
        if (prefChatDarkBackground != null) {
            prefChatDarkBackground.setOnPreferenceClickListener(preference -> {
                selectLocalImage(true);
                return true;
            });
            File bitmap = getDrawerBackgroundFile(requireActivity(), false);
            if (bitmap.exists()) {
                Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                prefChatDarkBackground.setIcon(d);
            } else
                prefChatDarkBackground.setIcon(R.drawable.dir_photo);
        }

        Preference prefResetChatBackground = findPreference(PreferencesFragment.KEY_RESET_CHAT_BACKGROUND);
        if (prefResetChatBackground != null) {
            prefResetChatBackground.setOnPreferenceClickListener(preference -> {
                File chat_light = getDrawerBackgroundFile(requireActivity(), true);
                File chat_dark = getDrawerBackgroundFile(requireActivity(), false);

                try {
                    tryDeleteFile(chat_light);
                    tryDeleteFile(chat_dark);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (prefChatDarkBackground != null && prefChatLightBackground != null) {
                    File bitmap = getDrawerBackgroundFile(requireActivity(), true);
                    if (bitmap.exists()) {
                        Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                        prefChatLightBackground.setIcon(d);
                    } else
                        prefChatLightBackground.setIcon(R.drawable.dir_photo);
                    bitmap = getDrawerBackgroundFile(requireActivity(), false);
                    if (bitmap.exists()) {
                        Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                        prefChatDarkBackground.setIcon(d);
                    } else
                        prefChatDarkBackground.setIcon(R.drawable.dir_photo);
                }
                return true;
            });
        }

        Preference prefMusicDir = findPreference(PreferencesFragment.KEY_MUSIC_DIR);
        if (prefMusicDir != null) {
            prefMusicDir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getMusicDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.music_dir);
                dialog.setDialogSelectionListener(files -> PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("music_dir", files[0]).apply());
                dialog.show();
                return true;
            });
        }

        Preference prefPhotoDir = findPreference(PreferencesFragment.KEY_PHOTO_DIR);
        if (prefPhotoDir != null) {
            prefPhotoDir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getPhotoDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.photo_dir);
                dialog.setDialogSelectionListener(files -> PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("photo_dir", files[0]).apply());
                dialog.show();
                return true;
            });
        }

        Preference prefVideoDir = findPreference(PreferencesFragment.KEY_VIDEO_DIR);
        if (prefVideoDir != null) {
            prefVideoDir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getVideoDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.video_dir);
                dialog.setDialogSelectionListener(files -> PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("video_dir", files[0]).apply());
                dialog.show();
                return true;
            });
        }

        Preference prefDocsDir = findPreference(PreferencesFragment.KEY_DOCS_DIR);
        if (prefDocsDir != null) {
            prefDocsDir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getDocDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.docs_dir);
                dialog.setDialogSelectionListener(files -> PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("docs_dir", files[0]).apply());
                dialog.show();
                return true;
            });
        }

        Preference prefStickerDir = findPreference(PreferencesFragment.KEY_STICKER_DIR);
        if (prefStickerDir != null) {
            prefStickerDir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getStickerDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.docs_dir);
                dialog.setDialogSelectionListener(files -> PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("sticker_dir", files[0]).apply());
                dialog.show();
                return true;
            });
        }

        Preference prefKateGMSToken = findPreference(PreferencesFragment.KEY_KATE_GMS_TOKEN);
        if (prefKateGMSToken != null) {
            prefKateGMSToken.setVisible(true);
        }

        Preference prefShowLogs = findPreference(PreferencesFragment.KEY_SHOW_LOGS);
        if (prefShowLogs != null) {
            prefShowLogs.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getLogsPlace().tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference prefRequestExecutor = findPreference(PreferencesFragment.KEY_REQUEST_EXECUTOR);
        if (prefRequestExecutor != null) {
            prefRequestExecutor.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getRequestExecutorPlace(getAccountId()).tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference prefPictureCacheCleaner = findPreference(PreferencesFragment.KEY_PICTURE_CACHE_CLEANER);
        if (prefPictureCacheCleaner != null) {
            prefPictureCacheCleaner.setOnPreferenceClickListener(preference -> {
                CleanImageCache(requireActivity(), true);
                return true;
            });
        }

        Preference prefAccountCacheCleaner = findPreference(PreferencesFragment.KEY_ACCOUNT_CACHE_CLEANER);
        if (prefAccountCacheCleaner != null) {
            prefAccountCacheCleaner.setOnPreferenceClickListener(preference -> {
                DBHelper.removeDatabaseFor(requireActivity(), getAccountId());
                CleanImageCache(requireActivity(), true);
                return true;
            });
        }

        Preference prefBlacklist = findPreference(PreferencesFragment.KEY_BLACKLIST);
        if (prefBlacklist != null) {
            prefBlacklist.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getUserBlackListPlace(getAccountId()).tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference prefFriendsByPhone = findPreference(PreferencesFragment.KEY_FRIENDS_BY_PHONE);
        if (prefFriendsByPhone != null) {
            prefFriendsByPhone.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasContactsPermission(requireActivity())) {
                    requestContactsPermission.launch();
                } else {
                    PlaceFactory.getFriendsByPhonesPlace(getAccountId()).tryOpenWith(requireActivity());
                }
                return true;
            });
        }

        Preference prefProxy = findPreference(PreferencesFragment.KEY_PROXY);
        if (prefProxy != null) {
            prefProxy.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireActivity(), ProxyManagerActivity.class));
                return true;
            });

        }

        Preference prefKeepLongpoll = findPreference(PreferencesFragment.KEY_KEEP_LONGPOLL);
        if (prefKeepLongpoll != null) {
            prefKeepLongpoll.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean keep = (boolean) newValue;
                if (keep) {
                    KeepLongpollService.start(preference.getContext());
                } else {
                    KeepLongpollService.stop(preference.getContext());
                }
                return true;
            });
        }

        ListPreference prefPlayerToUse = findPreference(PreferencesFragment.KEY_PLAYER_TO_USE);
        if (prefPlayerToUse != null) {
            boolean mayUseVKX = vkxIsInstalled();
            if (!mayUseVKX){
                prefPlayerToUse.setVisible(false);
                Settings.get().other().setPlayer("internal");
            }
            prefPlayerToUse.setOnPreferenceChangeListener((preference, newValue) -> {
                Settings.get().other().setPlayer(newValue.toString());
                return true;
            });
        }
    }

    private boolean onCheckUpdates() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=vkp_releases"));
        startActivity(intent);
        return true;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(view.findViewById(R.id.toolbar));
    }

    private void onSecurityClick() {
        if (Settings.get().security().isUsePinForSecurity()) {
            requestPin.launch(new Intent(requireActivity(), EnterPinActivity.class));
        } else {
            PlaceFactory.getSecuritySettingsPlace().tryOpenWith(requireActivity());
        }
    }

    private void tryDeleteFile(@NonNull File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("Can't delete file " + file);
        }
    }

    private void changeDrawerBackground(boolean isDark, Intent data) {
        ArrayList<LocalPhoto> photos = data.getParcelableArrayListExtra(Extra.PHOTOS);
        if (isEmpty(photos)) {
            return;
        }

        LocalPhoto photo = photos.get(0);
        boolean light = !isDark;

        File file = getDrawerBackgroundFile(requireActivity(), light);

        Bitmap original;

        try (FileOutputStream fos = new FileOutputStream(file)) {
            original = BitmapFactory.decodeFile(photo.getFullImageUri().getPath());

            original.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            Drawable d = Drawable.createFromPath(file.getAbsolutePath());
            if (light) {
                Preference lightSideBarPreference = findPreference("chat_light_background");
                if (lightSideBarPreference != null)
                    lightSideBarPreference.setIcon(d);
            } else {
                Preference darkSideBarPreference = findPreference("chat_dark_background");
                if (darkSideBarPreference != null)
                    darkSideBarPreference.setIcon(d);
            }
        } catch (IOException e) {
            CustomToast.CreateCustomToast(requireActivity()).setDuration(Toast.LENGTH_LONG).showToastError(e.getMessage());
        }
    }

    private String PushToken() {
        int accountId = Settings.get().accounts().getCurrent();

        if (accountId == ISettings.IAccountsSettings.INVALID_ID) {
            return null;
        }

        List<VkPushRegistration> available = Settings.get().pushSettings().getRegistrations();
        boolean can = available.size() == 1 && available.get(0).getUserId() == accountId;
        return can ? available.get(0).getGmcToken() : null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void ShowAdditionalInfo() {
        View view = View.inflate(requireActivity(), R.layout.dialog_additional_us, null);
        ((TextView) view.findViewById(R.id.item_user_agent)).setText("User-Agent: " + Constants.USER_AGENT(Account_Types.BY_TYPE));
        ((TextView) view.findViewById(R.id.item_device_id)).setText("Device-ID: " + Utils.getDeviceId(requireActivity()));
        ((TextView) view.findViewById(R.id.item_gcm_token)).setText("GMS-Token: " + PushToken());

        AtomicReference<String> tokenText = new AtomicReference<>("IDM Tokens");
        Settings.get().accounts().getRegistered().forEach(userId -> {
            String token = Settings.get().idm().getAccessToken(userId);
            tokenText.set(tokenText + "\n" + userId + " - " + token);
        });
        ((TextView) view.findViewById(R.id.item_tokens)).setText(tokenText.get());

        new MaterialAlertDialogBuilder(requireActivity())
                .setView(view)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void showThanks() {
        View view = View.inflate(requireActivity(), R.layout.dialog_thanks, null);

        StringBuilder agents = new StringBuilder("Агентам:\n");
        StringBuilder helpers = new StringBuilder("Помощникам:\n");
        StringBuilder donuts = new StringBuilder("Донам:\n");

        for (int i: CheckDonate.agents) {
            agents.append(CheckDonate.agents[i]).append("\n");
        }

        for (int i: CheckDonate.helpers) {
            helpers.append(CheckDonate.helpers[i]).append("\n");
        }

        for (int i: CheckDonate.donuts) {
            donuts.append(CheckDonate.donuts[i]).append("\n");
        }

        ((TextView) view.findViewById(R.id.item_agents)).setText(agents.toString());
        ((TextView) view.findViewById(R.id.item_helpers)).setText(helpers.toString());
        ((TextView) view.findViewById(R.id.item_helpers)).setText(donuts.toString());

        new MaterialAlertDialogBuilder(requireActivity())
                .setView(view)
                .show();
    }

    private void resolveAvatarStyleViews(int style, ImageView circle, ImageView oval) {
        switch (style) {
            case AvatarStyle.CIRCLE:
                circle.setVisibility(View.VISIBLE);
                oval.setVisibility(View.INVISIBLE);
                break;
            case AvatarStyle.OVAL:
                circle.setVisibility(View.INVISIBLE);
                oval.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showAvatarStyleDialog() {
        int current = Settings.get()
                .ui()
                .getAvatarStyle();

        View view = View.inflate(requireActivity(), R.layout.dialog_avatar_style, null);
        ImageView ivCircle = view.findViewById(R.id.circle_avatar);
        ImageView ivOval = view.findViewById(R.id.oval_avatar);
        ImageView ivCircleSelected = view.findViewById(R.id.circle_avatar_selected);
        ImageView ivOvalSelected = view.findViewById(R.id.oval_avatar_selected);

        ivCircle.setOnClickListener(v -> resolveAvatarStyleViews(AvatarStyle.CIRCLE, ivCircleSelected, ivOvalSelected));
        ivOval.setOnClickListener(v -> resolveAvatarStyleViews(AvatarStyle.OVAL, ivCircleSelected, ivOvalSelected));

        resolveAvatarStyleViews(current, ivCircleSelected, ivOvalSelected);

        PicassoInstance.with()
                .load(R.drawable.ava_settings)
                .transform(new RoundTransformation())
                .into(ivCircle);

        PicassoInstance.with()
                .load(R.drawable.ava_settings)
                .transform(new EllipseTransformation())
                .into(ivOval);

        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.avatar_style_title)
                .setView(view)
                .setPositiveButton(R.string.button_ok, (dialog, which) -> {
                    boolean circle = ivCircleSelected.getVisibility() == View.VISIBLE;
                    Settings.get()
                            .ui()
                            .storeAvatarStyle(circle ? AvatarStyle.CIRCLE : AvatarStyle.OVAL);
                    requireActivity().recreate();
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    private int getAccountId() {
        return requireArguments().getInt(Extra.ACCOUNT_ID);
    }

    private void initStartPagePreference(ListPreference lp) {
        ISettings.IDrawerSettings drawerSettings = Settings.get()
                .drawerSettings();

        ArrayList<String> enabledCategoriesName = new ArrayList<>();
        ArrayList<String> enabledCategoriesValues = new ArrayList<>();

        enabledCategoriesName.add(getString(R.string.last_closed_page));
        enabledCategoriesValues.add("last_closed");

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.FRIENDS)) {
            enabledCategoriesName.add(getString(R.string.friends));
            enabledCategoriesValues.add("1");
        }

        enabledCategoriesName.add(getString(R.string.dialogs));
        enabledCategoriesValues.add("2");

        enabledCategoriesName.add(getString(R.string.feed));
        enabledCategoriesValues.add("3");

        enabledCategoriesName.add(getString(R.string.drawer_feedback));
        enabledCategoriesValues.add("4");

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.GROUPS)) {
            enabledCategoriesName.add(getString(R.string.groups));
            enabledCategoriesValues.add("5");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.PHOTOS)) {
            enabledCategoriesName.add(getString(R.string.photos));
            enabledCategoriesValues.add("6");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.VIDEOS)) {
            enabledCategoriesName.add(getString(R.string.videos));
            enabledCategoriesValues.add("7");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.MUSIC)) {
            enabledCategoriesName.add(getString(R.string.music));
            enabledCategoriesValues.add("8");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.DOCS)) {
            enabledCategoriesName.add(getString(R.string.attachment_documents));
            enabledCategoriesValues.add("9");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.BOOKMARKS)) {
            enabledCategoriesName.add(getString(R.string.bookmarks));
            enabledCategoriesValues.add("10");
        }

        enabledCategoriesName.add(getString(R.string.search));
        enabledCategoriesValues.add("11");

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.NEWSFEED_COMMENTS)) {
            enabledCategoriesName.add(getString(R.string.drawer_newsfeed_comments));
            enabledCategoriesValues.add("12");
        }

        lp.setEntries(enabledCategoriesName.toArray(new CharSequence[0]));
        lp.setEntryValues(enabledCategoriesValues.toArray(new CharSequence[0]));
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.get().ui().notifyPlaceResumed(Place.PREFERENCES);

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.settings);
            actionBar.setSubtitle(null);
        }

        if (requireActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) requireActivity()).onSectionResume(AdditionalNavigationFragment.SECTION_ITEM_SETTINGS);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

}
