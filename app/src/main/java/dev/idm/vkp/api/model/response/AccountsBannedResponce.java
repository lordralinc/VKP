package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiUser;

public class AccountsBannedResponce {
    @SerializedName("profiles")
    public List<VKApiUser> profiles;
}
