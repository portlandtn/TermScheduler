package com.jedmay.termscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    String title;
    WGUTermRoomDatabase db;
    Intent intent;
    TextView startDateValueTextView;
    TextView endDateValueTextView;
    FloatingActionButton addCourseToTerm;
    FloatingActionButton editTermFAB;
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
        addCourseToTerm = findViewById(R.id.addCourseToTermFAB);
        editTermFAB = findViewById(R.id.editTermFAB);
        listView = findViewById(R.id.courseListView);
        intent = getIntent();
        termId = intent.getLongExtra("termId", 0);

        //Courses list ListView on click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO Will need to fix to go to course details
                //Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
//                courses = db.courseDao().getCoursesForTerm(termId);
//                long courseId = courses.get(position).getId();
//                intent.putExtra("courseId", courseId);
//                startActivity(intent);
            }
        });

        //edit Term on click Listener
        editTermFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TermEditActivity.class);
                intent.putExtra("termId", termId);
                intent.putExtra("isEditing", true);
                startActivity(intent);
            }
        });

        //Add Course to Term on click Listener
        addCourseToTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - this will have to be created after
                //Intent intent = new Intent(getApplicationContext(), CourseEditActivity.class);
                //intent.putExtra("termId", termId);
                //startActivity(intent);
            }
        });

        // Setup the screen
        updateTextViews();
        updateList();

    }

    private void updateTextViews() {

        try {
            term = db.termDao().getTerm(termId);
            title = term.getMTitle();
            setTitle(title + " Detail");
            String start = DataProvider.Formatter.formatDate(term.getMStartDate());
            String end = DataProvider.Formatter.formatDate(term.getMEndDate());
            startDateValueTextView.setText(start);
            endDateValueTextView.setText(end);
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
