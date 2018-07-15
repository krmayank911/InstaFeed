package com.buggyarts.instafeedplus;

import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.buggyarts.instafeedplus.adapters.StoriesPagerAdapter;

public class HotStoriesActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentManager manager;
    StoriesPagerAdapter storiesPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_stories);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueStatusBar));
        }

        setUpViewPager();
        setUpTabLayout();

    }

    private void setUpViewPager() {
        viewPager = findViewById(R.id.hot_stories_view_pager);

        manager = getSupportFragmentManager();
        viewPager = findViewById(R.id.hot_stories_view_pager);
        storiesPagerAdapter = new StoriesPagerAdapter(manager);
        viewPager.setAdapter(storiesPagerAdapter);
    }

    private void setUpTabLayout() {

        toolbar = findViewById(R.id.top_bar);
        toolbar.setTitle("Hot Stories");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        tabLayout = findViewById(R.id.hot_stories_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.setElevation(2);
        }
    }

}
