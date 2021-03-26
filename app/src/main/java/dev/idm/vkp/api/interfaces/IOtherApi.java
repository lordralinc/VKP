package dev.idm.vkp.api.interfaces;

import java.util.Map;

import dev.idm.vkp.util.Optional;
import io.reactivex.rxjava3.core.Single;

public interface IOtherApi {
    Single<Optional<String>> rawRequest(String method, Map<String, String> postParams);
}