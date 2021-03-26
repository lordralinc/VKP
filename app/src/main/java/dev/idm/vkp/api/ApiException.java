package dev.idm.vkp.api;

import dev.idm.vkp.api.model.Error;


public class ApiException extends Exception {

    private final Error error;

    public ApiException(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
