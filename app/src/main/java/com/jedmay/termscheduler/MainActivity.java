package com.jedmay.termscheduler;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import DataProvider.SampleData;
import Database.WGUTermRoomDatabase;

public class MainActivity extends AppCompatActivity {

    Button clearDatabase, createSampleDataButton, goToTermsActivity;

    SampleData sampleData;
    WGUTermRoomDatabase db;

    String title = "WGU Scheduler";

    TextView inProgressCourseTextView, completedCourseTextView, droppedCourseTextView, failedCourseTextView, plannedToTakeTextView;

    TextView inProgressAssessmentTextView, passedAssessmentTextView, failedAssessmentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(title);

        //Database
        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        //Sample Data
        sampleData = new SampleData();

        //Buttons
        clearDatabase = findViewById(R.id.clearDatabaseButton);
        createSampleDataButton = findViewById(R.id.createSampleDataButton);
        goToTermsActivity = findViewById(R.id.goToTermsButton);

        //Listeners for Buttons
        createClearDatabaseOnClickListener(clearDatabase);
        createCreateSampleDataButtonListener(createSampleDataButton);
        createGoToTermsActivityListener(goToTermsActivity);

        //TextViews
        inProgressCourseTextView = findViewById(R.id.coursesInProgressCountTextView);
        completedCourseTextView = findViewById(R.id.coursesCompletedCountTextView);
        droppedCourseTextView = findViewById(R.id.coursesDroppedCountTextView);
        failedCourseTextView = findViewById(R.id.coursesFailedCountTextView);
        plannedToTakeTextView = findViewById(R.id.coursesPlannedToTakeCountTextView);

        inProgressAssessmentTextView = findViewById(R.id.assessmentsPlannedCountTextView);
        passedAssessmentTextView = findViewById(R.id.assessmentsPassedCountTextView);
        failedAssessmentTextView = findViewById(R.id.assessmentsFailedCountTextView);

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
            inProgressCourseTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType("In Progress")));
            completedCourseTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType("Completed")));
            droppedCourseTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType("Dropped")));
            failedCourseTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType("Failed")));
            plannedToTakeTextView.setText(String.valueOf(db.courseDao().getCountOfCourseType("Plan to take")));

        } catch (Exception ex) {
            Log.d("TotalsForCourse",ex.getLocalizedMessage());
        }

    }

    private void getTotalsForAssessments() {
        try {
            inProgressAssessmentTextView.setText(String.valueOf(db.assessmentDao().getCountOfAssessmentType("Planned")));
            passedAssessmentTextView.setText(String.valueOf(db.assessmentDao().getCountOfAssessmentType("Passed")));
            failedAssessmentTextView.setText(String.valueOf(db.assessmentDao().getCountOfAssessmentType("Failed")));
        } catch (Exception ex) {
            Log.d("TotalsForAssessments",ex.getLocalizedMessage());
        }
    }

}
