package dev.idm.vkp.db;

import dev.idm.vkp.Injection;
import dev.idm.vkp.db.interfaces.IStorages;

public class Stores {

    public static IStorages getInstance() {
        return Injection.provideStores();
    }

}
