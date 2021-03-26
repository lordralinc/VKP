package dev.idm.vkp.api.impl;

import com.google.gson.Gson;

import dev.idm.vkp.Injection;
import dev.idm.vkp.api.ApiException;
import dev.idm.vkp.api.AuthException;
import dev.idm.vkp.api.CaptchaNeedException;
import dev.idm.vkp.api.IDirectLoginSeviceProvider;
import dev.idm.vkp.api.NeedValidationException;
import dev.idm.vkp.api.interfaces.IAuthApi;
import dev.idm.vkp.api.model.LoginResponse;
import dev.idm.vkp.api.model.VkApiValidationResponce;
import dev.idm.vkp.api.model.response.BaseResponse;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleTransformer;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.Utils.nonEmpty;


public class AuthApi implements IAuthApi {

    private static final Gson BASE_RESPONSE_GSON = new Gson();
    private final IDirectLoginSeviceProvider service;

    public AuthApi(IDirectLoginSeviceProvider service) {
        this.service = service;
    }

    static <T> Function<BaseResponse<T>, T> extractResponseWithErrorHandling() {
        return response -> {
            if (nonNull(response.error)) {
                throw Exceptions.propagate(new ApiException(response.error));
            }

            return response.response;
        };
    }

    private static <T> SingleTransformer<T, T> withHttpErrorHandling() {
        return single -> single.onErrorResumeNext(throwable -> {

            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;

                try {
                    ResponseBody body = httpException.response().errorBody();
                    LoginResponse response = BASE_RESPONSE_GSON.fromJson(body.string(), LoginResponse.class);

                    //{"error":"need_captcha","captcha_sid":"846773809328","captcha_img":"https:\/\/api.vk.com\/captcha.php?sid=846773809328"}

                    if ("need_captcha".equalsIgnoreCase(response.error)) {
                        return Single.error(new CaptchaNeedException(response.captchaSid, response.captchaImg));
                    }

                    if ("need_validation".equalsIgnoreCase(response.error)) {
                        return Single.error(new NeedValidationException(response.validationType, response.redirect_uri, response.validation_sid, response.phoneMask));
                    }

                    if (nonEmpty(response.error)) {
                        return Single.error(new AuthException(response.error, response.errorDescription));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return Single.error(throwable);
        });
    }

    @Override
    public Single<LoginResponse> directLogin(String grantType, int clientId, String clientSecret,
                                             String username, String pass, String v, boolean twoFaSupported,
                                             String scope, String code, String captchaSid, String captchaKey, boolean forceSms) {
        return service.provideAuthService()
                .flatMap(service -> service
                        .directLogin(grantType, clientId, clientSecret, username, pass, v, twoFaSupported ? 1 : null, scope, code, captchaSid, captchaKey, forceSms ? 1 : 0, Utils.getDeviceId(Injection.provideApplicationContext()), 1)
                        .compose(withHttpErrorHandling()));
    }

    @Override
    public Single<VkApiValidationResponce> validatePhone(int apiId, int clientId, String clientSecret, String sid, String v) {
        return service.provideAuthService()
                .flatMap(service -> service
                        .validatePhone(apiId, clientId, clientSecret, sid, v)
                        .map(extractResponseWithErrorHandling()));
    }
}
