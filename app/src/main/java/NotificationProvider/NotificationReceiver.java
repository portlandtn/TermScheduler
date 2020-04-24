package NotificationProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jedmay.termscheduler.CourseDetailActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CourseDetailActivity.class);
        context.startService(i);

    }
}
