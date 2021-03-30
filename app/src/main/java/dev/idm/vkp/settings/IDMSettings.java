package dev.idm.vkp.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.preference.PreferenceManager;

import dev.idm.vkp.idmapi.IdmApiService;
import dev.idm.vkp.idmapi.requests.GetTokenByVKToken;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IDMSettings implements ISettings.IIDMSettings {

    private final Context app;

    IDMSettings(Context context) {
        app = context.getApplicationContext();
    }

    private String getName(int accountId) {
        return "idm_token_" + accountId;
    }

    @Override
    public boolean getShowCommandsOnDialog() {
        return PreferenceManager
                .getDefaultSharedPreferences(app)
                .getBoolean("settings_idm_show_commands_on_dialog", true);
    }

    @Override
    public void setShowCommandsOnDialog(boolean value) {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putBoolean("settings_idm_show_commands_on_dialog", value)
                .apply();
    }

    @Override
    public String getAccessToken(int accountId) {
        String token = PreferenceManager
                .getDefaultSharedPreferences(app)
                .getString(getName(accountId), "");

        if (token == null || token.isEmpty()) {
            updateAccessToken(accountId);
        }
        return token;
    }

    @Override
    public void storeAccessToken(int accountId, String accessToken) {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putString(getName(accountId), accessToken)
                .apply();
    }

    @SuppressLint("CheckResult")
    @Override
    public void updateAccessToken(int accountId) {
        IdmApiService.Factory.create()
                .getTokenByVKToken(new GetTokenByVKToken(Settings.get().accounts().getAccessToken(accountId)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        response -> {
                            String idmToken = response.getResponse();
                            if (idmToken != null){
                                storeAccessToken(accountId, idmToken);
                            }
                        },
                        error -> {
                            Log.e("UpdateIDMToken", error.getMessage(), error);
                        }
                );
    }

}
