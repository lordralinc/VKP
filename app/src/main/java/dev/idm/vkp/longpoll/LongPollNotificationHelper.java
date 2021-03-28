package dev.idm.vkp.longpoll;

import android.content.Context;

import dev.idm.vkp.model.Message;
import dev.idm.vkp.settings.ISettings;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Logger;

import static dev.idm.vkp.util.Utils.hasFlag;

public class LongPollNotificationHelper {

    public static final String TAG = LongPollNotificationHelper.class.getSimpleName();

    /**
     * Действие при добавлении нового сообщения в диалог или чат
     *
     * @param message нотификация с сервера
     */
    public static void notifyAbountNewMessage(Context context, Message message) {
        if (message.isOut()) {
            return;
        }
        notifyAbountNewMessage(context, message.getAccountId(), message);
    }

    private static void notifyAbountNewMessage(Context context, int accountId, Message message) {
        int mask = Settings.get().notifications().getNotifPref(accountId, message.getPeerId());

        if (Settings.get().accounts().getCurrent() != accountId) {
            Logger.d(TAG, "notifyAbountNewMessage, Attempting to send a notification does not in the current account!!!");
            return;
        }

        if (message.getBody().contains("[id" + accountId + "|")){
            Settings.get().notifications().setPush(
                    accountId,
                    message.getPeerId(),
                    message.getId()
            );
        }

        if (hasFlag(mask, ISettings.INotificationSettings.FLAG_PUSH) && message.getBody().contains("[id" + accountId + "|")){
            NotificationHelper.notifyNewMessage(context, accountId, message);
            return;
        }

        if (!hasFlag(mask, ISettings.INotificationSettings.FLAG_SHOW_NOTIF)) {
            return;
        }

        NotificationHelper.notifyNewMessage(context, accountId, message);
    }
}
