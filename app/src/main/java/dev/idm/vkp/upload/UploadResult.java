package dev.idm.vkp.upload;

import dev.idm.vkp.api.model.server.UploadServer;

public class UploadResult<T> {

    private final UploadServer server;
    private final T result;

    public UploadResult(UploadServer server, T result) {
        this.server = server;
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public UploadServer getServer() {
        return server;
    }
}