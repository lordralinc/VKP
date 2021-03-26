package dev.idm.vkp.settings;

import android.content.Context;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import dev.idm.vkp.idm.IdmApi;
import dev.idm.vkp.model.Token;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class IDMSettings implements ISettings.IIDMSettings {

    private final Context app;

    IDMSettings(Context context) {
        app = context.getApplicationContext();
    }

    private String getName(int accountId){
        return "idm_token_" + accountId;
    }

    @Override
    public boolean getShowCommandsOnDialog() {
        return PreferenceManager
                .getDefaultSharedPreferences(app)
                .getBoolean("idm_show_commands_on_dialog", true);
    }

    @Override
    public void setShowCommandsOnDialog(boolean value) {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putBoolean("idm_show_commands_on_dialog", value)
                .apply();
    }

    @Override
    public String getAccessToken(int accountId) {
        String token = PreferenceManager
                .getDefaultSharedPreferences(app)
                .getString(getName(accountId), "");

        if (token == null || token.isEmpty()){ updateAccessToken(accountId); }
        return token;
    }

    @Override
    public void storeAccessToken(int accountId, String accessToken) {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putString(getName(accountId), accessToken)
                .apply();
    }

    @Override
    public void updateAccessToken(int accountId) {
        IdmApi
                .getToken()
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {e.printStackTrace();}

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String response_text = Objects.requireNonNull(response.body()).string();
                        Log.d("IDM API", response_text);
                        dev.idm.vkp.idm.responses.Token token = new Gson().fromJson(response_text, dev.idm.vkp.idm.responses.Token.class);
                        Log.d("IDM API", "Token validated");
                        storeAccessToken(
                                accountId,
                                token.response
                        );
                    }
                });
    }

}
