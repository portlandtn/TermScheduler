package NotificationProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jedmay.termscheduler.AssessmentDetailActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AssessmentDetailActivity.class);
        context.startService(i);

    }
}
