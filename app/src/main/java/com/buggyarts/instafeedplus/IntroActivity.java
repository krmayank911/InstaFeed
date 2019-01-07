package com.buggyarts.instafeedplus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.instafeedplus.adapters.pagerAdapters.IntroPagerAdapter;
import com.buggyarts.instafeedplus.customClasses.CustomViewPager;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.buggyarts.instafeedplus.utils.Constants;
import com.rd.PageIndicatorView;

public class IntroActivity extends AppCompatActivity implements IntroPagerAdapter.Callback, View.OnClickListener, ViewPager.OnPageChangeListener {

    TextView skip;
    ImageView next;
    String selected_country = null, selected_language = null;

    CustomViewPager introPager;
    IntroPagerAdapter introPagerAdapter;
    PageIndicatorView pageIndicatorView;
    int pagePosition = 0;

    boolean onBoarding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gather_info);

        skip = findViewById(R.id.button_skip);
        next = findViewById(R.id.button_next);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);

        introPager = findViewById(R.id.introViewPager);
        introPager.setPagingEnabled(false);
        introPagerAdapter = new IntroPagerAdapter(getSupportFragmentManager());
        introPagerAdapter.setCallback(this);
        introPager.setAdapter(introPagerAdapter);

        introPager.addOnPageChangeListener(this);

        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(introPager);
        pageIndicatorView.setCount(2); // specify total count of indicators

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra("onBoarding")){
                onBoarding = intent.getBooleanExtra("onBoarding",false);
            }
        }

//        actionSkip();
//        actionSubmit();
    }

//    public void countrySelectorSetup() {
//
//        country_spinner = findViewById(R.id.country_selector);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, Constants.countries);
//        country_spinner.setAdapter(arrayAdapter);
//        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selected_country = Constants.country_options_abr[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                selected_country = "null";
//            }
//        });
//    }
//
//    public void languageSelectorSetup() {
//
//        language_spinner = findViewById(R.id.language_selector);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, Constants.languages);
//        language_spinner.setAdapter(arrayAdapter);
//        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selected_language = Constants.languages_options_abr[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                selected_language = "null";
//            }
//        });
//    }

//    public void actionSkip() {
//
//        skip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean("asked_before", true);
//                editor.apply();
//                finish();
//            }
//        });
//    }

//    public void actionSubmit() {
//
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(!selected_country.equals("null")) {
//                    //save data in shared preferences
//                    SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString("country", selected_country);
//                    editor.putString("language", selected_language);
//                    editor.putBoolean("asked_before", true);
//                    editor.apply();
//
//                    if (onBoarding) {
//                        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        IntroActivity.this.finish();
//                    } else {
//                        exit();
//                    }
//                }else {
//                    Toast.makeText(IntroActivity.this,R.string.error_select_country,Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        exit();
        return super.onKeyDown(keyCode, event);
    }

    public void exit(){
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        IntroActivity.this.finish();
    }

    @Override
    public void onCountrySelected(String country) {
        selected_country = country;
    }

    @Override
    public void onLanguageSelected(String language) {
        selected_language = language;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_skip){

                SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("asked_before", true);
                editor.apply();

            if (onBoarding) {
                Intent intent = new Intent(IntroActivity.this, HomeTabActivity.class);
                startActivity(intent);
                IntroActivity.this.finish();
            } else {
                exit();
            }

        }else if(view.getId() == R.id.button_next){

            if(pagePosition == 0){
                if(selected_country != null) {
                    introPager.setCurrentItem(2, true);
                }else {
                    Toast.makeText(this,"Please Select a country", Toast.LENGTH_SHORT).show();
                }
            }else if(pagePosition == 1 ){

                if(selected_language != null) {
                    //save data in shared preferences
                    SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("country", selected_country);
                    editor.putString("language", selected_language);
                    editor.putBoolean("asked_before", true);
                    editor.apply();

                    if (onBoarding) {
                        Intent intent = new Intent(IntroActivity.this, HomeTabActivity.class);
                        startActivity(intent);
                        IntroActivity.this.finish();
                    } else {
                        exit();
                    }

                }else {
                    Toast.makeText(this,"Please Select a language", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pageIndicatorView.setSelected(position);
        onPositionChange(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    void onPositionChange(int position){
        pagePosition = position;

        if(pagePosition == 1){
            next.setImageDrawable(getResources().getDrawable(R.drawable.finnish));
            next.setColorFilter(ContextCompat.getColor(this, R.color.themeColorSecondaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
        }else {
            next.setImageDrawable(getResources().getDrawable(R.drawable.next));
            next.setColorFilter(ContextCompat.getColor(this, R.color.themeColorAccentDark), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

}