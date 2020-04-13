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

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import Database.WGUTermRoomDatabase;
import Model.Term;

public class TermEditActivity extends AppCompatActivity {

    EditText termNameEditText;
    boolean isEditing;
    WGUTermRoomDatabase db;
    Term term;
    long termId;
    protected java.util.Date startDate, endDate;
    String termName;
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


        //TextViews
        termNameEditText = findViewById(R.id.termNameEditText);
        startText = findViewById(R.id.startDateValueTextView);
        endText = findViewById(R.id.endDateValueTextView);

        //Buttons
        saveButton = findViewById(R.id.saveTermButton);
        cancelButton = findViewById(R.id.cancelTermButton);
        deleteButton = findViewById(R.id.deleteTermButton);
        setStartButton = findViewById(R.id.setStartDateButton);
        setEndButton = findViewById(R.id.setEndDateButton);

        isEditing = intent.getBooleanExtra("isEditing", false);
        if (isEditing) {
            termId = intent.getLongExtra("termId", 0);
            term = db.termDao().getTerm(termId);
            startDate = term.getMStartDate();
            endDate = term.getMEndDate();
            termName = term.getMTitle();
            deleteButton.setEnabled(true);
        } else {
            deleteButton.setEnabled(false);
            term = new Term();
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
                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
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
                                                                db.termDao().delete(term);
                                                                Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
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
                        if (!inputIsValid(termNameEditText.getText().toString(), startText.getText().toString(), endText.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "At a minimum, a term must have a name, start date, and end date", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                termName = termNameEditText.getText().toString();
                                term.setMTitle(termName);
                                term.setMStartDate(startDate);
                                term.setMEndDate(endDate);
                                if (!isEditing) {
                                    db.termDao().insert(term);
                                } else {
                                    db.termDao().update(term);
                                }
                                Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
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
            startDate = DataProvider.Formatter.formatDate(db.termDao().getTerm(termId).getMStartDate());
            endDate = DataProvider.Formatter.formatDate(db.termDao().getTerm(termId).getMEndDate());
        }
        else {
            startDate = DataProvider.Formatter.formatDate(tempCalendar.getTime());
            tempCalendar.add(Calendar.MONTH , 6);
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
        } else
            if (id == 998) {
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
                    showDate(year, month+1, day, startText);
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
                    showDate(year, month+1, day, endText);
                    createEndDate(year, month, day);
                }
            };

    private void createEndDate(int year, int month, int day) {
        this.endDate = Date.valueOf(DataProvider.Formatter.formatDate(year, month, day));
    }

    private void showDate(int year, int month, int day, TextView textView) {

        String monthString = new DateFormatSymbols().getMonths()[month-1];

        textView.setText(new StringBuilder().append(monthString).append(" ")
                .append(day).append(", ").append(year));

    }

    private boolean inputIsValid(String termName, String startDate, String endDate) {
        return !termName.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty();
    }

    private void populateScreenWithExistingData(boolean isEditing) {
        if (!isEditing) return;

        term = db.termDao().getTerm(termId);
        termNameEditText.setText(term.getMTitle());

    }
}
