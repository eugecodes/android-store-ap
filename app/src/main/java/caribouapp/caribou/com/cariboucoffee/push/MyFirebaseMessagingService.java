package caribouapp.caribou.com.cariboucoffee.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.urbanairship.push.fcm.AirshipFirebaseIntegration;

import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.util.Log;

/**
 * Created by andressegurola on 12/28/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private static final String URBAN_AIRSHIP_REMOTE_MESSAGE_PROP_PREFIX = "com.urbanairship";

    private static Random random = new Random();

    @Inject
    PushManager mPushManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (isUrbanAirshipPush(remoteMessage)) {
            AirshipFirebaseIntegration.processMessageSync(this, remoteMessage);
        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Push received - Title:" + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Push received - Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getData() != null) {
            String message = remoteMessage.getData().get("message");
            Log.d(TAG, "Push received - Data: " + remoteMessage.getData().toString());
            showNotification(getString(R.string.brand_app_name), message);
        } else {
            Log.i(TAG, "Push ignored: " + remoteMessage.toString());
        }
    }

    private boolean isUrbanAirshipPush(RemoteMessage remoteMessage) {
        for (Map.Entry<String, String> dataEntry : remoteMessage.getData().entrySet()) {
            if (dataEntry.getKey().startsWith(URBAN_AIRSHIP_REMOTE_MESSAGE_PROP_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    private void showNotification(String title, String body) {
        int pollNotificationId = random.nextInt();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_push_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(createContentPendingIntent(this))
                .setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(pollNotificationId, builder.build());
    }

    private PendingIntent createContentPendingIntent(Context context) {
        Intent pushIntent = new Intent(context, MainActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getActivity(context, random.nextInt(), pushIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            return PendingIntent.getActivity(context, random.nextInt(), pushIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

    }

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        AirshipFirebaseIntegration.processNewToken(this);
        SourceApplication.get(this).getComponent().inject(this);
        mPushManager.registerForPaytronixPush();
    }
}
