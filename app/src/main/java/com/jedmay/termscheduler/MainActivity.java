package com.jedmay.termscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import DataProvider.SampleData;
import Database.WGUTermRoomDatabase;

public class MainActivity extends AppCompatActivity {

    Button clearDatabase;
    Button createSampleDataButton;
    Button goToTermsActivity;
    Button showMeSomethingButton;
    SampleData sampleData;
    WGUTermRoomDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database
        db = WGUTermRoomDatabase.getDatabase(getApplicationContext());

        //Sample Data
        sampleData = new SampleData();

        //Buttons
        clearDatabase = findViewById(R.id.clearDatabaseButton);
        createSampleDataButton = findViewById(R.id.createSampleDataButton);
        goToTermsActivity = findViewById(R.id.termsButton);
        showMeSomethingButton = findViewById(R.id.showDataButton);

        //Listeners for Buttons
        createClearDatabaseOnClickListener(clearDatabase);
        createCreateSampleDataButtonListener(createSampleDataButton);
        createGoToTermsActivityListener(goToTermsActivity);
        createShowMeSomethingListener(showMeSomethingButton);


    }

    private void createShowMeSomethingListener(Button showMeSomethingButton) {

        showMeSomethingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String term1Title = db.termDao().getTerm(4).getMTitle();
                Toast.makeText(getApplicationContext(),"Term1 title is: " + term1Title, Toast.LENGTH_LONG).show();


            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void createCreateSampleDataButtonListener(Button createSampleDataButton) {

        createSampleDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleData.populateDatabaseWithSampleData(getApplicationContext());
            }
        });
    }

    private void createGoToTermsActivityListener(Button goToTermsActivity) {

        goToTermsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You Clicked Terms", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void createClearDatabaseOnClickListener(Button clearDatabase) {

        clearDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleData.deleteAllDataFromDatabase(getApplicationContext());
            }
        });
    }



    private void updateList() {
    }


}
