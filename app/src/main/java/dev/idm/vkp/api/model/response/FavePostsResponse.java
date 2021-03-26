package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiUser;
import dev.idm.vkp.api.model.VkApiAttachments;

public class FavePostsResponse {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<VkApiAttachments.Entry> posts;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}
