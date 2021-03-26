package dev.idm.vkp.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.api.Apis;
import dev.idm.vkp.api.interfaces.IAccountApis;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Optional;

import static dev.idm.vkp.settings.NotificationsPrefs.FLAG_HIGH_PRIORITY;
import static dev.idm.vkp.settings.NotificationsPrefs.FLAG_LED;
import static dev.idm.vkp.settings.NotificationsPrefs.FLAG_SHOW_NOTIF;
import static dev.idm.vkp.settings.NotificationsPrefs.FLAG_SOUND;
import static dev.idm.vkp.settings.NotificationsPrefs.FLAG_VIBRO;
import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.Utils.hasFlag;

public class DialogNotifOptionsDialog extends DialogFragment {

    protected int mask;
    private int peerId;
    private int accountId;
    private SwitchCompat scEnable;
    private SwitchCompat scHighPriority;
    private SwitchCompat scSound;
    private SwitchCompat scVibro;
    private SwitchCompat scLed;

    public static DialogNotifOptionsDialog newInstance(int aid, int peerId) {
        Bundle args = new Bundle();
        args.putInt(Extra.PEER_ID, peerId);
        args.putInt(Extra.ACCOUNT_ID, aid);
        DialogNotifOptionsDialog dialog = new DialogNotifOptionsDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountId = getArguments().getInt(Extra.ACCOUNT_ID);
        peerId = getArguments().getInt(Extra.PEER_ID);

        mask = Settings.get()
                .notifications()
                .getNotifPref(accountId, peerId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = View.inflate(requireActivity(), R.layout.dialog_dialog_options, null);

        scEnable = root.findViewById(R.id.enable);
        scHighPriority = root.findViewById(R.id.priority);
        scSound = root.findViewById(R.id.sound);
        scVibro = root.findViewById(R.id.vibro);
        scLed = root.findViewById(R.id.led);

        scEnable.setChecked(hasFlag(mask, FLAG_SHOW_NOTIF));
        scEnable.setOnCheckedChangeListener((buttonView, isChecked) -> resolveOtherSwitches());

        scSound.setChecked(hasFlag(mask, FLAG_SOUND));
        scHighPriority.setChecked(hasFlag(mask, FLAG_HIGH_PRIORITY));
        scVibro.setChecked(hasFlag(mask, FLAG_VIBRO));
        scLed.setChecked(hasFlag(mask, FLAG_LED));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.peer_notification_settings)
                .setPositiveButton(R.string.button_ok, (dialog, whichButton) -> onSaveClick())
                .setNeutralButton(R.string.set_default, (dialog, which) -> Settings.get()
                        .notifications()
                        .setDefault(accountId, peerId));

        builder.setView(root);
        resolveOtherSwitches();

        return builder.create();
    }



    private void onSaveClick() {
        int newMask = 0;
        if (scEnable.isChecked()) {
            newMask += FLAG_SHOW_NOTIF;
        }

        if (scHighPriority.isEnabled() && scHighPriority.isChecked()) {
            newMask += FLAG_HIGH_PRIORITY;
        }

        if (scSound.isEnabled() && scSound.isChecked()) {
            newMask += FLAG_SOUND;
        }

        if (scVibro.isEnabled() && scVibro.isChecked()) {
            newMask += FLAG_VIBRO;
        }

        if (scLed.isEnabled() && scLed.isChecked()) {
            newMask += FLAG_LED;
        }

        Settings.get()
                .notifications()
                .setNotifPref(accountId, peerId, newMask);
        int account_id = Settings.get().accounts().getCurrent();
        String access_token = Settings.get().accounts().getAccessToken(account_id);

        IAccountApis api = Apis.get().vkManual(account_id, access_token);
        Map<String, String> params = new HashMap<>();

        int vk_mask = 0;
        if (newMask > 0){
            vk_mask = 1;
        }

        params.put("time", "-1");
        params.put("peer_id", String.valueOf(peerId));
        params.put("sound", String.valueOf(vk_mask));
        api
                .other()
                .rawRequest("account.setSilenceMode", params)
                .subscribe(this::updateSubscribe);
    }

    private void updateSubscribe(Optional<String> object) {
    }

    private void resolveOtherSwitches() {
        boolean enable = scEnable.isChecked();
        scHighPriority.setEnabled(enable);
        scSound.setEnabled(enable);
        scVibro.setEnabled(enable);
        scLed.setEnabled(enable);
    }
}
