package com.jedmay.termscheduler;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jedmay.termscheduler.dataProvider.Validator;
import com.jedmay.termscheduler.database.WGUTermRoomDatabase;
import com.jedmay.termscheduler.model.Mentor;

public class MentorEditActivity extends AppCompatActivity {

    boolean isEditing;
    long mentorId, courseId;
    Mentor mentor;
    String title;

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
        isEditing = intent.getBooleanExtra("isEditing", false);

        if (isEditing) {
            mentorId = intent.getLongExtra("mentorId", 0);
            courseId = intent.getLongExtra("courseId", 0);
            mentor = db.mentorDao().getMentor(mentorId);
            title = "Edit " + mentor.getMName();

            mentorName.setText(mentor.getMName());
            mentorEmailAddress.setText(mentor.getMEmail());
            mentorPhoneNumber.setText(mentor.getMPhone());
        } else {
            mentor = new Mentor();
            title = "New Mentor";
        }
        setTitle(title);

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
                                createAndStartIntent();
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
                                createAndStartIntent();
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
                    Toast.makeText(getApplicationContext(), "The mentor name cannot be blank.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        mentor.setMName(mentorName.getText().toString());
                        mentor.setMEmail(mentorEmailAddress.getText().toString());
                        mentor.setMPhone(mentorPhoneNumber.getText().toString());

                        if (!isEditing) {
                            mentorId = db.mentorDao().insert(mentor);
                        } else {
                            db.mentorDao().update(mentor);
                        }

                        //The only way to get to this screen is from the CourseEditActivity, while editing a course.
                        createAndStartIntent();
                    } catch (Exception ex) {
                        Log.d("InsertMentor", ex.getLocalizedMessage());
                    }
                }
            }
        });

    }

    private String[] createValidationString() {
        return new String[] {mentorName.getText().toString()};
    }

    private void createAndStartIntent() {
        Intent intent= new Intent(getApplicationContext(), CourseEditActivity.class);;
        intent.putExtra("isEditing", true);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
        finish();
    }
}
