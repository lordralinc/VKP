package dev.idm.vkp.longpoll;

public interface ILongpoll {
    int getAccountId();

    void connect();

    void shutdown();
}