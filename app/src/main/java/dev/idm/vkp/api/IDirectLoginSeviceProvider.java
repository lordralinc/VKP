package dev.idm.vkp.api;

import dev.idm.vkp.api.services.IAuthService;
import io.reactivex.rxjava3.core.Single;

public interface IDirectLoginSeviceProvider {
    Single<IAuthService> provideAuthService();
}