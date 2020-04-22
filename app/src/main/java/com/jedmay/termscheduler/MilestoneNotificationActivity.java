package com.jedmay.termscheduler;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import Database.WGUTermRoomDatabase;
import Model.Assessment;

public class MilestoneNotificationActivity extends AppCompatActivity {

    WGUTermRoomDatabase db;

    long assessmentId;
    Assessment assessment;
    PendingIntent pi;

    AlarmManager am;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_notification);

        Intent i = getIntent();

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());
        assessmentId = i.getLongExtra("assessmentId", 0);
        assessment = db.assessmentDao().getAssessment(assessmentId);

        Intent intent = new Intent(getApplicationContext(), AssessmentDetailActivity.class);
        intent.putExtra("assessmentId", assessmentId);

        pi = PendingIntent.getActivity(this,0, intent, 0);

        showNotification("Alert for " + assessment.getMTitle(), "test message here");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void showNotification(String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "notif_channel";
        CharSequence channelName = "notificationChannel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 100});
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
