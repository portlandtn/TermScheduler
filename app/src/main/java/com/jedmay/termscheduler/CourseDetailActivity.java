package com.jedmay.termscheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jedmay.termscheduler.dataProvider.Formatter;
import com.jedmay.termscheduler.database.WGUTermRoomDatabase;
import com.jedmay.termscheduler.model.Assessment;
import com.jedmay.termscheduler.model.Course;
import com.jedmay.termscheduler.notificationProvider.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {

    Course course;
    long courseId;
    String title;
    Calendar cal;
    WGUTermRoomDatabase db;
    Intent intent;
    TextView startDateValueTextView, endDateValueTextView, courseStatusValueTextView, mentorValueTextView;
    Button viewNotesButton, startNotificationButton, endNotificationButton, milestonesButton;
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
        milestonesButton = findViewById(R.id.milestonesButton);
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

        milestonesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MilestoneNotificationActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        startNotificationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();

                Date date = course.getMStartDate();
                c.setTime(date);
                c.set(Calendar.HOUR_OF_DAY,10);
                c.set(Calendar.MINUTE, 2);
                c.set(Calendar.SECOND, 0);
                startAlarm(c);
            }
        });

        endNotificationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();

                Date date = course.getMEndDate();
                c.setTime(date);
                c.set(Calendar.HOUR_OF_DAY,10);
                c.set(Calendar.MINUTE, 4);
                c.set(Calendar.SECOND, 0);
                startAlarm(c);
            }
        });

        // Setup the screen
        updateTextViews();
        updateList();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Constants.notificationTitle = "Alarm for " + db.courseDao().getCourse(courseId).getMTitle();
        Intent intent = new Intent(this, CourseDetailActivity.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 2, intent, 0);
        am.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pi);
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
            assessmentString[i] = "Title: " + assessments.get(i).getMTitle() + " | Status: " + assessments.get(i).getMStatus();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assessmentString);
        assessmentListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
