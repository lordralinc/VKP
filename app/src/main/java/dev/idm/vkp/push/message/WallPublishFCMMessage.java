package dev.idm.vkp.push.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.MainActivity;
import dev.idm.vkp.link.VkLinkParser;
import dev.idm.vkp.link.types.AbsLink;
import dev.idm.vkp.link.types.WallPostLink;
import dev.idm.vkp.longpoll.AppNotificationChannels;
import dev.idm.vkp.longpoll.NotificationHelper;
import dev.idm.vkp.model.Community;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.push.NotificationScheduler;
import dev.idm.vkp.push.OwnerInfo;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.PersistentLogger;
import dev.idm.vkp.util.Utils;

import static dev.idm.vkp.push.NotificationUtils.configOtherPushNotification;

public class WallPublishFCMMessage {

    private static final String TAG = WallPublishFCMMessage.class.getSimpleName();

    // collapseKey: wall_publish, extras: Bundle[{from=376771982493, name=Fenrir for VK,
    // text=Тестирование уведомлений, type=wall_publish, place=wall-72124992_4914,
    // group_id=72124992, sandbox=0, collapse_key=wall_publish}]

    //public long from;
    //public String name;
    private String text;
    //public String type;
    private String place;
    private int group_id;

    public static WallPublishFCMMessage fromRemoteMessage(@NonNull RemoteMessage remote) {
        WallPublishFCMMessage message = new WallPublishFCMMessage();
        //message.name = bundle.getString("name");
        //message.from = optLong(bundle, "from");
        message.group_id = Integer.parseInt(remote.getData().get("group_id"));
        message.text = remote.getData().get("text");
        //message.type = bundle.getString("type");
        message.place = remote.getData().get("place");
        return message;
    }

    public void notify(Context context, int accountId) {
        if (!Settings.get()
                .notifications()
                .isWallPublishNotifEnabled()) {
            return;
        }

        Context app = context.getApplicationContext();
        OwnerInfo.getRx(app, accountId, -Math.abs(group_id))
                .subscribeOn(NotificationScheduler.INSTANCE)
                .subscribe(ownerInfo -> notifyImpl(app, ownerInfo.getCommunity(), ownerInfo.getAvatar()), throwable -> {/*ignore*/});
    }

    private void notifyImpl(Context context, @NonNull Community community, Bitmap bitmap) {
        String url = "vk.com/" + place;
        AbsLink link = VkLinkParser.parse(url);

        if (!(link instanceof WallPostLink)) {
            PersistentLogger.logThrowable("Push issues", new Exception("Unknown place: " + place));
            return;
        }

        WallPostLink wallPostLink = (WallPostLink) link;

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Utils.hasOreo()) {
            nManager.createNotificationChannel(AppNotificationChannels.getNewPostChannel(context));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppNotificationChannels.NEW_POST_CHANNEL_ID)
                .setSmallIcon(R.drawable.pencil)
                .setLargeIcon(bitmap)
                .setContentTitle(community.getFullName())
                .setContentText(context.getString(R.string.postings_you_the_news))
                .setSubText(text)
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        int aid = Settings.get()
                .accounts()
                .getCurrent();

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.PLACE, PlaceFactory.getPostPreviewPlace(aid, wallPostLink.postId, wallPostLink.ownerId));
        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, wallPostLink.postId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();

        configOtherPushNotification(notification);
        nManager.notify(place, NotificationHelper.NOTIFICATION_WALL_PUBLISH_ID, notification);
    }
}
