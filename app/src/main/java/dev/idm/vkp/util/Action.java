package dev.idm.vkp.util;


public interface Action<T> {
    void call(T targer);
}
