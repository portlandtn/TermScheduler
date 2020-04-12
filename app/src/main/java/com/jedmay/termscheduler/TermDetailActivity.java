package com.jedmay.termscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Database.WGUTermRoomDatabase;
import Model.Course;
import Model.Term;

public class TermDetailActivity extends AppCompatActivity {

    Term term;
    long termId;
    WGUTermRoomDatabase db;
    Intent intent;
    TextView startDateValueTextView;
    TextView endDateValueTextView;
    FloatingActionButton fab;
    List<Course> courses;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        // Setup resources
        startDateValueTextView = findViewById(R.id.startDateValueTextView);
        endDateValueTextView = findViewById(R.id.endDateValueTextView);
        fab = findViewById(R.id.newCourseFloatingActionButton);
        listView = findViewById(R.id.courseListView);
        intent = getIntent();
        termId = intent.getLongExtra("termId", 0);

        // Setup the screen
        updateTextViews();
        updateList();

    }

    private void updateTextViews() {

        try {
            term = db.termDao().getTerm(termId);
            setTitle(term.getMTitle() + " Detail");
            String start = DataProvider.Formatter.formatDate(term.getMStartDate());
            String end = DataProvider.Formatter.formatDate(term.getMEndDate());
            startDateValueTextView.setText(start);
            endDateValueTextView.setText(end);

            //startDateValueTextView.setText(String.valueOf(term.getMStartDate()));
            //endDateValueTextView.setText(String.valueOf(term.getMEndDate()));
        } catch (Exception ex) {
            Log.d("GetTerm", ex.getLocalizedMessage());
        }

    }

    private void updateList() {

        courses = db.courseDao().getCoursesForTerm(termId);
        String[] courseString = new String[courses.size()];

        for (int i = 0; i < courses.size(); i++) {
            courseString[i] = courses.get(i).getMTitle();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courseString);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
