package dev.idm.vkp.domain;

import dev.idm.vkp.model.SectionCounters;
import io.reactivex.rxjava3.core.Single;

public interface ICountersInteractor {
    Single<SectionCounters> getApiCounters(int accountId);
}