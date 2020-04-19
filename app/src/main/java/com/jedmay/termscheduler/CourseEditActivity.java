package com.jedmay.termscheduler;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DataProvider.Formatter;
import DataProvider.Validator;
import Database.WGUTermRoomDatabase;
import Model.Assessment;
import Model.Course;
import Model.Mentor;

public class CourseEditActivity extends AppCompatActivity {

    EditText courseNameEditText;
    boolean isEditing;
    WGUTermRoomDatabase db;
    Course course;
    long courseId;
    long termId;
    Date startDate, endDate;
    String courseName, title;
    Button cancelButton, deleteButton, saveButton, editMentorButton, setStartButton, setEndButton;

    TextView startText, endText;

    int month, day, year;
    Calendar calendar;

    Spinner mentorSpinner, statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        statusSpinner = findViewById(R.id.courseStatusSpinner);
        mentorSpinner = findViewById(R.id.mentorSpinner);
        populateMentorSpinner();

        Intent intent = getIntent();

        //Text and Edit Views
        courseNameEditText = findViewById(R.id.courseNameEditText);
        startText = findViewById(R.id.courseStartDateValueTextView);
        endText = findViewById(R.id.courseEndDateValueTextView);

        //Buttons
        editMentorButton = findViewById(R.id.editMentorButton);
        saveButton = findViewById(R.id.saveCourseButton);
        cancelButton = findViewById(R.id.cancelCourseButton);
        deleteButton = findViewById(R.id.deleteCourseButton);
        setStartButton = findViewById(R.id.setCourseStartDateButton);
        setEndButton = findViewById(R.id.setCourseEndDateButton);

        isEditing = intent.getBooleanExtra("isEditing", false);
        if (isEditing) {
            courseId = intent.getLongExtra("courseId", 0);
            course = db.courseDao().getCourse(courseId);
            //setup the variables
            startDate = course.getMStartDate();
            endDate = course.getMEndDate();
            courseName = course.getMTitle();
            termId = db.courseDao().getCourse(courseId).getMTermId();

            //setup controls
            deleteButton.setVisibility(View.VISIBLE);
            for(int i = 0; i < statusSpinner.getAdapter().getCount(); i++) {
                if(statusSpinner.getAdapter().getItem(i).toString().contains(course.getMStatus())) {
                    statusSpinner.setSelection(i);
                }
            }

            for(int i = 0; i < mentorSpinner.getAdapter().getCount(); i++) {
                if(mentorSpinner.getAdapter().getItem(i).toString().contains(db.mentorDao().getMentor(course.getMMentorId()).getMName())) {
                    mentorSpinner.setSelection(i);
                }
            }

        } else {
            deleteButton.setVisibility(View.INVISIBLE);
            course = new Course();
            termId = intent.getLongExtra("termId", 0);
            course.setMTermId(termId);
        }

        //Populate Data
        calendar = Calendar.getInstance();

        setUpDates();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        startDate = calendar.getTime();
        endDate = calendar.getTime();

        populateScreenWithExistingData(isEditing);

        //Listeners

        mentorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mentorSpinner.getSelectedItem().toString().equals("<Add Mentor>")) {
                    Intent intent = new Intent(getApplicationContext(), MentorEditActivity.class);
                    intent.putExtra("previousActivity", CourseEditActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to quit editing?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to delete this course?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                List<Assessment> assessments = db.assessmentDao().getAllAssessments();
                                long[] assessmentIds = new long[assessments.size()];
                                for (int i = 0; i < assessments.size(); i++) {
                                    assessmentIds[i] = assessments.get(i).getMCourseId();
                                }

                                if (Validator.objectHasDependencies(assessmentIds, courseId)) {
                                    Toast.makeText(getApplicationContext(), "This course cannot be deleted. It has assessments assigned to it that must be deleted first.", Toast.LENGTH_LONG).show();
                                } else {
                                    db.courseDao().delete(course);
                                    Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
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
                if (!DataProvider.Validator.stringsAreNotEmpty(validationString)) {
                    Toast.makeText(getApplicationContext(), "The course name cannot be blank.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        courseName = courseNameEditText.getText().toString();
                        course.setMTitle(courseName);
                        course.setMStartDate(startDate);
                        course.setMEndDate(endDate);
                        course.setMTermId(termId);
                        String status = statusSpinner.getSelectedItem().toString();
                        course.setMStatus(status);
                        String mentorName = mentorSpinner.getSelectedItem().toString();
                        long mentorId = db.mentorDao().getMentorIdFromName(mentorName);
                        course.setMMentorId(mentorId);
                        if (!isEditing) {
                            courseId = db.courseDao().insert(course);
                        } else {
                            db.courseDao().update(course);
                        }
                        Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                        intent.putExtra("courseId", courseId);
                        startActivity(intent);
                        finish();
                    } catch (Exception ex) {
                        Log.d("InsertTerm", ex.getLocalizedMessage());
                    }
                }
            }
        });

        editMentorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MentorEditActivity.class);
                String mentorName = mentorSpinner.getSelectedItem().toString();
                long mentorId = db.mentorDao().getMentorIdFromName(mentorName);
                intent.putExtra("mentorId", mentorId);
                intent.putExtra("isEditing", true);
                startActivity(intent);
            }
        });

        setStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });
        setEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(998);
            }
        });
    }

    private void setUpDates() {
        String startDate;
        String endDate;
        Calendar tempCalendar = Calendar.getInstance();

        if (isEditing) {
            startDate = DataProvider.Formatter.formatDate(db.courseDao().getCourse(courseId).getMStartDate());
            endDate = DataProvider.Formatter.formatDate(db.courseDao().getCourse(courseId).getMEndDate());
        } else {
            startDate = DataProvider.Formatter.formatDate(tempCalendar.getTime());
            endDate = DataProvider.Formatter.formatDate(tempCalendar.getTime());
        }
        startText.setText(startDate);
        endText.setText(endDate);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    startDateListener, year, month, day);
        } else if (id == 998) {
            return new DatePickerDialog(this,
                    endDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view,
                                      int arg1, int arg2, int arg3) {
                    year = arg1;
                    month = arg2;
                    day = arg3;
                    showDate(year, month + 1, day, startText);
                    startDate = Formatter.convertIntegersToDate(year, month, day);
                }
            };

    private DatePickerDialog.OnDateSetListener endDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    year = arg1;
                    month = arg2;
                    day = arg3;
                    showDate(year, month + 1, day, endText);
                    endDate = Formatter.convertIntegersToDate(year, month, day);
                }
            };

    private void showDate(int year, int month, int day, TextView textView) {

        String monthString = new DateFormatSymbols().getMonths()[month - 1];

        textView.setText(new StringBuilder().append(monthString).append(" ")
                .append(day).append(", ").append(year));

    }

    private String[] createValidationString() {
        return new String[]{
                courseNameEditText.getText().toString()
        };
    }

    private void populateScreenWithExistingData(boolean isEditing) {
        if (!isEditing) {
            title = "New Course";
            setTitle(title);
            return;
        }
        course = db.courseDao().getCourse(courseId);
        courseNameEditText.setText(course.getMTitle());
        title = course.getMTitle();
        setTitle(title);
    }

    private void populateMentorSpinner() {
        try {

            List<Mentor> mentors = db.mentorDao().getAllMentors();
            String[] mentorArray = new String[mentors.size() + 1];

            for(int i = 0; i < mentors.size(); i++) {
                mentorArray[i] = mentors.get(i).getMName();
            }

            String addMentor = "<Add Mentor>";
            mentorArray[mentors.size()] = addMentor;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, mentorArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mentorSpinner.setAdapter(adapter);
        } catch (Exception ex) {
            Log.d("MentorSpinner", ex.getLocalizedMessage());
        }

    }

}
