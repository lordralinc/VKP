package dev.idm.vkp.idm;

import android.util.Log;

import dev.idm.vkp.settings.Settings;

public class NetWorker {
    public static final okhttp3.MediaType JSON = okhttp3.MediaType.get("application/json; charset=utf-8");
    okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

    public okhttp3.Call get(String url){
        Log.d("IDM API", "Make get request to " + url);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        return client.newCall(request);
    }

    public okhttp3.Call post(String url, String params){ return post(url, params, ""); }
    public okhttp3.Call post(String url, String params, String accessToken){
        Log.d("IDM API", "Make post request to " + url + " with params " + params);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(params, JSON);
        okhttp3.Request.Builder request = new okhttp3.Request.Builder().url(url);

        if (accessToken != null && !accessToken.isEmpty()){
            request.addHeader("Authorization", "Token " + accessToken);
            Log.d("IDM API", "Use header: " + "Token " + accessToken);
        }

        return client.newCall(request.post(body).build());
    }
}
