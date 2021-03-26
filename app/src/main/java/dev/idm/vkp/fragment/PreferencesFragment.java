package dev.idm.vkp.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import dev.idm.vkp.Account_Types;
import dev.idm.vkp.BuildConfig;
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
import dev.idm.vkp.activity.alias.BlackFenrirAlias;
import dev.idm.vkp.activity.alias.BlueFenrirAlias;
import dev.idm.vkp.activity.alias.DefaultFenrirAlias;
import dev.idm.vkp.activity.alias.GreenFenrirAlias;
import dev.idm.vkp.activity.alias.RedFenrirAlias;
import dev.idm.vkp.activity.alias.VKFenrirAlias;
import dev.idm.vkp.activity.alias.VioletFenrirAlias;
import dev.idm.vkp.activity.alias.WhiteFenrirAlias;
import dev.idm.vkp.activity.alias.YellowFenrirAlias;
import dev.idm.vkp.api.model.LocalServerSettings;
import dev.idm.vkp.db.DBHelper;
import dev.idm.vkp.filepicker.model.DialogConfigs;
import dev.idm.vkp.filepicker.model.DialogProperties;
import dev.idm.vkp.filepicker.view.FilePickerDialog;
import dev.idm.vkp.idm.NetWorker;
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
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static dev.idm.vkp.util.Utils.isEmpty;

public class PreferencesFragment extends PreferenceFragmentCompat {

    public static final String KEY_DEFAULT_CATEGORY = "default_category";
    public static final String KEY_AVATAR_STYLE = "avatar_style";
    private static final String KEY_APP_THEME = "app_theme";
    private static final String KEY_NIGHT_SWITCH = "night_switch";
    private static final String KEY_NOTIFICATION = "notifications";
    private static final String KEY_SECURITY = "security";
    private static final String KEY_DRAWER_ITEMS = "drawer_categories";

    private static final String TAG = PreferencesFragment.class.getSimpleName();

