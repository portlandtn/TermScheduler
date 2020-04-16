package com.jedmay.termscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Database.WGUTermRoomDatabase;
import Model.Assessment;
import Model.Course;

public class CourseDetailActivity extends AppCompatActivity {

    Course course;
    long courseId;
    String title;
    WGUTermRoomDatabase db;
    Intent intent;
    TextView startDateValueTextView, endDateValueTextView, courseStatusValueTextView, mentorValueTextView;
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
        addAssessmentToCourseFAB = findViewById(R.id.addAssessmentToCourseFAB);
        editCourseFAB = findViewById(R.id.editCourseFAB);
        assessmentListView = findViewById(R.id.assessmentListView);
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
            mentorValueTextView.setText(db.mentorDao().getMentorForCourse(courseId).getMName());
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
