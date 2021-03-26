package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiMessage;
import dev.idm.vkp.api.model.VKApiUser;
import dev.idm.vkp.api.model.VkApiConversation;

public class MessageHistoryResponse {

    @SerializedName("items")
    public List<VKApiMessage> messages;

    @SerializedName("count")
    public int count;

    @SerializedName("unread")
    public int unread;

    @SerializedName("conversations")
    public List<VkApiConversation> conversations;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}
