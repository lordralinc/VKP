package dev.idm.vkp.api;

import dev.idm.vkp.Injection;
import dev.idm.vkp.api.interfaces.INetworker;

public class Apis {

    public static INetworker get() {
        return Injection.provideNetworkInterfaces();
    }

}
