package com.buggyarts.instafeedplus;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.buggyarts.instafeedplus.adapters.MainPagerAdapter;
import com.buggyarts.instafeedplus.customViews.SimpleToolBar;
import com.buggyarts.instafeedplus.fragments.CategoriesFragment;
import com.buggyarts.instafeedplus.fragments.StoriesFragment;
import com.buggyarts.instafeedplus.fragments.TopFeeds;
import com.buggyarts.instafeedplus.fragments.TrendingFeeds;

public class HomeTabActivity extends AppCompatActivity implements SimpleToolBar.TopBarCallback {

    TopFeeds fragmentTopFeeds;
    CategoriesFragment fragmentCategories;
    TrendingFeeds fragmentTrendingFeeds;
    StoriesFragment fragmentStories;

    ViewPager viewPager;
    MainPagerAdapter adapter;
    FragmentManager fragmentManager;
    SimpleToolBar toolBar;

    AHBottomNavigation bottomNavigationView;

    private static int PREFERENCE_REQUEST = 12;
    boolean onBoarding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        checkUserPreferences();

        setupToolbar();

        fragmentManager = getSupportFragmentManager();
        if (fragmentTopFeeds == null) {
            fragmentTopFeeds = TopFeeds.newInstance();
        }
        loadFragment(fragmentTopFeeds);

        setupBottomNav();
    }

    public void setupToolbar(){
        toolBar = findViewById(R.id.toolBar);
        toolBar.getBackButton().setVisibility(View.GONE);
        toolBar.getTitleLabel().setText(getResources().getText(R.string.app_name));
        toolBar.setTopbarListener(this);
    }

    public void checkUserPreferences() {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        if (!preferences.contains("asked_before")) {
            onBoarding = true;
            Intent intent = new Intent(HomeTabActivity.this, GatherInfoActivity.class);
            intent.putExtra("onBoarding",onBoarding);
            startActivity(intent);
        }
    }

    public void setupBottomNav() {
        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Feeds", R.drawable.ic_home_black_24dp, R.color.colorWhite);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getResources().getString(R.string.categories_title), R.drawable.ic_more_black_24dp, R.color.colorWhite);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getResources().getString(R.string.trending_title), R.drawable.ic_whatshot_black_24dp, R.color.colorWhite);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getResources().getString(R.string.magazine_title), R.drawable.ic_pages_black_24dp, R.color.colorWhite);

        // Add items
        bottomNavigationView.addItem(item1);
        bottomNavigationView.addItem(item2);
        bottomNavigationView.addItem(item3);
        bottomNavigationView.addItem(item4);

        bottomNavigationView.setBehaviorTranslationEnabled(false);

        bottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigationView.setCurrentItem(0);
        bottomNavigationView.setInactiveColor(R.color.colorPrimary);

        bottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasTabSelected) {
                switch (position) {
                    case 0:
                        toolBar.getTitleLabel().setText("InstaFeed+");
                        if (fragmentTopFeeds == null) {
                            fragmentTopFeeds = TopFeeds.newInstance();
                        }
                        loadFragment(fragmentTopFeeds);
                        return true;
                    case 1:
                        toolBar.getTitleLabel().setText(getResources().getString(R.string.categories_title));
                        if (fragmentCategories == null) {
                            fragmentCategories = CategoriesFragment.newInstance();
                        }
                        loadFragment(fragmentCategories);
                        return true;
                    case 2:
                        toolBar.getTitleLabel().setText(getResources().getString(R.string.trending_title));
                        if (fragmentTrendingFeeds == null) {
                            fragmentTrendingFeeds = TrendingFeeds.newInstance();
                        }
                        loadFragment(fragmentTrendingFeeds);
                        return true;
                    case 3:
                        toolBar.getTitleLabel().setText(getResources().getString(R.string.magazine_title));
                        if (fragmentStories == null) {
                            fragmentStories = StoriesFragment.newInstance();
                        }
                        loadFragment(fragmentStories);
//                        Intent intent = new Intent(MainActivity.this,HotStoriesActivity.class);
//                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commitAllowingStateLoss();
//        transaction.commit();
    }

    @Override
    public void backButtonCalled() {

    }

}
