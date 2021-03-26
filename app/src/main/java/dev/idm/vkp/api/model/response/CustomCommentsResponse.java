package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiComment;
import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiPoll;
import dev.idm.vkp.api.model.VKApiUser;

public class CustomCommentsResponse {

    // Parse manually in CustomCommentsResponseAdapter

    public Main main;

    public Integer firstId;

    public Integer lastId;

    public Integer admin_level;

    public static class Main {

        @SerializedName("count")
        public int count;

        @SerializedName("items")
        public List<VKApiComment> comments;

        @SerializedName("profiles")
        public List<VKApiUser> profiles;

        @SerializedName("groups")
        public List<VKApiCommunity> groups;

        @SerializedName("poll")
        public VKApiPoll poll;
    }

}
