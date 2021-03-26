package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiUser;
import dev.idm.vkp.api.model.feedback.VkApiBaseFeedback;

public class NotificationsResponse {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<VkApiBaseFeedback> notifications;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;

    @SerializedName("next_from")
    public String nextFrom;

    @SerializedName("last_viewed")
    public long lastViewed;
}
