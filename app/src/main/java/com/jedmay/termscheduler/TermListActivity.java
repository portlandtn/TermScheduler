package com.jedmay.termscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import com.jedmay.termscheduler.database.WGUTermRoomDatabase;
import com.jedmay.termscheduler.model.Term;

public class TermListActivity extends AppCompatActivity {

    WGUTermRoomDatabase db;
    ListView listView;
    String title = "Term List";
    FloatingActionButton addTermFAB;
    List<Term> terms;

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);

        //General Setup
        setTitle(title);
        addTermFAB = findViewById(R.id.addTermFAB);

        //Database
        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        //Update ListView
        listView = findViewById(R.id.termListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TermDetailActivity.class);
                terms = db.termDao().getAllTerms();
                long termId = terms.get(position).getId();
                intent.putExtra("termId", termId);
                startActivity(intent);
            }
        });
        updateList();

        addTermFAB.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Intent intent = new Intent(getApplicationContext(), TermEditActivity.class);
                                       startActivity(intent);
                                   }
                               }
        );
    }




    private void updateList() {
        terms = db.termDao().getAllTerms();
        String[] termsString = new String[terms.size()];

        for (int i = 0; i < terms.size(); i++) {
            termsString[i] = terms.get(i).getMTitle();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, termsString);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
