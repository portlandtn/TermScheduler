package NotificationProvider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmProvider {

    public static AlarmManager setAlarmManager(Context context, Calendar cal) {

        Calendar calendar = updateCalendarForAlerts(cal);

        Intent notifyIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context,1,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        return alarmManager;
    }

    // Sets the calendar time to 07:00, regardless of what it was
    private static Calendar updateCalendarForAlerts(Calendar cal) {
        cal.set(Calendar.HOUR, 7);
        cal.set(Calendar.MINUTE, 0);
        return cal;
    }

}
