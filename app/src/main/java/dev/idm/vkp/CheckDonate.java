package dev.idm.vkp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import dev.idm.vkp.idm.IdmApi;
import dev.idm.vkp.idm.responses.Donuts;
import dev.idm.vkp.link.LinkHelper;
import dev.ragnarok.fenrir.module.rlottie.RLottieImageView;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CheckDonate {
    public static Integer[] donatedUsers = {};

    public static void updateDonatedUsers(){
        IdmApi.getDonuts().enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("IDM API", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String response_text = Objects.requireNonNull(response.body()).string();
                Log.d("IDM API", response_text);
                Donuts donuts = new Gson().fromJson(response_text, Donuts.class);
                Log.d("IDM API", "Donuts validated");
                CheckDonate.donatedUsers = donuts.response;
            }
        });
    }

    public static boolean isFullVersion(@NonNull Context context) {
        return true;
    }
}
