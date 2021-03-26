package dev.idm.vkp.api.interfaces;

import androidx.annotation.CheckResult;

import dev.idm.vkp.api.model.response.NotificationsResponse;
import dev.idm.vkp.model.AnswerVKOfficialList;
import io.reactivex.rxjava3.core.Single;


public interface INotificationsApi {

    @CheckResult
    Single<Integer> markAsViewed();

    @CheckResult
    Single<NotificationsResponse> get(Integer count, String startFrom, String filters,
                                      Long startTime, Long endTime);

    @CheckResult
    Single<AnswerVKOfficialList> getOfficial(Integer count, Integer startFrom, String filters, Long startTime, Long endTime);

}
