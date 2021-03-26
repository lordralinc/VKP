package dev.idm.vkp.api.impl;

import dev.idm.vkp.api.IOtherVkRetrofitProvider;
import dev.idm.vkp.api.IUploadRetrofitProvider;
import dev.idm.vkp.api.IVkRetrofitProvider;
import dev.idm.vkp.api.OtherVkRetrofitProvider;
import dev.idm.vkp.api.UploadRetrofitProvider;
import dev.idm.vkp.api.VkMethodHttpClientFactory;
import dev.idm.vkp.api.VkRetrofitProvider;
import dev.idm.vkp.api.interfaces.IAccountApis;
import dev.idm.vkp.api.interfaces.IAuthApi;
import dev.idm.vkp.api.interfaces.ILocalServerApi;
import dev.idm.vkp.api.interfaces.ILongpollApi;
import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.interfaces.IUploadApi;
import dev.idm.vkp.api.services.IAuthService;
import dev.idm.vkp.api.services.ILocalServerService;
import dev.idm.vkp.settings.IProxySettings;

public class Networker implements INetworker {

    private final IOtherVkRetrofitProvider otherVkRetrofitProvider;
    private final IVkRetrofitProvider vkRetrofitProvider;
    private final IUploadRetrofitProvider uploadRetrofitProvider;

    public Networker(IProxySettings settings) {
        otherVkRetrofitProvider = new OtherVkRetrofitProvider(settings);
        vkRetrofitProvider = new VkRetrofitProvider(settings, new VkMethodHttpClientFactory());
        uploadRetrofitProvider = new UploadRetrofitProvider(settings);
    }

    @Override
    public IAccountApis vkDefault(int accountId) {
        return VkApies.get(accountId, vkRetrofitProvider);
    }

    @Override
    public IAccountApis vkManual(int accountId, String accessToken) {
        return VkApies.create(accountId, accessToken, vkRetrofitProvider);
    }

    @Override
    public IAuthApi vkDirectAuth() {
        return new AuthApi(() -> otherVkRetrofitProvider.provideAuthRetrofit().map(wrapper -> wrapper.create(IAuthService.class)));
    }

    @Override
    public IAuthApi vkAuth() {
        return new AuthApi(() -> otherVkRetrofitProvider.provideAuthServiceRetrofit().map(wrapper -> wrapper.create(IAuthService.class)));
    }

    @Override
    public ILocalServerApi localServerApi() {
        return new LocalServerApi(() -> otherVkRetrofitProvider.provideLocalServerRetrofit().map(wrapper -> wrapper.create(ILocalServerService.class)));
    }

    @Override
    public ILongpollApi longpoll() {
        return new LongpollApi(otherVkRetrofitProvider);
    }

    @Override
    public IUploadApi uploads() {
        return new UploadApi(uploadRetrofitProvider);
    }
}
