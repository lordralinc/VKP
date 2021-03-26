package dev.idm.vkp.api.interfaces;

import dev.idm.vkp.api.model.LoginResponse;
import dev.idm.vkp.api.model.VkApiValidationResponce;
import io.reactivex.rxjava3.core.Single;


public interface IAuthApi {
    Single<LoginResponse> directLogin(String grantType, int clientId, String clientSecret,
                                      String username, String pass, String v, boolean twoFaSupported,
                                      String scope, String code, String captchaSid, String captchaKey, boolean forceSms);

    Single<VkApiValidationResponce> validatePhone(int apiId, int clientId, String clientSecret, String sid, String v);
}