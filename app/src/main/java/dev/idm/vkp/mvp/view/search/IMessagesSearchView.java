package dev.idm.vkp.mvp.view.search;

import dev.idm.vkp.model.Message;


public interface IMessagesSearchView extends IBaseSearchView<Message> {

    void goToMessagesLookup(int accountId, int peerId, int messageId);
}
