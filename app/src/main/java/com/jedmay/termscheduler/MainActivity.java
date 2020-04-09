package com.jedmay.termscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import DataProvider.AssessmentStatus;
import DataProvider.CourseStatus;
import DataProvider.SampleData;
import Database.WGUTermRoomDatabase;
import Model.Assessment;
import Model.Course;
import Model.Mentor;
import Model.Note;
import Model.Term;

public class MainActivity extends AppCompatActivity {

    Button clearDatabase;
    Button createSampleDataButton;
    Button goToTermsActivity;
    Button showMeSomethingButton;
    SampleData sampleData;
    WGUTermRoomDatabase db;

    TextView inProgressCourseTextView;
    TextView completedCourseTextView;
    TextView droppedCourseTextView;
    TextView failedCourseTextView;

    TextView inProgressAssessmentTextView;
    TextView passedAssessmentTextView;
    TextView failedAssessmentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database
        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        //Sample Data
        sampleData = new SampleData();

        sampleData.deleteAllDataFromDatabase(getApplicationContext());

        //Buttons
        clearDatabase = findViewById(R.id.clearDatabaseButton);
        createSampleDataButton = findViewById(R.id.createSampleDataButton);
        goToTermsActivity = findViewById(R.id.termsButton);

        //Listeners for Buttons
        createClearDatabaseOnClickListener(clearDatabase);
        createCreateSampleDataButtonListener(createSampleDataButton);
        createGoToTermsActivityListener(goToTermsActivity);

        //TextViews
        inProgressCourseTextView = findViewById(R.id.inProgressCountTextView);
        completedCourseTextView = findViewById(R.id.completedCountTextView);
        droppedCourseTextView = findViewById(R.id.droppedCountTextView);
        failedCourseTextView = findViewById(R.id.failedCoursesCountTextView);

        inProgressAssessmentTextView = findViewById(R.id.inProgressAssessmentCountTextView);
        passedAssessmentTextView = findViewById(R.id.passedCountTextView);
        failedAssessmentTextView = findViewById(R.id.failedAssessmentsCountTextView);

        //Get totals for courses
        //getTotalsForCourses();

        //Get totals for assessments
        //getTotalsForAssessments();

    }

    private void getTotalsForCourses() {
        inProgressAssessmentTextView.setText(db.courseDao().getCountOfCourseType(CourseStatus.IN_PROGRESS));
        completedCourseTextView.setText(db.courseDao().getCountOfCourseType(CourseStatus.COMPLETED));
        droppedCourseTextView.setText(db.courseDao().getCountOfCourseType(CourseStatus.DROPPED));
        failedCourseTextView.setText(db.courseDao().getCountOfCourseType(CourseStatus.FAILED));
    }

    private void getTotalsForAssessments() {
        inProgressAssessmentTextView.setText(db.assessmentDao().getCountOfAssessmentType(AssessmentStatus.PLANNED));
        passedAssessmentTextView.setText(db.assessmentDao().getCountOfAssessmentType(AssessmentStatus.PASSED));
        failedAssessmentTextView.setText(db.assessmentDao().getCountOfAssessmentType(AssessmentStatus.FAILED));
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void createCreateSampleDataButtonListener(Button createSampleDataButton) {

        createSampleDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleData.populateDatabaseWithSampleData(getApplicationContext());

            }
        });
    }

    private void createGoToTermsActivityListener(Button goToTermsActivity) {

        goToTermsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You Clicked Terms", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void createClearDatabaseOnClickListener(Button clearDatabase) {

        clearDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleData.deleteAllDataFromDatabase(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Database Cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void updateList() {
    }


}
