package com.buggyarts.instafeedplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.instafeedplus.utils.Constants;

public class GatherInfoActivity extends AppCompatActivity {

    Toolbar toolbar;
    Spinner country_spinner, language_spinner;

    TextView skip, submit;
    String selected_country = null, selected_language = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gather_info);

        setToolbar();
        countrySelectorSetup();
        languageSelectorSetup();

        actionSkip();
        actionSubmit();
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setTitle("Preferences");
        setSupportActionBar(toolbar);
    }

    public void countrySelectorSetup() {

        country_spinner = findViewById(R.id.country_selector);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, Constants.countries);
        country_spinner.setAdapter(arrayAdapter);
        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_country = Constants.country_options_abr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_country = "null";
            }
        });
    }

    public void languageSelectorSetup() {

        language_spinner = findViewById(R.id.language_selector);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, Constants.languages);
        language_spinner.setAdapter(arrayAdapter);
        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_language = Constants.languages_options_abr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_language = "null";
            }
        });
    }

    public void actionSkip() {
        skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("asked_before", true);
                editor.apply();
                finish();
            }
        });
    }

    public void actionSubmit() {
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save data in shared preferences
                SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("country", selected_country);
                editor.putString("language", selected_language);
                editor.putBoolean("asked_before", true);
                editor.apply();
//                finish();

                Intent intent = new Intent(GatherInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}