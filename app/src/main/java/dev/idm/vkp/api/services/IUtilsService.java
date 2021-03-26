package dev.idm.vkp.api.services;

import dev.idm.vkp.api.model.Items;
import dev.idm.vkp.api.model.VKApiCheckedLink;
import dev.idm.vkp.api.model.VKApiShortLink;
import dev.idm.vkp.api.model.response.BaseResponse;
import dev.idm.vkp.api.model.response.ResolveDomailResponse;
import dev.idm.vkp.api.model.response.VkApiChatResponse;
import dev.idm.vkp.api.model.response.VkApiLinkResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface IUtilsService {

    @FormUrlEncoded
    @POST("utils.resolveScreenName")
    Single<BaseResponse<ResolveDomailResponse>> resolveScreenName(@Field("screen_name") String screenName);

    @FormUrlEncoded
    @POST("utils.getShortLink")
    Single<BaseResponse<VKApiShortLink>> getShortLink(@Field("url") String url,
                                                      @Field("private") Integer t_private);

    @FormUrlEncoded
    @POST("utils.getLastShortenedLinks")
    Single<BaseResponse<Items<VKApiShortLink>>> getLastShortenedLinks(@Field("count") Integer count,
                                                                      @Field("offset") Integer offset);

    @FormUrlEncoded
    @POST("utils.deleteFromLastShortened")
    Single<BaseResponse<Integer>> deleteFromLastShortened(@Field("key") String key);

    @FormUrlEncoded
    @POST("utils.checkLink")
    Single<BaseResponse<VKApiCheckedLink>> checkLink(@Field("url") String url);

    @FormUrlEncoded
    @POST("messages.joinChatByInviteLink")
    Single<BaseResponse<VkApiChatResponse>> joinChatByInviteLink(@Field("link") String link);

    @FormUrlEncoded
    @POST("messages.getInviteLink")
    Single<BaseResponse<VkApiLinkResponse>> getInviteLink(@Field("peer_id") Integer peer_id,
                                                          @Field("reset") Integer reset);
}
