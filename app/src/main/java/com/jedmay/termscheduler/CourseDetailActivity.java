package com.jedmay.termscheduler;

import android.app.PendingIntent;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

import Database.WGUTermRoomDatabase;
import Model.Assessment;
import Model.Course;
import NotificationProvider.NotificationReceiver;

public class CourseDetailActivity extends AppCompatActivity {

    Course course;
    long courseId;
    String title;
    boolean startAlarmIsSet, endAlarmIsSet;
    WGUTermRoomDatabase db;
    Intent intent;
    TextView startDateValueTextView, endDateValueTextView, courseStatusValueTextView, mentorValueTextView;
    Button viewNotesButton, startNotificationButton, endNotificationButton;
    FloatingActionButton addAssessmentToCourseFAB, editCourseFAB;
    List<Assessment> assessments;
    ListView assessmentListView;

    NotificationManagerCompat notificationManager;
    NotificationReceiver notificationReceiver;

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

        //Notifications
        notificationManager = NotificationManagerCompat.from(this);
        notificationReceiver = new NotificationReceiver();

        startAlarmIsSet = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(getApplicationContext(), CourseDetailActivity.class), PendingIntent.FLAG_NO_CREATE) != null);

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
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                NotificationProvider.AlarmProvider.setAlarmManager(getApplicationContext(), Calendar.getInstance());
                startAlarmIsSet = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(getApplicationContext(), CourseDetailActivity.class), PendingIntent.FLAG_NO_CREATE) != null);
                if (startAlarmIsSet) {
                    startNotificationButton.setBackgroundResource(R.drawable.ic_add_alert_black_24dp);
                } else {
                    Toast.makeText(getApplicationContext(),"It's False", Toast.LENGTH_LONG).show();
                }
            }
        });

        endNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Setup the screen
        updateTextViews();
        updateList();

    }

    private void updateTextViews() {

        try {
            course = db.courseDao().getCourse(courseId);
            title = course.getMTitle();
            setTitle(title + " Detail");
            String start = DataProvider.Formatter.formatDate(course.getMStartDate());
            String end = DataProvider.Formatter.formatDate(course.getMEndDate());
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
            assessmentString[i] = "Title: " + assessments.get(i).getMTitle() + " | Status: " + assessments.get(i).getMStatus();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assessmentString);
        assessmentListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
