package NotificationProvider;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppStartup extends Application {
    public static final String COURSE_ALERT_CHANNEL = "course alert channel";
    public static final String MILESTONE_ALERT_CHANNEL = "milestone alert channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel courseChannel = new NotificationChannel(
                    COURSE_ALERT_CHANNEL,
                    "Course Alert",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            courseChannel.setDescription("Course alert channel");

            NotificationChannel milestoneChannel = new NotificationChannel(
                    MILESTONE_ALERT_CHANNEL,
                    "Milestone Alert",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            courseChannel.setDescription("Milestone alert channel");

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(courseChannel);
            manager.createNotificationChannel(milestoneChannel);
        }

    }

}
