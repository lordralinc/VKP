package dev.idm.vkp;

import android.content.Context;

import dev.idm.vkp.api.CaptchaProvider;
import dev.idm.vkp.api.ICaptchaProvider;
import dev.idm.vkp.api.impl.Networker;
import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.db.impl.AppStorages;
import dev.idm.vkp.db.impl.LogsStorage;
import dev.idm.vkp.db.interfaces.ILogsStorage;
import dev.idm.vkp.db.interfaces.IStorages;
import dev.idm.vkp.domain.IAttachmentsRepository;
import dev.idm.vkp.domain.IBlacklistRepository;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.domain.impl.AttachmentsRepository;
import dev.idm.vkp.domain.impl.BlacklistRepository;
import dev.idm.vkp.media.gif.AppGifPlayerFactory;
import dev.idm.vkp.media.gif.IGifPlayerFactory;
import dev.idm.vkp.media.voice.IVoicePlayerFactory;
import dev.idm.vkp.media.voice.VoicePlayerFactory;
import dev.idm.vkp.push.IDeviceIdProvider;
import dev.idm.vkp.push.IPushRegistrationResolver;
import dev.idm.vkp.push.PushRegistrationResolver;
import dev.idm.vkp.settings.IProxySettings;
import dev.idm.vkp.settings.ISettings;
import dev.idm.vkp.settings.ProxySettingsImpl;
import dev.idm.vkp.settings.SettingsImpl;
import dev.idm.vkp.upload.IUploadManager;
import dev.idm.vkp.upload.UploadManagerImpl;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;

import static dev.idm.vkp.util.Objects.isNull;

public class Injection {

    private static final Object UPLOADMANAGERLOCK = new Object();
    private static final IProxySettings proxySettings = new ProxySettingsImpl(provideApplicationContext());
    private static final INetworker networkerInstance = new Networker(proxySettings);
    private static volatile ICaptchaProvider captchaProvider;
    private static volatile IPushRegistrationResolver resolver;
    private static volatile IUploadManager uploadManager;
    private static volatile IAttachmentsRepository attachmentsRepository;
    private static volatile IBlacklistRepository blacklistRepository;
    private static volatile ILogsStorage logsStore;

    public static IProxySettings provideProxySettings() {
        return proxySettings;
    }

    public static IGifPlayerFactory provideGifPlayerFactory() {
        return new AppGifPlayerFactory(proxySettings);
    }

    public static IVoicePlayerFactory provideVoicePlayerFactory() {
        return new VoicePlayerFactory(provideApplicationContext(), provideProxySettings(), provideSettings().other());
    }

    public static IPushRegistrationResolver providePushRegistrationResolver() {
        if (isNull(resolver)) {
            synchronized (Injection.class) {
                if (isNull(resolver)) {
                    Context context = provideApplicationContext();
                    IDeviceIdProvider deviceIdProvider = () -> Utils.getDeviceId(context);
                    resolver = new PushRegistrationResolver(deviceIdProvider, provideSettings(), provideNetworkInterfaces());
                }
            }
        }

        return resolver;
    }

    public static IUploadManager provideUploadManager() {
        if (uploadManager == null) {
            synchronized (UPLOADMANAGERLOCK) {
                if (uploadManager == null) {
                    uploadManager = new UploadManagerImpl(App.getInstance(), provideNetworkInterfaces(),
                            provideStores(), provideAttachmentsRepository(), Repository.INSTANCE.getWalls());
                }
            }
        }

        return uploadManager;
    }

    public static ICaptchaProvider provideCaptchaProvider() {
        if (isNull(captchaProvider)) {
            synchronized (Injection.class) {
                if (isNull(captchaProvider)) {
                    captchaProvider = new CaptchaProvider(provideApplicationContext(), provideMainThreadScheduler());
                }
            }
        }
        return captchaProvider;
    }

    public static IAttachmentsRepository provideAttachmentsRepository() {
        if (isNull(attachmentsRepository)) {
            synchronized (Injection.class) {
                if (isNull(attachmentsRepository)) {
                    attachmentsRepository = new AttachmentsRepository(provideStores().attachments(), Repository.INSTANCE.getOwners());
                }
            }
        }

        return attachmentsRepository;
    }

    public static INetworker provideNetworkInterfaces() {
        return networkerInstance;
    }

    public static IStorages provideStores() {
        return AppStorages.getInstance(App.getInstance());
    }

    public static IBlacklistRepository provideBlacklistRepository() {
        if (isNull(blacklistRepository)) {
            synchronized (Injection.class) {
                if (isNull(blacklistRepository)) {
                    blacklistRepository = new BlacklistRepository();
                }
            }
        }
        return blacklistRepository;
    }

    public static ISettings provideSettings() {
        return SettingsImpl.getInstance(App.getInstance());
    }

    public static ILogsStorage provideLogsStore() {
        if (isNull(logsStore)) {
            synchronized (Injection.class) {
                if (isNull(logsStore)) {
                    logsStore = new LogsStorage(provideApplicationContext());
                }
            }
        }
        return logsStore;
    }

    public static Scheduler provideMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }

    public static Context provideApplicationContext() {
        return App.getInstance();
    }
}
