package dev.idm.vkp.api.impl;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.api.IServiceProvider;
import dev.idm.vkp.api.TokenType;
import dev.idm.vkp.api.interfaces.INotificationsApi;
import dev.idm.vkp.api.model.feedback.VkApiBaseFeedback;
import dev.idm.vkp.api.model.response.NotificationsResponse;
import dev.idm.vkp.api.services.INotificationsService;
import dev.idm.vkp.model.AnswerVKOfficialList;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Objects.isNull;
import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.Utils.safeCountOf;


class NotificationsApi extends AbsApi implements INotificationsApi {

    NotificationsApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<Integer> markAsViewed() {
        return provideService(INotificationsService.class, TokenType.USER)
                .flatMap(service -> service.markAsViewed()
                        .map(extractResponseWithErrorHandling()));
    }

    @Override
    public Single<NotificationsResponse> get(Integer count, String startFrom, String filters, Long startTime, Long endTime) {
        return provideService(INotificationsService.class, TokenType.USER)
                .flatMap(service -> service.get(count, startFrom, filters, startTime, endTime)
                        .map(extractResponseWithErrorHandling())
                        .map(response -> {
                            List<VkApiBaseFeedback> realList = new ArrayList<>(safeCountOf(response.notifications));

                            if (nonNull(response.notifications)) {
                                for (VkApiBaseFeedback n : response.notifications) {
                                    if (isNull(n)) continue;

                                    if (nonNull(n.reply)) {
                                        // fix В ответе нет этого параметра
                                        n.reply.from_id = getAccountId();
                                    }

                                    realList.add(n);
                                }
                            }

                            response.notifications = realList; //without unsupported items
                            return response;
                        }));
    }

    @Override
    public Single<AnswerVKOfficialList> getOfficial(Integer count, Integer startFrom, String filters, Long startTime, Long endTime) {
        return provideService(INotificationsService.class, TokenType.USER)
                .flatMap(service -> service.getOfficial(count, startFrom, filters, startTime, endTime, "photo_200_orig,photo_200")
                        .map(extractResponseWithErrorHandling())
                        .map(response -> response));
    }
}
