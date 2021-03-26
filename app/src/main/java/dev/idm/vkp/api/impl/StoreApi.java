package dev.idm.vkp.api.impl;

import dev.idm.vkp.Constants;
import dev.idm.vkp.api.IServiceProvider;
import dev.idm.vkp.api.TokenType;
import dev.idm.vkp.api.interfaces.IStoreApi;
import dev.idm.vkp.api.model.VkApiStickerSetsData;
import dev.idm.vkp.api.model.VkApiStickersKeywords;
import dev.idm.vkp.api.services.IStoreService;
import io.reactivex.rxjava3.core.Single;


class StoreApi extends AbsApi implements IStoreApi {

    StoreApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<VkApiStickersKeywords> getStickerKeywords() {
        return provideService(IStoreService.class, TokenType.USER)
                .flatMap(service -> service
                        .getStickersKeywords("var dic=API.store.getStickersKeywords({'v':'" + Constants.API_VERSION + "','aliases':1,'all_products':1}).dictionary;return {'keywords': dic@.words, 'words_stickers': dic@.user_stickers};")
                        .map(extractResponseWithErrorHandling()));
    }

    @Override
    public Single<VkApiStickerSetsData> getStickers() {
        return provideService(IStoreService.class, TokenType.USER)
                .flatMap(service -> service
                        .getStickers("var pack = API.store.getProducts({'v':'" + Constants.API_VERSION + "','extended':1,'filters':'active','type':'stickers'}); var recent = API.messages.getRecentStickers(); return {'sticker_pack': pack, 'recent': recent};")
                        .map(extractResponseWithErrorHandling()));
    }
}
