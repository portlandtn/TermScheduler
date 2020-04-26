package com.jedmay.termscheduler;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jedmay.termscheduler.dataProvider.Formatter;
import com.jedmay.termscheduler.database.WGUTermRoomDatabase;
import com.jedmay.termscheduler.model.Assessment;
import com.jedmay.termscheduler.model.Course;
import com.jedmay.termscheduler.notificationProvider.Constants;
import com.jedmay.termscheduler.notificationProvider.NotificationReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {

    Course course;
    long courseId;

    Date start, end;
    private final int START_TIME_DIALOG = 100;
    private final int END_TIME_DIALOG = 200;

    String title;
    Calendar cal;
    WGUTermRoomDatabase db;
    Intent intent;
    TextView startDateValueTextView, endDateValueTextView, courseStatusValueTextView, mentorValueTextView;
    Button viewNotesButton, startNotificationButton, endNotificationButton;
    FloatingActionButton addAssessmentToCourseFAB, editCourseFAB;
    List<Assessment> assessments;
    ListView assessmentListView;

    @Override
    protected void onResume() {
        super.onResume();
        updateTextViews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        // Setup resources
        startDateValueTextView = findViewById(R.id.startCourseDateValueTextView);
        endDateValueTextView = findViewById(R.id.endCourseDateValueTextView);
        courseStatusValueTextView = findViewById(R.id.courseStatusValueTextView);
        mentorValueTextView = findViewById(R.id.mentorValueTextView);
        viewNotesButton = findViewById(R.id.viewNotesButton);
        startNotificationButton = findViewById(R.id.startDateNotificationButton);
        endNotificationButton = findViewById(R.id.endDateNotificationButton);
        addAssessmentToCourseFAB = findViewById(R.id.addAssessmentToCourseFAB);
        editCourseFAB = findViewById(R.id.editCourseFAB);
        assessmentListView = findViewById(R.id.assessmentListView);
        cal = Calendar.getInstance();

        intent = getIntent();
        courseId = intent.getLongExtra("courseId", 0);


        //Courses list ListView on click listener
        assessmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AssessmentDetailActivity.class);
                assessments = db.assessmentDao().getAssessmentsForCourse(courseId);
                long assessmentId = assessments.get(position).getId();
                intent.putExtra("assessmentId", assessmentId);
                startActivity(intent);
            }
        });

        //edit Course on click Listener
        editCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseEditActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("isEditing", true);
                startActivity(intent);
            }
        });

        //Add Course to Term on click Listener
        addAssessmentToCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AssessmentEditActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        viewNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        startNotificationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                start = course.getMStartDate();
                c.setTime(start);
                showDialog(START_TIME_DIALOG);
            }
        });

        endNotificationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                end = course.getMEndDate();
                c.setTime(end);
                showDialog(START_TIME_DIALOG);
            }
        });

        // Setup the screen
        updateTextViews();
        updateList();

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        boolean is24HourView = android.text.format.DateFormat.is24HourFormat(this);
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        if (id == START_TIME_DIALOG) {
            c.setTime(start);
            c.set(Calendar.HOUR_OF_DAY,hour);
            c.set(Calendar.MINUTE, minute);
            start = c.getTime();
            return new TimePickerDialog(this, startTimeListener, hour, minute, is24HourView);
        } else if (id == END_TIME_DIALOG) {
            c.setTime(end);
            c.set(Calendar.HOUR_OF_DAY,hour);
            c.set(Calendar.MINUTE, minute);
            end = c.getTime();
            return new TimePickerDialog(this, endTimeListener, hour, minute, is24HourView);
        } else {
            return null;
        }
    }

    private TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar c = Calendar.getInstance();
            c.setTime(start);
            c.set(Calendar.HOUR_OF_DAY,hourOfDay);
            c.set(Calendar.MINUTE, minute);
            startAlarm(c,(int) courseId + 1000);
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar c = Calendar.getInstance();
            c.setTime(end);
            c.set(Calendar.HOUR_OF_DAY,hourOfDay);
            c.set(Calendar.MINUTE, minute);
            startAlarm(c,(int) courseId + 2000);
        }
    };

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c, int requestCode) {
        c.set(Calendar.SECOND, 0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Constants.notificationTitle = "Alarm for " + db.courseDao().getCourse(courseId).getMTitle();
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, requestCode, intent, 0);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            assert am != null;
            am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        } else {
            assert am != null;
            am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        }
        Toast.makeText(getApplicationContext(),"Your alarm is set for " + c.getTime(),Toast.LENGTH_LONG).show();

    }

    private void updateTextViews() {

        try {
            course = db.courseDao().getCourse(courseId);
            title = course.getMTitle();
            setTitle(title + " Detail");
            String start = Formatter.formatDate(course.getMStartDate());
            String end = Formatter.formatDate(course.getMEndDate());
            startDateValueTextView.setText(start);
            endDateValueTextView.setText(end);
            courseStatusValueTextView.setText(db.courseDao().getCourse(courseId).getMStatus());
            long mentorId = db.courseDao().getCourse(courseId).getMMentorId();
            mentorValueTextView.setText(db.mentorDao().getMentor(mentorId).getMName());
        } catch (Exception ex) {
            Log.d("GetTerm", ex.getLocalizedMessage());
        }

    }

    private void updateList() {

        assessments = db.assessmentDao().getAssessmentsForCourse(courseId);
        String[] assessmentString = new String[assessments.size()];

        for (int i = 0; i < assessments.size(); i++) {
            String date = Formatter.formatDate(assessments.get(i).getMPlannedDate());

            assessmentString[i] = "Title: " +
                    assessments.get(i).getMTitle() +
                    "\nStatus: " +
                    assessments.get(i).getMStatus() +
                    "\nPlanned Date: " +
                    date;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assessmentString);
        assessmentListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
