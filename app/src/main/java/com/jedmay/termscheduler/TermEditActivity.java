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

import java.util.Calendar;

import Database.WGUTermRoomDatabase;
import Model.Term;

public class TermEditActivity extends AppCompatActivity {

    EditText termNameEditText;
    EditText startDateEditText;
    EditText endDateEditText;
    boolean isEditing;
    WGUTermRoomDatabase db;
    Term term;
    Button cancelButton;
    Button saveButton;

    TextView startText;
    TextView endText;
    Button setStart;
    Button setEnd;

    int month, day, year;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
  //      showDate(year, month, day, startText);
    //    showDate(year, month, day, endText);

        Intent intent = getIntent();
        isEditing = intent.getBooleanExtra("isEditing", false);
        Toast.makeText(getApplicationContext(), "Editing = " + isEditing, Toast.LENGTH_SHORT).show();

        termNameEditText = findViewById(R.id.termNameEditText);
        startDateEditText = findViewById(R.id.startDate_DatePicker);
        endDateEditText = findViewById(R.id.endDate_DatePicker);

        populateScreenWithExistingData(isEditing, intent);

        saveButton = findViewById(R.id.saveTermButton);
        cancelButton = findViewById(R.id.cancelTermButton);
        setStart = findViewById(R.id.setStartDate);
        setEnd = findViewById(R.id.setEndDate);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputIsValid(termNameEditText.getText().toString(), startDateEditText.getText().toString(), endDateEditText.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "At a minimum, a term must have a name, start date, and end date", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        db.termDao().insert(term);
                        Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.d("InsertTerm", ex.getLocalizedMessage());
                    }
                }
            }
        });

    }

    public void setStartDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Set Start Date Method Entered", Toast.LENGTH_SHORT).show();

    }

    public void setEndDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Set End Date Method Entered", Toast.LENGTH_SHORT).show();
    }

    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "SetDate Method Entered", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    startDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                     arg1 = year;
                     arg2 = month;
                     arg3 = day;
                    showDate(arg1, arg2+1, arg3, startText);
                }
            };

    private DatePickerDialog.OnDateSetListener endDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    arg1 = year;
                    arg2 = month;
                    arg3 = day;
                    showDate(arg1, arg2+1, arg3, endText);
                }
            };

    private void showDate(int year, int month, int day, TextView textView) {
        textView.setText(new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year));
    }

    private boolean inputIsValid(String termName, String startDate, String endDate) {
        return !termName.isEmpty() && !startDate.isEmpty() && endDate.isEmpty();
    }

    private void populateScreenWithExistingData(boolean isEditing, Intent intent) {
        if (!isEditing) return;

        long termId = intent.getLongExtra("termId", 0);
        term = db.termDao().getTerm(termId);

        termNameEditText.setText(term.getMTitle());
        startDateEditText.setText(String.valueOf(term.getMStartDate()));
        endDateEditText.setText(String.valueOf(term.getMEndDate()));

    }
}
