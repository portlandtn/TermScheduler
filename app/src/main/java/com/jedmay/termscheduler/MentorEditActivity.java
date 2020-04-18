package com.jedmay.termscheduler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import DataProvider.Validator;
import Database.WGUTermRoomDatabase;
import Model.Course;
import Model.Mentor;

public class MentorEditActivity extends AppCompatActivity {

    boolean isEditing;
    long mentorId;
    Mentor mentor;

    EditText mentorName, mentorEmailAddress, mentorPhoneNumber;
    Button cancelButton, deleteButton, saveButton;

    WGUTermRoomDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_edit);

        //Setup
        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        mentorName = findViewById(R.id.mentorNameEditText);
        mentorEmailAddress = findViewById(R.id.mentorEmailAddressEditText);
        mentorPhoneNumber = findViewById(R.id.mentorPhoneEditText);

        cancelButton = findViewById(R.id.cancelEditMentorButton);
        deleteButton = findViewById(R.id.deleteMentorButton);
        saveButton = findViewById(R.id.saveMentorButton);
        Intent intent = getIntent();


        if (isEditing) {
            isEditing = intent.getBooleanExtra("isEditing", false);
            mentorId = intent.getLongExtra("mentorId", 0);
            mentor = db.mentorDao().getMentor(mentorId);

            mentorName.setText(mentor.getMName());
            mentorEmailAddress.setText(mentor.getMEmail());
            mentorPhoneNumber.setText(mentor.getMPhone());
        } else {
            mentor = new Mentor();
        }

        // Listeners
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MentorEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to quit editing?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent;
                                intent = isEditing ? new Intent(getApplicationContext(), MentorListActivity.class) : new Intent(getApplicationContext(), CourseEditActivity.class);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MentorEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to delete this mentor?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                    db.mentorDao().delete(mentor);
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

    }
}
