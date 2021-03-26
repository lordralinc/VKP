package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

public class isLikeResponse {

    @SerializedName("liked")
    public int liked;

    @SerializedName("copied")
    public int copied;

}
