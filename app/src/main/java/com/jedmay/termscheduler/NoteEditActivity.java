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

import Database.WGUTermRoomDatabase;
import Model.Course;
import Model.Note;

public class NoteEditActivity extends AppCompatActivity {

    String title;
    WGUTermRoomDatabase db;

    Intent intent;

    long courseId;
    long noteId;
    Note note;
    boolean isEditing;
    Button cancelButton, deleteButton, saveButton;
    EditText noteEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        cancelButton = findViewById(R.id.cancelNoteButton);
        deleteButton = findViewById(R.id.deleteNoteButton);
        saveButton = findViewById(R.id.saveNoteButton);
        noteEditText = findViewById(R.id.noteEditText);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        intent = getIntent();

        courseId = intent.getLongExtra("courseId", 0);
        isEditing = intent.getBooleanExtra("isEditing", false);

        if (isEditing) {
            noteId = intent.getLongExtra("noteId", 0);
            note = db.noteDao().getNote(noteId);
            title = "Edit Note for " + db.courseDao().getCourse(courseId).getMTitle();
            noteEditText.setText(note.getMNote());
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            note = new Note();
            note.setMCourseId(courseId);
            title = "New Note for " + db.courseDao().getCourse(courseId).getMTitle();
            deleteButton.setVisibility(View.INVISIBLE);
        }
        setTitle(title);

        // On Click Listeners
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NoteEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to quit editing?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                                intent.putExtra("courseId", courseId);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NoteEditActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to delete this note?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                db.noteDao().delete(note);
                                Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                                intent.putExtra("courseId", courseId);
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
                if (noteEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "The note cannot be blank.", Toast.LENGTH_SHORT).show();
                } else {
                    String textString = noteEditText.getText().toString();
                    note.setMNote(noteEditText.getText().toString());
                    Course course = db.courseDao().getCourse(courseId);
                    try {
                        if (!isEditing) {
                            db.noteDao().insert(note);
                        } else {
                            db.noteDao().update(note);
                        }
                    } catch (Exception ex) {
                        Log.d("SaveNote", ex.getLocalizedMessage());
                    }
                    Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                    intent.putExtra("courseId", courseId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
