package dev.idm.vkp.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.idm.vkp.api.model.VKApiAudio;
import dev.idm.vkp.api.model.VKApiAudioPlaylist;
import dev.idm.vkp.api.model.VKApiCatalogLink;
import dev.idm.vkp.api.model.VKApiVideo;


public class CatalogResponse {

    @SerializedName("audios")
    public List<VKApiAudio> audios;

    @SerializedName("playlists")
    public List<VKApiAudioPlaylist> playlists;

    @SerializedName("videos")
    public List<VKApiVideo> videos;

    @SerializedName("items")
    public List<VKApiCatalogLink> items;

    @SerializedName("next_from")
    public String nextFrom;
}
