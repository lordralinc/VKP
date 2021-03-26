package dev.idm.vkp.api;

import com.google.gson.Gson;

import dev.idm.vkp.Account_Types;
import dev.idm.vkp.settings.Settings;

public class DefaultVkApiInterceptor extends AbsVkApiInterceptor {

    private final int accountId;

    DefaultVkApiInterceptor(int accountId, String v, Gson gson) {
        super(v, gson);
        this.accountId = accountId;
    }

    @Override
    protected String getToken() {
        return Settings.get()
                .accounts()
                .getAccessToken(accountId);
    }

    @Override
    protected @Account_Types
    int getType() {
        return Settings.get()
                .accounts()
                .getType(accountId);
    }


    @Override
    protected int getAccountId() {
        return accountId;
    }
}
