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

import Database.WGUTermRoomDatabase;
import Model.Course;
import Model.Mentor;

public class CourseEditActivity extends AppCompatActivity {

    EditText courseNameEditText;
    boolean isEditing;
    WGUTermRoomDatabase db;
    Course course;
    long courseId;
    java.util.Date startDate, endDate;
    String courseName, title;
    Button cancelButton, deleteButton, saveButton, setStartButton, setEndButton;

    TextView startText, endText;

    int month, day, year;
    Calendar calendar;


    Spinner mentorSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        mentorSpinner = findViewById(R.id.mentorSpinner);
        populateMentorSpinner();



        Intent intent = getIntent();

        //TextViews
        courseNameEditText = findViewById(R.id.courseNameEditText);
        startText = findViewById(R.id.courseStartDateTextView);
        endText = findViewById(R.id.courseEndDateValueTextView);

        //Buttons
        saveButton = findViewById(R.id.saveCourseButton);
        cancelButton = findViewById(R.id.cancelCourseButton);
        deleteButton = findViewById(R.id.deleteCourseButton);
        setStartButton = findViewById(R.id.setCourseStartDateButton);
        setEndButton = findViewById(R.id.setCourseEndDateButton);

        isEditing = intent.getBooleanExtra("isEditing", false);
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
                                db.courseDao().delete(course);
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


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] validationString = createValidationString();
                if (!inputIsValid(validationString)) {
                    Toast.makeText(getApplicationContext(), "The course name cannot be blank.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        courseName = courseNameEditText.getText().toString();
                        course.setMTitle(courseName);
                        course.setMStartDate(startDate);
                        course.setMEndDate(endDate);
                        String status = mentorSpinner.getSelectedItem().toString();
                        course.setMStatus(status);
                        if (!isEditing) {
                            db.courseDao().insert(course);
                        } else {
                            db.courseDao().update(course);
                        }
                        Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.d("InsertTerm", ex.getLocalizedMessage());
                    }
                }
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

    public void setStartDate(View view) {
        showDialog(999);
    }

    public void setEndDate(View view) {
        showDialog(998);
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
                    createStartDate(year, month, day);
                }
            };

    private void createStartDate(int year, int month, int day) {
        this.startDate = Date.valueOf(DataProvider.Formatter.formatDate(year, month, day));
    }

    private DatePickerDialog.OnDateSetListener endDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    year = arg1;
                    month = arg2;
                    day = arg3;
                    showDate(year, month + 1, day, endText);
                    createEndDate(year, month, day);
                }
            };

    private void createEndDate(int year, int month, int day) {
        this.endDate = Date.valueOf(DataProvider.Formatter.formatDate(year, month, day));
    }

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

    private boolean inputIsValid(String[] things) {
        for (String item : things) {
            if (item.isEmpty()) {
                return false;
            }
        }

        return true;
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
