package com.jedmay.termscheduler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.util.Calendar;
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
    java.util.Date startDate, endDate;
    String courseName, title;
    Button cancelButton, deleteButton, saveButton, setStartButton, setEndButton;

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
        saveButton = findViewById(R.id.saveCourseButton);
        cancelButton = findViewById(R.id.cancelCourseButton);
        deleteButton = findViewById(R.id.deleteCourseButton);
        setStartButton = findViewById(R.id.setCourseStartDateButton);
        setEndButton = findViewById(R.id.setCourseEndDateButton);

        isEditing = intent.getBooleanExtra("isEditing", false);
        termId = intent.getLongExtra("termId", 0);
        if (isEditing) {
            courseId = intent.getLongExtra("courseId", 0);
            course = db.courseDao().getCourse(courseId);
            startDate = course.getMStartDate();
            endDate = course.getMEndDate();
            courseName = course.getMTitle();
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
            course = new Course();
            course.setMTermId(termId);
        }

        //Populate Data
        calendar = Calendar.getInstance();

        setUpDates();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        populateScreenWithExistingData(isEditing);

        //Listeners
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
                    } catch (Exception ex) {
                        Log.d("InsertTerm", ex.getLocalizedMessage());
                    }
                }
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
            tempCalendar.add(Calendar.MONTH, 6);
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
                    startDate = Formatter.convertDateToJavaSQL(year, month, day);
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
                    endDate = Formatter.convertDateToJavaSQL(year, month, day);
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
            String[] mentorArray = new String[mentors.size()];

            for(int i = 0; i < mentors.size(); i++) {
                mentorArray[i] = mentors.get(i).getMName();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, mentorArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mentorSpinner.setAdapter(adapter);
        } catch (Exception ex) {
            Log.d("MentorSpinner", ex.getLocalizedMessage());
        }

    }

}
