package dev.idm.vkp.api;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import dev.idm.vkp.Account_Types;
import dev.idm.vkp.Constants;
import dev.idm.vkp.api.adapters.LongpollUpdateAdapter;
import dev.idm.vkp.api.adapters.LongpollUpdatesAdapter;
import dev.idm.vkp.api.model.LocalServerSettings;
import dev.idm.vkp.api.model.longpoll.AbsLongpollEvent;
import dev.idm.vkp.api.model.longpoll.VkApiLongpollUpdates;
import dev.idm.vkp.settings.IProxySettings;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static dev.idm.vkp.util.Objects.nonNull;


public class OtherVkRetrofitProvider implements IOtherVkRetrofitProvider {

    private final IProxySettings proxySettings;
    private final Object longpollRetrofitLock = new Object();
    private final Object localServerRetrofitLock = new Object();
    private RetrofitWrapper longpollRetrofitInstance;
    private RetrofitWrapper localServerRetrofitInstance;

    @SuppressLint("CheckResult")
    public OtherVkRetrofitProvider(IProxySettings proxySettings) {
        this.proxySettings = proxySettings;
        this.proxySettings.observeActive()
                .subscribe(ignored -> onProxySettingsChanged());
    }

    private void onProxySettingsChanged() {
        synchronized (longpollRetrofitLock) {
            if (nonNull(longpollRetrofitInstance)) {
                longpollRetrofitInstance.cleanup();
                longpollRetrofitInstance = null;
            }
        }
        synchronized (localServerRetrofitLock) {
            if (nonNull(localServerRetrofitInstance)) {
                localServerRetrofitInstance.cleanup();
                localServerRetrofitInstance = null;
            }
        }
    }

    @Override
    public Single<RetrofitWrapper> provideAuthRetrofit() {
        return Single.fromCallable(() -> {

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(HttpLogger.DEFAULT_LOGGING_INTERCEPTOR).addInterceptor(chain -> {
                        Request request = chain.request().newBuilder().addHeader("User-Agent", Constants.USER_AGENT(Constants.DEFAULT_ACCOUNT_TYPE)).build();
                        return chain.proceed(request);
                    });

            ProxyUtil.applyProxyConfig(builder, proxySettings.getActiveProxy());
            Gson gson = new GsonBuilder().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://" + Settings.get().other().getAuthDomain() + "/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .client(builder.build())
                    .build();

            return RetrofitWrapper.wrap(retrofit, false);
        });
    }

    @Override
    public Single<RetrofitWrapper> provideAuthServiceRetrofit() {
        return Single.fromCallable(() -> {

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(HttpLogger.DEFAULT_LOGGING_INTERCEPTOR).addInterceptor(chain -> {
                        Request request = chain.request().newBuilder().addHeader("User-Agent", Constants.USER_AGENT(Account_Types.BY_TYPE)).build();
                        return chain.proceed(request);
                    });

            ProxyUtil.applyProxyConfig(builder, proxySettings.getActiveProxy());
            Gson gson = new GsonBuilder().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://" + Settings.get().other().getApiDomain() + "/method/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .client(builder.build())
                    .build();

            return RetrofitWrapper.wrap(retrofit, false);
        });
    }

    private Retrofit createLocalServerRetrofit() {
        LocalServerSettings local_settings = Settings.get().other().getLocalServer();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLogger.DEFAULT_LOGGING_INTERCEPTOR).addInterceptor(chain -> {
                    Request request = chain.request().newBuilder().addHeader("User-Agent", Constants.USER_AGENT(Account_Types.BY_TYPE)).build();
                    return chain.proceed(request);
                }).addInterceptor(chain -> {
                    Request original = chain.request();
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    RequestBody body = original.body();
                    if (body instanceof FormBody) {
                        FormBody formBody = (FormBody) body;
                        for (int i = 0; i < formBody.size(); i++) {
                            formBuilder.add(formBody.name(i), formBody.value(i));
                        }
                    }
                    if (local_settings.password != null) {
                        formBuilder.add("password", local_settings.password);
                    }
                    Request request = original.newBuilder()
                            .method("POST", formBuilder.build())
                            .build();
                    return chain.proceed(request);
                });
        String url = Utils.firstNonEmptyString(local_settings.url, "https://debug.dev");
        assert url != null;
        return new Retrofit.Builder()
                .baseUrl(url + "/method/")
                .addConverterFactory(GsonConverterFactory.create(VkRetrofitProvider.getVkgson()))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(builder.build())
                .build();
    }

    private Retrofit createLongpollRetrofitInstance() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLogger.DEFAULT_LOGGING_INTERCEPTOR).addInterceptor(chain -> {
                    Request request = chain.request().newBuilder().addHeader("User-Agent", Constants.USER_AGENT(Account_Types.BY_TYPE)).build();
                    return chain.proceed(request);
                });

        ProxyUtil.applyProxyConfig(builder, proxySettings.getActiveProxy());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(VkApiLongpollUpdates.class, new LongpollUpdatesAdapter())
                .registerTypeAdapter(AbsLongpollEvent.class, new LongpollUpdateAdapter())
                .create();

        return new Retrofit.Builder()
                .baseUrl("https://" + Settings.get().other().getApiDomain() + "/method/") // dummy
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(builder.build())
                .build();
    }

    @Override
    public Single<RetrofitWrapper> provideLocalServerRetrofit() {
        return Single.fromCallable(() -> {

            if (Objects.isNull(localServerRetrofitInstance)) {
                synchronized (localServerRetrofitLock) {
                    if (Objects.isNull(localServerRetrofitInstance)) {
                        localServerRetrofitInstance = RetrofitWrapper.wrap(createLocalServerRetrofit());
                    }
                }
            }

            return localServerRetrofitInstance;
        });
    }

    @Override
    public Single<RetrofitWrapper> provideLongpollRetrofit() {
        return Single.fromCallable(() -> {

            if (Objects.isNull(longpollRetrofitInstance)) {
                synchronized (longpollRetrofitLock) {
                    if (Objects.isNull(longpollRetrofitInstance)) {
                        longpollRetrofitInstance = RetrofitWrapper.wrap(createLongpollRetrofitInstance());
                    }
                }
            }

            return longpollRetrofitInstance;
        });
    }
}
