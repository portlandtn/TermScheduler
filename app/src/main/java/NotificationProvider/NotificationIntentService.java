package NotificationProvider;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.jedmay.termscheduler.MainActivity;
import com.jedmay.termscheduler.R;

public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private final String TITLE;
    private final String MESSAGE;

    public NotificationIntentService(String title, String message) {
        super("NotificationIntentService");
        this.TITLE = title;
        this.MESSAGE = message;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(TITLE);
        builder.setContentText(MESSAGE);
        builder.setSmallIcon(R.drawable.ic_add_alert_black_24dp);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}
