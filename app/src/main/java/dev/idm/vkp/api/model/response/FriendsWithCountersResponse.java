package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import dev.idm.vkp.api.model.Items;
import dev.idm.vkp.api.model.VKApiUser;

public class FriendsWithCountersResponse {

    @SerializedName("friends")
    public Items<VKApiUser> friends;

    @SerializedName("counters")
    public VKApiUser.Counters counters;
}
