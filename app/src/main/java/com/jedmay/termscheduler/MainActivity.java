package com.jedmay.termscheduler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
    TextView plannedToTakeTextView;

    TextView inProgressAssessmentTextView;
    TextView passedAssessmentTextView;
    TextView failedAssessmentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("WGU Scheduler");

        //Database
        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        //Sample Data
        sampleData = new SampleData();

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
        plannedToTakeTextView = findViewById(R.id.plannedToTakeCountTextView);

        inProgressAssessmentTextView = findViewById(R.id.inProgressAssessmentCountTextView);
        passedAssessmentTextView = findViewById(R.id.passedCountTextView);
        failedAssessmentTextView = findViewById(R.id.failedAssessmentsCountTextView);

        updateList();

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
                Toast.makeText(getApplicationContext(), "Sample Data Created", Toast.LENGTH_SHORT).show();
                updateList();
            }
        });
    }

    private void createGoToTermsActivityListener(Button goToTermsActivity) {
    try {
        goToTermsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
                startActivity(intent);

            }
        });
    } catch (Exception ex) {
        Log.d("TermsActivityButton", ex.getLocalizedMessage());
    }
    }

    private void createClearDatabaseOnClickListener(Button clearDatabase) {

        clearDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("This will clear out the database. Are you sure you want to proceed?");
                alertDialogBuilder.setPositiveButton("ERASE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                sampleData.deleteAllDataFromDatabase(getApplicationContext());
                                updateList();
                                Toast.makeText(MainActivity.this,"Database has been erased.",Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        });
    }

    private void updateList() {
        getTotalsForCourses();
        getTotalsForAssessments();
    }

    private void getTotalsForCourses() {
        try {
            inProgressCourseTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType(CourseStatus.IN_PROGRESS)));
            completedCourseTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType(CourseStatus.COMPLETED)));
            droppedCourseTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType(CourseStatus.DROPPED)));
            failedCourseTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType(CourseStatus.FAILED)));
            plannedToTakeTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType(CourseStatus.PLAN_TO_TAKE)));

        } catch (Exception ex) {
            Log.d("TotalsForCourse",ex.getLocalizedMessage());
        }

    }

    private void getTotalsForAssessments() {
        try {
            inProgressAssessmentTextView.setText(String.valueOf(db.assessmentDao().getCountOfAssessmentType(AssessmentStatus.PLANNED)));
            passedAssessmentTextView.setText(String.valueOf(db.assessmentDao().getCountOfAssessmentType(AssessmentStatus.PASSED)));
            failedAssessmentTextView.setText(String.valueOf(db.assessmentDao().getCountOfAssessmentType(AssessmentStatus.FAILED)));
        } catch (Exception ex) {
            Log.d("TotalsForAssessments",ex.getLocalizedMessage());
        }
    }

}
