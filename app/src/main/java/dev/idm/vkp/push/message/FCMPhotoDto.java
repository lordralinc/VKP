package dev.idm.vkp.push.message;

import com.google.gson.annotations.SerializedName;

public class FCMPhotoDto {
    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

    @SerializedName("url")
    public String url;
}
