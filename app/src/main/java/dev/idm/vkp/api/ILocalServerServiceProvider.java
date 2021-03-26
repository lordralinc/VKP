package dev.idm.vkp.api;

import dev.idm.vkp.api.services.ILocalServerService;
import io.reactivex.rxjava3.core.Single;

public interface ILocalServerServiceProvider {
    Single<ILocalServerService> provideLocalServerService();
}
