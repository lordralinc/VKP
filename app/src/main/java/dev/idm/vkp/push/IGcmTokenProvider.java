package dev.idm.vkp.push;

import java.io.IOException;

public interface IGcmTokenProvider {
    String getToken() throws IOException;
}