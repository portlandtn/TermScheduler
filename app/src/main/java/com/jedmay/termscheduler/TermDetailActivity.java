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

import com.jedmay.termscheduler.dataProvider.Formatter;
import com.jedmay.termscheduler.database.WGUTermRoomDatabase;
import com.jedmay.termscheduler.model.Course;
import com.jedmay.termscheduler.model.Term;

public class TermDetailActivity extends AppCompatActivity {

    Term term;
    long termId;
    String title;
    WGUTermRoomDatabase db;
    Intent intent;
    TextView startDateValueTextView, endDateValueTextView;
    FloatingActionButton addCourseToTerm, editTermFAB;
    List<Course> courses;
    ListView courseListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        // Setup resources
        startDateValueTextView = findViewById(R.id.termStartDateValueTextView);
        endDateValueTextView = findViewById(R.id.termEndDateValueTextView);
        addCourseToTerm = findViewById(R.id.addCourseToTermFAB);
        editTermFAB = findViewById(R.id.editTermFAB);
        courseListView = findViewById(R.id.courseListView);
        intent = getIntent();
        termId = intent.getLongExtra("termId", 0);

        //On-click Listeners
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), CourseDetailActivity.class);
                courses = db.courseDao().getCoursesForTerm(termId);
                long courseId = courses.get(position).getId();
                i.putExtra("courseId", courseId);
                startActivity(i);
            }
        });

        //Edit Term on click Listener
        editTermFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TermEditActivity.class);
                i.putExtra("termId", termId);
                i.putExtra("isEditing", true);
                startActivity(i);
            }
        });

        //Add Course to Term on click Listener
        addCourseToTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseEditActivity.class);
                intent.putExtra("termId", termId);
                startActivity(intent);
            }
        });

        //Setup the screen
        updateTextViews();
        updateList();

    }

    private void updateTextViews() {

        try {
            term = db.termDao().getTerm(termId);
            title = term.getMTitle();
            setTitle(title + " Detail");
            String start = Formatter.formatDate(term.getMStartDate());
            String end = Formatter.formatDate(term.getMEndDate());
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
        courseListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
