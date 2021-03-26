package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiTopic;
import dev.idm.vkp.api.model.VKApiUser;

public class TopicsResponse {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<VKApiTopic> items;

    @SerializedName("default_order")
    public int defaultOrder;

    @SerializedName("can_add_topics")
    public int canAddTopics;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}