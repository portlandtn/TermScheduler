package com.jedmay.termscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Database.WGUTermRoomDatabase;
import Model.Assessment;

public class AssessmentDetailActivity extends AppCompatActivity {

    WGUTermRoomDatabase db;
    Assessment assessment;
    long assessmentId;
    long courseId;
    String title;

    Intent intent;

    TextView plannedDate, assessmentStatus, assessmentType;
    FloatingActionButton editAssessment;

    @Override
    protected void onResume() {
        super.onResume();
        updateTextViews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        plannedDate = findViewById(R.id.startAssessmentDateValueTextView);
        assessmentStatus = findViewById(R.id.assessmentStatusValueTextView);
        assessmentType = findViewById(R.id.assessmentTypeValueTextView);

        editAssessment = findViewById(R.id.editAssessmentFAB);

        intent = getIntent();
        assessmentId = intent.getLongExtra("assessmentId", 0);
        assessment = db.assessmentDao().getAssessment(assessmentId);
        courseId = assessment.getMCourseId();

        title = "Details for " + assessment.getMTitle();
        setTitle(title);

        editAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AssessmentEditActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("courseId", courseId);
                intent.putExtra("isEditing", true);
                startActivity(intent);
            }
        });

        updateTextViews();

    }

    private void updateTextViews() {
        try {
            plannedDate.setText(DataProvider.Formatter.formatDate(assessment.getMPlannedDate()));
            assessmentStatus.setText(assessment.getMStatus());
            assessmentType.setText(assessment.getMType());
        } catch (Exception ex) {
            Log.d("updateAssTextViews", ex.getLocalizedMessage());
        }
    }

}
