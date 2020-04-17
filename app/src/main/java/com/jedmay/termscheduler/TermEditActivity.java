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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DataProvider.Formatter;
import DataProvider.Validator;
import Database.WGUTermRoomDatabase;
import Model.Course;
import Model.Term;

public class TermEditActivity extends AppCompatActivity {

    EditText termNameEditText;
    boolean isEditing;
    WGUTermRoomDatabase db;
    Term term;
    long termId;
    Date startDate, endDate;
    String termName, title;
    Button cancelButton, deleteButton, saveButton, setStartButton, setEndButton;
    TextView startText, endText;

    int month, day, year;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        Intent intent = getIntent();

        //Text and Edit Views
        termNameEditText = findViewById(R.id.termNameEditText);
        startText = findViewById(R.id.termStartDateValueTextView);
        endText = findViewById(R.id.termEndDateValueTextView);

        //Buttons
        saveButton = findViewById(R.id.saveTermButton);
        cancelButton = findViewById(R.id.cancelTermButton);
        deleteButton = findViewById(R.id.deleteTermButton);
        setStartButton = findViewById(R.id.setTermStartDateButton);
        setEndButton = findViewById(R.id.setTermEndDateButton);

        isEditing = intent.getBooleanExtra("isEditing", false);

        if (isEditing) {
            termId = intent.getLongExtra("termId", 0);
            term = db.termDao().getTerm(termId);
            startDate = term.getMStartDate();
            endDate = term.getMEndDate();
            termName = term.getMTitle();
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
            term = new Term();
        }

        //Populate Data
        calendar = Calendar.getInstance();

        setUpDates();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        populateScreenWithExistingData(isEditing);

        //On-click listeners
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TermEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to quit editing?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TermEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to delete this term?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                List<Course> courses = db.courseDao().getAllCourses();
                                long[] courseIds = new long[courses.size()];
                                for (int i = 0; i < courses.size(); i++) {
                                    courseIds[i] = courses.get(i).getMTermId();
                                }

                                if (Validator.objectHasDependencies(courseIds, termId)) {
                                    Toast.makeText(getApplicationContext(), "This term cannot be deleted. It has courses in it that must be deleted first.", Toast.LENGTH_LONG).show();
                                } else {
                                    db.termDao().delete(term);
                                    Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
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

                if (!Validator.stringsAreNotEmpty(validationString)) {
                    Toast.makeText(getApplicationContext(), "The term name cannot be blank.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        termName = termNameEditText.getText().toString();
                        term.setMTitle(termName);
                        term.setMStartDate(startDate);
                        term.setMEndDate(endDate);
                        if (!isEditing) {
                            termId = db.termDao().insert(term);
                        } else {
                            db.termDao().update(term);
                        }
                        Intent intent = new Intent(getApplicationContext(), TermDetailActivity.class);
                        intent.putExtra("termId", termId);
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

    private String[] createValidationString() {

        return new String[]{
                termNameEditText.getText().toString(),
                startText.getText().toString(),
                endText.getText().toString()
        };
    }

    private void setUpDates() {
        String startDate;
        String endDate;
        Calendar tempCalendar = Calendar.getInstance();

        if (isEditing) {
            startDate = DataProvider.Formatter.formatDate(db.termDao().getTerm(termId).getMStartDate());
            endDate = DataProvider.Formatter.formatDate(db.termDao().getTerm(termId).getMEndDate());
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

    private void populateScreenWithExistingData(boolean isEditing) {
        if (!isEditing) {
            title = "New Term";
            setTitle(title);
            return;
        }
        term = db.termDao().getTerm(termId);
        termNameEditText.setText(term.getMTitle());
        title = term.getMTitle() + " Detail";
        setTitle(title);
    }
}
