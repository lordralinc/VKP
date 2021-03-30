package dev.idm.vkp.util;

import android.annotation.SuppressLint;

import java.io.PrintWriter;
import java.io.StringWriter;

import dev.idm.vkp.Injection;
import dev.idm.vkp.db.interfaces.ILogsStorage;
import dev.idm.vkp.model.LogEvent;
import dev.idm.vkp.settings.Settings;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class PersistentLogger {

    @SuppressLint("CheckResult")
    public static void logThrowable(String tag, Throwable throwable) {
        if (!Settings.get().other().isDeveloperMode())
            return;
        ILogsStorage store = Injection.provideLogsStore();
        Throwable cause = Utils.getCauseIfRuntime(throwable);

        getStackTrace(cause)
                .flatMapCompletable(s -> store.add(LogEvent.Type.ERROR, tag, s)
                        .ignoreElement())
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                }, ignore -> {
                });
    }

    private static Single<String> getStackTrace(Throwable throwable) {
        return Single.fromCallable(() -> {
            try (StringWriter sw = new StringWriter();
                 PrintWriter pw = new PrintWriter(sw)) {
                throwable.printStackTrace(pw);
                return sw.toString();
            }
        });
    }
}