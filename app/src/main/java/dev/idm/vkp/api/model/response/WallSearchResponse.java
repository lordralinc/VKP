package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiPost;
import dev.idm.vkp.api.model.VKApiUser;


public class WallSearchResponse {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<VKApiPost> items;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}