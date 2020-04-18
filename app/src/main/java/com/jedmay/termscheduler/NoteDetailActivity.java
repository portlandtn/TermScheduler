package com.jedmay.termscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Database.WGUTermRoomDatabase;
import Model.Note;

public class NoteDetailActivity extends AppCompatActivity {

    long courseId;
    String title;

    WGUTermRoomDatabase db;
    ListView noteListView;
    List<Note> notes;
    FloatingActionButton addNoteToCourse;
    Intent intent;

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());
        intent = getIntent();

        //Setup Resources
        courseId = intent.getLongExtra("courseId", 0);
        title = "Notes For " + db.courseDao().getCourse(courseId).getMTitle();
        setTitle(title);

        noteListView = findViewById(R.id.noteLVW);
        notes = db.noteDao().getNotesForCourse(courseId);
        addNoteToCourse = findViewById(R.id.addNoteToCourseFAB);

        // onclick listener to edit a note from the view
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NoteEditActivity.class);
                long noteId = notes.get(position).getId();
                intent.putExtra("noteId", noteId);
                intent.putExtra("courseId", courseId);
                intent.putExtra("isEditing", true);
                startActivity(intent);
            }
        });

        // onclick listener to add a new note to a course
        addNoteToCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteEditActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        updateList();

    }

    private void updateList() {
        String[] notesString = new String[notes.size()];

        for (int i = 0; i < notes.size(); i++) {
            notesString[i] = notes.get(i).getMNote();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesString);
        noteListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
