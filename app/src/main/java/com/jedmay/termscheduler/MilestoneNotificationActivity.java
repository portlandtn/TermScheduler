package com.jedmay.termscheduler;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import Database.WGUTermRoomDatabase;
import Model.Course;
import NotificationProvider.DatePickerFragment;
import NotificationProvider.TimePicker;

public class MilestoneNotificationActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    WGUTermRoomDatabase db;

    long courseId;
    Course course;
    PendingIntent pi;

    TextView milestoneDateTextView, milestoneTimeTextView;
    Button setDateButton, setTimeButton;

    Calendar cal;

    AlarmManager am;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_notification);

        setDateButton = findViewById(R.id.setMilestoneDateButton);
        setTimeButton = findViewById(R.id.setMilestoneTimeButton);
        milestoneDateTextView = findViewById(R.id.milestoneDateValueTextView);
        milestoneTimeTextView = findViewById(R.id.milestoneTimeValueTextView);

        Intent i = getIntent();
        cal = Calendar.getInstance();

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());
        courseId = i.getLongExtra("courseId", 0);
        course = db.courseDao().getCourse(courseId);

        Intent intent = new Intent(getApplicationContext(), AssessmentDetailActivity.class);
        intent.putExtra("courseId", courseId);

        pi = PendingIntent.getActivity(this,0, intent, 0);

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(),"DialogTimeFragment");
            }
        });

        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker Fragment");
            }
        });
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        milestoneTimeTextView.setText(DateFormat.format("h:mm aa",cal.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        milestoneDateTextView.setText(DateFormat.format("MMMM dd, yyyy", cal.getTime()));
    }
}
