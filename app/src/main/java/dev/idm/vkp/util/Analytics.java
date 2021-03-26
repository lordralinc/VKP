package dev.idm.vkp.util;

import dev.idm.vkp.BuildConfig;


public class Analytics {

    public static void logUnexpectedError(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace();
        }

        //FirebaseCrash.report(throwable);
    }
}