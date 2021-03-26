package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import dev.idm.vkp.model.LoadMoreState;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.model.Peer;

public interface INotReadMessagesView extends IBasicMessageListView, IErrorView {

    void focusTo(int index);

    void setupHeaders(@LoadMoreState int upHeaderState, @LoadMoreState int downHeaderState);

    void forwardMessages(int accountId, @NonNull ArrayList<Message> messages);

    void showDeleteForAllDialog(ArrayList<Integer> ids);

    void doFinish(int incoming, int outgoing, boolean notAnim);

    void displayToolbarAvatar(Peer peer);

    void displayUnreadCount(int unreadCount);
}