    private final ActivityResultLauncher<Intent> requestLightBackgound = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    changeDrawerBackground(false, result.getData());
                    //requireActivity().recreate();
                }
            });

    private final ActivityResultLauncher<Intent> requestDarkBackgound = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    changeDrawerBackground(true, result.getData());
                    //requireActivity().recreate();
                }
            });

    private final ActivityResultLauncher<Intent> requestPin = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    PlaceFactory.getSecuritySettingsPlace().tryOpenWith(requireActivity());
                }
            });

    private final AppPerms.doRequestPermissions requestContactsPermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.READ_CONTACTS},
            () -> PlaceFactory.getFriendsByPhonesPlace(getAccountId()).tryOpenWith(requireActivity()));

    private final AppPerms.doRequestPermissions requestReadPermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> CustomToast.CreateCustomToast(requireActivity()).showToast(R.string.permission_all_granted_text));

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
        if (!AppPerms.hasReadStoragePermission(getActivity())) {
            requestReadPermission.launch();
            return;
        }

        Intent intent = new Intent(getActivity(), PhotosActivity.class);
        intent.putExtra(PhotosActivity.EXTRA_MAX_SELECTION_COUNT, 1);
        if (isDark) {
            requestDarkBackgound.launch(intent);
        } else {
            requestLightBackgound.launch(intent);
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
        Preference prefLightChat = findPreference("chat_light_background");
        Preference prefDarkChat = findPreference("chat_dark_background");
        Preference prefResetPhotoChat = findPreference("reset_chat_background");
        if (prefDarkChat == null || prefLightChat == null || prefResetPhotoChat == null)
            return;
        prefDarkChat.setEnabled(bEnable);
        prefLightChat.setEnabled(bEnable);
        prefResetPhotoChat.setEnabled(bEnable);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        ListPreference nightPreference = findPreference(KEY_NIGHT_SWITCH);

        nightPreference.setOnPreferenceChangeListener((preference, newValue) -> {
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
            }

            return true;
        });

        SwitchPreference messages_menu_down = findPreference("messages_menu_down");
        messages_menu_down.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        SwitchPreference prefAmoled = findPreference("amoled_theme");
        prefAmoled.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        SwitchPreference prefMiniplayer = findPreference("show_mini_player");
        prefMiniplayer.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("vk_auth_domain").setOnPreferenceChangeListener((preference, newValue) -> {
            Injection.provideProxySettings().setActive(Injection.provideProxySettings().getActiveProxy());
            return true;
        });

        findPreference("vk_api_domain").setOnPreferenceChangeListener((preference, newValue) -> {
            Injection.provideProxySettings().setActive(Injection.provideProxySettings().getActiveProxy());
            return true;
        });

        findPreference("local_media_server").setOnPreferenceClickListener((newValue) -> {
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

        EditTextPreference prefMaxResolution = findPreference("max_bitmap_resolution");
        prefMaxResolution.setOnPreferenceChangeListener((preference, newValue) -> {
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

        SwitchPreference prefMiniplayerRoundIcon = findPreference("audio_round_icon");
        prefMiniplayerRoundIcon.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        SwitchPreference prefshow_profile_in_additional_page = findPreference("show_profile_in_additional_page");
        prefshow_profile_in_additional_page.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        SwitchPreference prefshow_recent_dialogs = findPreference("show_recent_dialogs");
        prefshow_recent_dialogs.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        SwitchPreference do_zoom_photo = findPreference("do_zoom_photo");
        do_zoom_photo.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        ListPreference font_size = findPreference("font_size");
        font_size.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        ListPreference language_ui = findPreference("language_ui");
        language_ui.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        SwitchPreference snow_mode = findPreference("snow_mode");
        snow_mode.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        ListPreference prefPhotoPreview = findPreference("photo_preview_size");
        prefPhotoPreview.setOnPreferenceChangeListener((preference, newValue) -> {
            Settings.get().main().notifyPrefPreviewSizeChanged();
            return true;
        });
        ListPreference defCategory = findPreference(KEY_DEFAULT_CATEGORY);
        initStartPagePreference(defCategory);


        Preference notification = findPreference(KEY_NOTIFICATION);
        if (notification != null) {
            notification.setOnPreferenceClickListener(preference -> {
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

        Preference security = findPreference(KEY_SECURITY);
        if (Objects.nonNull(security)) {
            security.setOnPreferenceClickListener(preference -> {
                onSecurityClick();
                return true;
            });
        }

        Preference drawerCategories = findPreference(KEY_DRAWER_ITEMS);
        if (drawerCategories != null) {
            drawerCategories.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getDrawerEditPlace().tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference avatarStyle = findPreference(KEY_AVATAR_STYLE);
        if (avatarStyle != null) {
            avatarStyle.setOnPreferenceClickListener(preference -> {
                showAvatarStyleDialog();
                return true;
            });
        }

        Preference appTheme = findPreference(KEY_APP_THEME);
        if (appTheme != null) {
            appTheme.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getSettingsThemePlace().tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference check_updates = findPreference("check_updates");
        if (check_updates != null) {
            check_updates.setOnPreferenceClickListener(preference -> {
                Toast.makeText(appTheme.getContext(), "Запущен процесс обновления", Toast.LENGTH_SHORT).show();
                new NetWorker().get("https://raw.githubusercontent.com/lordralinc/VKP/main/releases/current_vesion.json")
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Toast.makeText(appTheme.getContext(), "Ошибка при обновлении", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                try {
                                    JSONObject json = new JSONObject(response.body().string());
                                    int versionCode = BuildConfig.VERSION_CODE;
                                    if (versionCode < json.getInt("build")){
                                        String path = Environment.getExternalStorageDirectory().toString() +
                                                File.separator +
                                                Environment.DIRECTORY_DOWNLOADS +
                                                File.separator +
                                                "VKP" +
                                                File.separator +
                                                "VKP " + json.getString("version_name");

                                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                                            public void onReceive(Context ctxt, Intent intent) {
                                                Intent upl_intent = new Intent(Intent.ACTION_VIEW);
                                                Uri uri = Uri.fromFile(new File(path));
                                                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                                startActivity(upl_intent);
                                            }
                                        };

                                        DownloadManager downloadmanager = (DownloadManager) appTheme.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                        appTheme.getContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                                        Uri uri = Uri.parse(json.getString("url"));
                                        DownloadManager.Request request = new DownloadManager.Request(uri);
                                        request.setTitle("VKP");
                                        request.setDescription(getText(R.string.downloading));
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.setVisibleInDownloadsUi(false);
                                        request.setDestinationUri(Uri.parse(path));
                                        downloadmanager.enqueue(request);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
//                View view = View.inflate(requireActivity(), R.layout.dialog_about_us, null);
//                new MaterialAlertDialogBuilder(requireActivity())
//                        .setView(view)
//                        .show();
                return true;
            });
        }

        Preference version = findPreference("version");
        if (version != null) {
            version.setSummary(Utils.getAppVersionName(requireActivity()) + ", VK API " + Constants.API_VERSION);
            version.setOnPreferenceClickListener(preference -> {
                View view = View.inflate(requireActivity(), R.layout.dialog_about_us, null);
                new MaterialAlertDialogBuilder(requireActivity())
                        .setView(view)
                        .show();
                return true;
            });
        }


        Preference additional_debug = findPreference("additional_debug");
        if (additional_debug != null) {
            additional_debug.setOnPreferenceClickListener(preference -> {
                ShowAdditionalInfo();
                return true;
            });
        }

        SwitchPreference settings_idm_show_commands_on_dialog = findPreference("settings_idm_show_commands_on_dialog");
        if (settings_idm_show_commands_on_dialog != null){
            settings_idm_show_commands_on_dialog.setChecked(Settings.get().idm().getShowCommandsOnDialog());
            settings_idm_show_commands_on_dialog.setOnPreferenceChangeListener((preference, newValue) -> {
                Settings.get().idm().setShowCommandsOnDialog((boolean) newValue);
                return true;
            });
        }

        Preference update_idm_token = findPreference("update_idm_token");
        if (update_idm_token != null) {
            update_idm_token.setOnPreferenceClickListener(preference -> {
                Settings.get().idm().updateAccessToken(Settings.get().accounts().getCurrent());
                return true;
            });
        }

        Preference select_icon = findPreference("select_custom_icon");
        if (select_icon != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                select_icon.setVisible(false);
            } else {
                select_icon.setOnPreferenceClickListener(preference -> {
                    ShowSelectIcon();
                    return true;
                });
            }
        }

        ListPreference chat_background = findPreference("chat_background");
        if (chat_background != null) {
            chat_background.setOnPreferenceChangeListener((preference, newValue) -> {
                String val = newValue.toString();
                int index = Integer.parseInt(val);
                EnableChatPhotoBackground(index);
                return true;
            });
            EnableChatPhotoBackground(Integer.parseInt(chat_background.getValue()));
        }

        Preference lightSideBarPreference = findPreference("chat_light_background");
        if (lightSideBarPreference != null) {
            lightSideBarPreference.setOnPreferenceClickListener(preference -> {
                selectLocalImage(false);
                return true;
            });
            File bitmap = getDrawerBackgroundFile(requireActivity(), true);
            if (bitmap.exists()) {
                Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                lightSideBarPreference.setIcon(d);
            } else
                lightSideBarPreference.setIcon(R.drawable.dir_photo);
        }

        Preference darkSideBarPreference = findPreference("chat_dark_background");
        if (darkSideBarPreference != null) {
            darkSideBarPreference.setOnPreferenceClickListener(preference -> {
                selectLocalImage(true);
                return true;
            });
            File bitmap = getDrawerBackgroundFile(requireActivity(), false);
            if (bitmap.exists()) {
                Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                darkSideBarPreference.setIcon(d);
            } else
                darkSideBarPreference.setIcon(R.drawable.dir_photo);
        }

        Preference resetDrawerBackground = findPreference("reset_chat_background");
        if (resetDrawerBackground != null) {
            resetDrawerBackground.setOnPreferenceClickListener(preference -> {
                File chat_light = getDrawerBackgroundFile(requireActivity(), true);
                File chat_dark = getDrawerBackgroundFile(requireActivity(), false);

                try {
                    tryDeleteFile(chat_light);
                    tryDeleteFile(chat_dark);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (darkSideBarPreference != null && lightSideBarPreference != null) {
                    File bitmap = getDrawerBackgroundFile(requireActivity(), true);
                    if (bitmap.exists()) {
                        Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                        lightSideBarPreference.setIcon(d);
                    } else
                        lightSideBarPreference.setIcon(R.drawable.dir_photo);
                    bitmap = getDrawerBackgroundFile(requireActivity(), false);
                    if (bitmap.exists()) {
                        Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                        darkSideBarPreference.setIcon(d);
                    } else
                        darkSideBarPreference.setIcon(R.drawable.dir_photo);
                }
                return true;
            });
        }

        findPreference("music_dir")
                .setOnPreferenceClickListener(preference -> {
                    if (!AppPerms.hasReadStoragePermission(getActivity())) {
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

        findPreference("photo_dir")
                .setOnPreferenceClickListener(preference -> {
                    if (!AppPerms.hasReadStoragePermission(getActivity())) {
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

        findPreference("video_dir")
                .setOnPreferenceClickListener(preference -> {
                    if (!AppPerms.hasReadStoragePermission(getActivity())) {
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

        findPreference("docs_dir")
                .setOnPreferenceClickListener(preference -> {
                    if (!AppPerms.hasReadStoragePermission(getActivity())) {
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

        findPreference("sticker_dir")
                .setOnPreferenceClickListener(preference -> {
                    if (!AppPerms.hasReadStoragePermission(getActivity())) {
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

        findPreference("kate_gms_token").setVisible(Constants.DEFAULT_ACCOUNT_TYPE == Account_Types.KATE);

        findPreference("show_logs")
                .setOnPreferenceClickListener(preference -> {
                    PlaceFactory.getLogsPlace().tryOpenWith(requireActivity());
                    return true;
                });

        findPreference("request_executor")
                .setOnPreferenceClickListener(preference -> {
                    PlaceFactory.getRequestExecutorPlace(getAccountId()).tryOpenWith(requireActivity());
                    return true;
                });

        findPreference("picture_cache_cleaner")
                .setOnPreferenceClickListener(preference -> {
                    CleanImageCache(requireActivity(), true);
                    return true;
                });

        findPreference("account_cache_cleaner")
                .setOnPreferenceClickListener(preference -> {
                    DBHelper.removeDatabaseFor(requireActivity(), getAccountId());
                    CleanImageCache(requireActivity(), true);
                    return true;
                });

        findPreference("blacklist")
                .setOnPreferenceClickListener(preference -> {
                    PlaceFactory.getUserBlackListPlace(getAccountId()).tryOpenWith(requireActivity());
                    return true;
                });

        findPreference("friends_by_phone")
                .setOnPreferenceClickListener(preference -> {
                    if (!AppPerms.hasContactsPermission(requireActivity())) {
                        requestContactsPermission.launch();
                    } else {
                        PlaceFactory.getFriendsByPhonesPlace(getAccountId()).tryOpenWith(requireActivity());
                    }
                    return true;
                });

        findPreference("proxy")
                .setOnPreferenceClickListener(preference -> {
                    startActivity(new Intent(requireActivity(), ProxyManagerActivity.class));
                    return true;
                });

        SwitchPreference keepLongpoll = findPreference("keep_longpoll");
        keepLongpoll.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean keep = (boolean) newValue;
            if (keep) {
                KeepLongpollService.start(preference.getContext());
            } else {
                KeepLongpollService.stop(preference.getContext());
            }
            return true;
        });
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

    private void ShowSelectIcon() {
        if (!CheckDonate.isFullVersion(requireActivity())) {
            return;
        }
        View view = View.inflate(requireActivity(), R.layout.icon_select_alert, null);
        view.findViewById(R.id.default_icon).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        });
        view.findViewById(R.id.blue_icon).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        });
        view.findViewById(R.id.green_icon).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        });
        view.findViewById(R.id.violet_icon).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        });
        view.findViewById(R.id.red_icon).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        });
        view.findViewById(R.id.yellow_icon).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        });
        view.findViewById(R.id.black_icon).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        });
        view.findViewById(R.id.vk_official).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        });
        view.findViewById(R.id.white_icon).setOnClickListener(v -> {
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), DefaultFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlueFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), GreenFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VioletFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), RedFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), YellowFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), BlackFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), VKFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            requireActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(requireActivity(), WhiteFenrirAlias.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        });
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
