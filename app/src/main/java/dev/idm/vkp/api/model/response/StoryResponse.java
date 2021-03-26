package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiUser;

public class StoryResponse {

    @SerializedName("items")
    public List<StoryBlockResponce> items;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}
