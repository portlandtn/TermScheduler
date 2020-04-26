package com.jedmay.termscheduler;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.jedmay.termscheduler.notificationProvider.Constants;
import com.jedmay.termscheduler.notificationProvider.DatePickerFragment;
import com.jedmay.termscheduler.notificationProvider.NotificationReceiver;
import com.jedmay.termscheduler.notificationProvider.TimePickerFragment;

import java.util.Calendar;

public class MilestoneNotificationActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    long courseId;
    String title = "Set Milestone Alarm";

    TextView milestoneDateTextView, milestoneTimeTextView;
    EditText milestoneTitleEditText;
    Button setDateButton, setTimeButton, cancelButton, saveButton;
    ListView notificationsListView;

    Calendar cal;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_notification);

        setDateButton = findViewById(R.id.setMilestoneDateButton);
        setTimeButton = findViewById(R.id.setMilestoneTimeButton);
        saveButton = findViewById(R.id.saveMilestoneButton);
        cancelButton = findViewById(R.id.cancelMilestoneButton);
        milestoneTitleEditText = findViewById(R.id.milestoneTitleEditText);
        milestoneDateTextView = findViewById(R.id.milestoneDateValueTextView);
        milestoneTimeTextView = findViewById(R.id.milestoneTimeValueTextView);
        notificationsListView = findViewById(R.id.notificationsListView);

        setTitle(title);

        cal = Calendar.getInstance();

        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId", 0);

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlarm();
                Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
                finish();
            }
        });

        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        milestoneTimeTextView.setText(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(cal.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        milestoneDateTextView.setText(java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(cal.getTime()));
    }

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm() {
        String title = milestoneTitleEditText.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You must enter an title before creating an alarm.",Toast.LENGTH_SHORT).show();
        } else {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Constants.notificationTitle = milestoneTitleEditText.getText().toString();
            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, 0);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                assert am != null;
                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            } else {
                assert am != null;
                am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        }
    }

}
