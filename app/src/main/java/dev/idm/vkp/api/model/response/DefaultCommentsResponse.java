package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiComment;
import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiUser;

public class DefaultCommentsResponse {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<VKApiComment> items;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

}
