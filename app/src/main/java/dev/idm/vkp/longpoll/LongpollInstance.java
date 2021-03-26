package dev.idm.vkp.longpoll;

import dev.idm.vkp.Injection;
import dev.idm.vkp.realtime.Processors;

public class LongpollInstance {

    private static volatile ILongpollManager longpollManager;

    public static ILongpollManager get() {
        if (longpollManager == null) {
            synchronized (LongpollInstance.class) {
                if (longpollManager == null) {
                    longpollManager = new AndroidLongpollManager(Injection.provideNetworkInterfaces(), Processors.realtimeMessages());
                }
            }
        }
        return longpollManager;
    }
}