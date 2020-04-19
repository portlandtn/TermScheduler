package com.jedmay.termscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import Database.WGUTermRoomDatabase;
import Model.Assessment;

public class AssessmentEditActivity extends AppCompatActivity {

    WGUTermRoomDatabase db;
    Assessment assessment;
    long assessmentId;
    long courseId;
    String title;
    boolean isEditing;

    Intent intent;
    Button cancelButton, deleteButton, saveButton, setStartDateButton, setEndDateButton;
    TextView startDate, endDate;

    Spinner statusSpinner, typeSpinner;


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_edit);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        //Setup Resources
        cancelButton = findViewById(R.id.cancelAssessmentButton);
        deleteButton = findViewById(R.id.deleteAssessmentButton);
        saveButton = findViewById(R.id.saveAssessmentButton);
        setStartDateButton = findViewById(R.id.setAssessmentStartDateButton);
        setEndDateButton = findViewById(R.id.setAssessmentEndDateButton);
        startDate = findViewById(R.id.startAssessmentDateValueTextView);
        endDate = findViewById(R.id.endAssessmentDateValueTextView);
        statusSpinner = findViewById(R.id.assessmentStatusSpinner);
        typeSpinner = findViewById(R.id.assessmentTypeSpinner);

        intent = getIntent();
        isEditing = intent.getBooleanExtra("isEditing", false);

        if (isEditing) {
            assessmentId = intent.getLongExtra("assessmentId", 0);
            title = "Edit " + assessment.getMTitle();
            startDate.setText(DataProvider.Formatter.formatDate(assessment.getMStartDate()));
            endDate.setText(DataProvider.Formatter.formatDate(assessment.getMEndDate()));

            for(int i = 0; i < statusSpinner.getAdapter().getCount(); i++) {
                if(statusSpinner.getAdapter().getItem(i).toString().contains(assessment.getMStatus())) {
                    statusSpinner.setSelection(i);
                }
            }

            for(int i = 0; i < typeSpinner.getAdapter().getCount(); i++) {
                if(typeSpinner.getAdapter().getItem(i).toString().contains(assessment.getMType())) {
                    typeSpinner.setSelection(i);
                }
            }
            deleteButton.setVisibility(View.VISIBLE);

        }
        else {
            assessment = new Assessment();
            courseId = intent.getLongExtra("courseId", 0);
            assessment.setMCourseId(courseId);
            deleteButton.setVisibility(View.INVISIBLE);

        }
        setTitle(title);

    }


}
