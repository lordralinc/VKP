package dev.idm.vkp

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.ImageLoaderFactory
import dev.idm.vkp.activity.MainActivity
import dev.idm.vkp.domain.Repository.messages
import dev.idm.vkp.idm.NetWorker
import dev.idm.vkp.longpoll.NotificationHelper
import dev.idm.vkp.model.PeerUpdate
import dev.idm.vkp.model.SentMsg
import dev.idm.vkp.picasso.PicassoInstance
import dev.idm.vkp.place.Place
import dev.idm.vkp.player.util.MusicUtils
import dev.idm.vkp.push.NotificationUtils
import dev.idm.vkp.service.ErrorLocalizer
import dev.idm.vkp.service.KeepLongpollService
import dev.idm.vkp.settings.Settings
import dev.idm.vkp.util.CustomToast.Companion.CreateCustomToast
import dev.idm.vkp.util.Objects
import dev.idm.vkp.util.PersistentLogger
import dev.idm.vkp.util.RxUtils
import dev.idm.vkp.util.Utils
import dev.ragnarok.fenrir.module.FenrirNative
import ealvatag.tag.TagOptionSingleton
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class App : Application(), ImageLoaderFactory {
    private val UPDATE_CHANNEL_NAME: String = "update_channel"
    private val compositeDisposable = CompositeDisposable()

    override fun newImageLoader(): ImageLoader {
        return PicassoInstance.createCoilImageLoader(this, Injection.provideProxySettings())
    }


    fun showNotification(title: String, message: String){

        val manager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Objects.isNull(manager)) { return }

        if (Utils.hasOreo()) {
            val channel = NotificationChannel(
                this.UPDATE_CHANNEL_NAME,
                applicationContext.getString(R.string.updates),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.enableLights(true)
            channel.enableVibration(true)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "update_channel")
            .setSmallIcon(R.drawable.ic_notification_upload)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .apply {
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            .build()

        manager.notify(this.UPDATE_CHANNEL_NAME, 1, notification)
    }

    @SuppressLint("UnsafeExperimentalUsageWarning")
    override fun onCreate() {
        sInstanse = this
        AppCompatDelegate.setDefaultNightMode(Settings.get().ui().nightMode)

        //CrashConfig.Builder.create().apply()
        if (Settings.get().other().isEnable_native) {
            FenrirNative.loadNativeLibrary { PersistentLogger.logThrowable("NativeError", it) }
        }
        FenrirNative.updateAppContext(this)
        TagOptionSingleton.getInstance().isAndroid = true
        MusicUtils.registerBroadcast(this)
        super.onCreate()
        PicassoInstance.init(this, Injection.provideProxySettings())
        if (Settings.get().other().isKeepLongpoll) {
            KeepLongpollService.start(this)
        }
        compositeDisposable.add(messages
            .observePeerUpdates()
            .flatMap { source: List<PeerUpdate> -> Flowable.fromIterable(source) }
            .subscribe({ update: PeerUpdate ->
                if (update.readIn != null) {
                    NotificationHelper.tryCancelNotificationForPeer(
                        this,
                        update.accountId,
                        update.peerId
                    )
                }
            }, RxUtils.ignore())
        )
        compositeDisposable.add(
            messages
                .observeSentMessages()
                .subscribe({ sentMsg: SentMsg ->
                    NotificationHelper.tryCancelNotificationForPeer(
                        this,
                        sentMsg.accountId,
                        sentMsg.peerId
                    )
                }, RxUtils.ignore())
        )
        compositeDisposable.add(
            messages
                .observeMessagesSendErrors()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ throwable: Throwable ->
                    run {
                        CreateCustomToast(this).showToastError(
                            ErrorLocalizer.localizeThrowable(this, throwable)
                        ); throwable.printStackTrace()
                    }
                }, RxUtils.ignore())
        )
        RxJavaPlugins.setErrorHandler {
            Handler(mainLooper).post {
                CreateCustomToast(this).showToastError(
                    ErrorLocalizer.localizeThrowable(
                        this,
                        it
                    )
                )
            }
        }



        NetWorker().get("https://raw.githubusercontent.com/lordralinc/VKP/main/releases/current_version.json")
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Toast.makeText(
                        this@App.applicationContext,
                        applicationContext.getText(R.string.error_on_checking_update),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val json = JSONObject(response.body!!.string())
                        val versionCode = BuildConfig.VERSION_CODE
                        if (versionCode < json.getInt("version_code")) {
                            showNotification(
                                applicationContext.getString(R.string.update_title),
                                applicationContext.getString(R.string.update_text).format(json.getString("version_name"))
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    companion object {
        private var sInstanse: App? = null

        @JvmStatic
        val instance: App
            get() {
                checkNotNull(sInstanse) { "App instance is null!!! WTF???" }
                return sInstanse!!
            }
    }
}
