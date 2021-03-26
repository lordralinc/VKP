package dev.idm.vkp.api.interfaces;

import dev.idm.vkp.api.model.VkApiStickerSetsData;
import dev.idm.vkp.api.model.VkApiStickersKeywords;
import io.reactivex.rxjava3.core.Single;


public interface IStoreApi {
    Single<VkApiStickersKeywords> getStickerKeywords();

    Single<VkApiStickerSetsData> getStickers();
}
