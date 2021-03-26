package dev.idm.vkp.api.impl;

import dev.idm.vkp.api.IServiceProvider;
import dev.idm.vkp.api.TokenType;
import dev.idm.vkp.api.interfaces.IPagesApi;
import dev.idm.vkp.api.model.VKApiWikiPage;
import dev.idm.vkp.api.services.IPagesService;
import io.reactivex.rxjava3.core.Single;


class PagesApi extends AbsApi implements IPagesApi {

    PagesApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<VKApiWikiPage> get(int ownerId, int pageId, Boolean global, Boolean sitePreview, String title, Boolean needSource, Boolean needHtml) {
        return provideService(IPagesService.class, TokenType.USER)
                .flatMap(service -> service
                        .get(ownerId, pageId, integerFromBoolean(global), integerFromBoolean(sitePreview),
                                title, integerFromBoolean(needSource), integerFromBoolean(needHtml))
                        .map(extractResponseWithErrorHandling()));
    }
}
