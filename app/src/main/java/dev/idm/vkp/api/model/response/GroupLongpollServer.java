package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

public class GroupLongpollServer {

    @SerializedName("key")
    public String key;

    @SerializedName("server")
    public String server;

    @SerializedName("ts")
    public String ts;
}