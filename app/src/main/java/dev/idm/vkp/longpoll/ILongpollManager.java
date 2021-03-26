package dev.idm.vkp.longpoll;

import dev.idm.vkp.api.model.longpoll.VkApiLongpollUpdates;
import io.reactivex.rxjava3.core.Flowable;

public interface ILongpollManager {
    void forceDestroy(int accountId);

    Flowable<VkApiLongpollUpdates> observe();

    Flowable<Integer> observeKeepAlive();

    void keepAlive(int accountId);
}