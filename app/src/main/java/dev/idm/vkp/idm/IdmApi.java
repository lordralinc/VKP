package dev.idm.vkp.idm;

import dev.idm.vkp.settings.Settings;
import okhttp3.Call;

public class IdmApi {
    public final static String BASE_URL = "https://irisduty.ru/api/";
    public static NetWorker netWorker = new NetWorker();

    public static Call getDonuts(){
        return netWorker.get(IdmApi.BASE_URL + "users/getDonut/");
    }

    public static Call getToken() {
        return netWorker.post(
                IdmApi.BASE_URL + "auth/getTokenByVKToken/",
                "{\"access_token\":\"" + Settings.get().accounts().getAccessToken(Settings.get().accounts().getCurrent()) + "\"}"
        );
    }

    public static Call getUser(int uid) {
        return netWorker.post(
                IdmApi.BASE_URL + "users/getById/",
                "{\"id\":" + uid + "}",
                Settings.get().idm().getAccessToken(Settings.get().accounts().getCurrent())
        );
    }

}
