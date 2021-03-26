package dev.idm.vkp.settings;

import dev.idm.vkp.Injection;

public class Settings {

    public static ISettings get() {
        return Injection.provideSettings();
    }

}
