package com.jedmay.termscheduler;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.jedmay.termscheduler.dataProvider.Formatter;
import com.jedmay.termscheduler.dataProvider.Validator;
import com.jedmay.termscheduler.database.WGUTermRoomDatabase;
import com.jedmay.termscheduler.model.Assessment;
import com.jedmay.termscheduler.notificationProvider.DatePickerFragment;

import java.util.Calendar;
import java.util.Date;

public class AssessmentEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    WGUTermRoomDatabase db;
    Assessment assessment;
    long assessmentId;
    long courseId;
    String title;
    boolean isEditing;
    Date plannedDate;

    Intent intent;
    Button cancelButton, deleteButton, saveButton, setDateButton;
    TextView plannedDateTextView;
    EditText assessmentNameEditView;

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
        assessmentNameEditView = findViewById(R.id.assessmentNameEditText);
        setDateButton = findViewById(R.id.setAssessmentPlannedDateButton);
        plannedDateTextView = findViewById(R.id.assessmentPlannedDateValueTextView);
        statusSpinner = findViewById(R.id.assessmentStatusSpinner);
        typeSpinner = findViewById(R.id.assessmentTypeSpinner);

        intent = getIntent();
        isEditing = intent.getBooleanExtra("isEditing", false);

        if (isEditing) {
            try {
                assessmentId = intent.getLongExtra("assessmentId", 0);
                assessment = db.assessmentDao().getAssessment(assessmentId);
                courseId = assessment.getMCourseId();
                title = "Edit " + assessment.getMTitle();
                assessmentNameEditView.setText(assessment.getMTitle());
                plannedDate = assessment.getMPlannedDate();
                plannedDateTextView.setText(Formatter.formatDate(plannedDate));
            } catch (Exception ex) {
                Log.d("editingAssessment", ex.getLocalizedMessage());
            }

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
            title = "New Assessment";
        }
        setTitle(title);

        setUpDates();

        //On-click listeners
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssessmentEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to quit editing?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(getApplicationContext(), AssessmentDetailActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssessmentEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to delete this Assessment?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                    db.assessmentDao().delete(assessment);
                                    Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                                    intent.putExtra("courseid", courseId);
                                    startActivity(intent);
                                    finish();
                            }
                        });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] validationString = createValidationString();

                if (!Validator.stringsAreNotEmpty(validationString)) {
                    Toast.makeText(getApplicationContext(), "The assessment name cannot be blank.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        assessment.setMTitle(assessmentNameEditView.getText().toString());
                        assessment.setMPlannedDate(plannedDate);
                        assessment.setMCourseId(courseId);
                        assessment.setMStatus(statusSpinner.getSelectedItem().toString());
                        assessment.setMType(typeSpinner.getSelectedItem().toString());
                        if (!isEditing) {
                            assessmentId = db.assessmentDao().insert(assessment);
                        } else {
                            db.assessmentDao().update(assessment);
                        }
                        Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                        intent.putExtra("courseId", courseId);
                        startActivity(intent);
                        finish();
                    } catch (Exception ex) {
                        Log.d("InsertAssessment", ex.getLocalizedMessage());
                    }
                }
            }
        });

        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker Fragment");
                //showDialog(999);
            }
        });

    }

    private void setUpDates() {
        String plannedDate;
        Calendar c = Calendar.getInstance();

        if (isEditing) {
            plannedDate = Formatter.formatDate(db.assessmentDao().getAssessment(assessmentId).getMPlannedDate());
        } else {
            plannedDate = Formatter.formatDate(c.getTime());
        }
        plannedDateTextView.setText(plannedDate);

    }

    private String[] createValidationString() {
        return new String[] {assessmentNameEditView.getText().toString()};
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        this.plannedDate = c.getTime();
        plannedDateTextView.setText(Formatter.formatDate(plannedDate));

    }
}
