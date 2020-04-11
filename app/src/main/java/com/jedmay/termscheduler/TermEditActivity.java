package com.jedmay.termscheduler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
//    int month, day, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        Intent intent = getIntent();
        isEditing = intent.getBooleanExtra("isEditing", false);

        termNameEditText = findViewById(R.id.termNameEditText);
        startDateEditText = findViewById(R.id.startDate_DatePicker);
        endDateEditText = findViewById(R.id.endDate_DatePicker);

        populateScreenWithExistingData(isEditing, intent);

        saveButton = findViewById(R.id.saveTermButton);
        cancelButton = findViewById(R.id.cancelTermButton);

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
