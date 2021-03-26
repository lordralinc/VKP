package dev.idm.vkp.api.impl;

import dev.idm.vkp.api.IServiceProvider;
import dev.idm.vkp.api.TokenType;
import dev.idm.vkp.api.interfaces.IStatusApi;
import dev.idm.vkp.api.services.IStatusService;
import io.reactivex.rxjava3.core.Single;


class StatusApi extends AbsApi implements IStatusApi {

    StatusApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<Boolean> set(String text, Integer groupId) {
        return provideService(IStatusService.class, TokenType.USER)
                .flatMap(service -> service.set(text, groupId)
                        .map(extractResponseWithErrorHandling())
                        .map(response -> response == 1));
    }
}
