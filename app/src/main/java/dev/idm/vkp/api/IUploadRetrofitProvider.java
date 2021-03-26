package dev.idm.vkp.api;

import io.reactivex.rxjava3.core.Single;

public interface IUploadRetrofitProvider {
    Single<RetrofitWrapper> provideUploadRetrofit();
}