package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiUser;
import dev.idm.vkp.api.model.VkApiDialog;

public class DialogsResponse {

    @SerializedName("items")
    public List<VkApiDialog> dialogs;

    @SerializedName("count")
    public int count;

    @SerializedName("unread_count")
    public int unreadCount;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}