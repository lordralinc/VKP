package dev.idm.vkp.api.services;

import dev.idm.vkp.api.model.VkApiStickerSetsData;
import dev.idm.vkp.api.model.VkApiStickersKeywords;
import dev.idm.vkp.api.model.response.BaseResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IStoreService {

    @FormUrlEncoded
    @POST("execute")
    Single<BaseResponse<VkApiStickerSetsData>> getStickers(@Field("code") String code);

    @FormUrlEncoded
    @POST("execute")
    Single<BaseResponse<VkApiStickersKeywords>> getStickersKeywords(@Field("code") String code);
}
