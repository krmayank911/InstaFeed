package com.buggyarts.instafeedplus;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.adapters.MainPagerAdapter;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.fragments.Bookmarks;
import com.buggyarts.instafeedplus.fragments.CategoriesFragment;
import com.buggyarts.instafeedplus.fragments.StoriesFragment;
import com.buggyarts.instafeedplus.fragments.TopFeeds;
import com.buggyarts.instafeedplus.fragments.TrendingFeeds;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

import static com.buggyarts.instafeedplus.utils.Constants.CATEGORIES;
import static com.buggyarts.instafeedplus.utils.Constants.CATEG_S;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    MainPagerAdapter adapter;
    FragmentManager fragmentManager;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle abdt;

    CollapsingToolbarLayout toolbarLayout;
    Toolbar toolbar;
    TabLayout tabLayout;

    AHBottomNavigation bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.main);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        checkUserPreferences();

        setUpToolbar();

        fragmentManager = getSupportFragmentManager();
        loadFragment(new TopFeeds());

        setupBottomNav();
//        setUpViewPager();
//        navigationHandler();

    }

    public void checkUserPreferences() {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        if (!preferences.contains("asked_before")) {
            Intent intent = new Intent(MainActivity.this, GatherInfoActivity.class);
            startActivity(intent);
        }
    }

    public void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
//        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);


//        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.setSelectedTabIndicatorHeight(0);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolBar);
        toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        toolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));
    }

    public void setUpViewPager() {
        fragmentManager = getSupportFragmentManager();
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        tabLayout.setupWithViewPager(viewPager);

        adapter = new MainPagerAdapter(fragmentManager);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);


        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        searchView.setBackground(getResources().getDrawable(R.drawable.search_bg));
        searchView.setSubmitButtonEnabled(true);

        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Log.d("QUERY",query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

//        if (abdt.onOptionsItemSelected(item)) {
//            return true;
//        }

        switch (item.getItemId()) {
            case R.id.action_search:
                //SearchView
                return true;
            case R.id.user_preferences:
                Intent intent = new Intent(MainActivity.this, GatherInfoActivity.class);
                startActivity(intent);
                return true;
            case R.id.open_bookmarks:
                loadFragment(new Bookmarks());
                toolbarLayout.setTitle("Bookmarks");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void navigationHandler() {
        drawerLayout = findViewById(R.id.drawer_layout);

        TextView magazine = findViewById(R.id.magazine);
        magazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HotStoriesActivity.class);
                startActivity(intent);
            }
        });

        CATEG_S = new ArrayList<>();

        ArrayList<Object> categoryArrayList = new ArrayList<>();

        int i = 0;
        while (i < CATEGORIES.length) {
            categoryArrayList.add(new Category(CATEGORIES[i]));
            i++;
        }

        RecyclerView catg_recyclerView = findViewById(R.id.nav_categories_list);
        RecyclerView.LayoutManager catg_layoutManager = new LinearLayoutManager(this);
        catg_recyclerView.setLayoutManager(catg_layoutManager);

        ObjectRecyclerViewAdapter catg_adapter = new ObjectRecyclerViewAdapter(categoryArrayList, this);

//        final OptionsRecyclerViewAdapter catg_adapter = new OptionsRecyclerViewAdapter(CATEG_S, new OptionsRecyclerViewAdapter.OnCategoryClick() {
//            @Override
//            public void onCategoryClickListener(int index, String category) {
//                Intent intent = new Intent(MainActivity.this, FeedsActivity.class);
//                intent.putExtra("category", category);
//                intent.putExtra("index", index);
//                startActivity(intent);
//            }
//        });

        catg_recyclerView.setAdapter(catg_adapter);

        abdt = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                catg_adapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        abdt.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        abdt.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(abdt);
        abdt.syncState();

    }

    public void setupBottomNav() {
        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Feeds", R.drawable.ic_home_black_24dp, R.color.colorWhite);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Categories", R.drawable.ic_more_black_24dp, R.color.colorWhite);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Trending", R.drawable.ic_whatshot_black_24dp, R.color.colorWhite);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Stories", R.drawable.ic_pages_black_24dp, R.color.colorWhite);

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
                        toolbarLayout.setTitle("InstaFeed+");
                        loadFragment(new TopFeeds());
                        return true;
                    case 1:
                        toolbarLayout.setTitle("Categories");
                        loadFragment(new CategoriesFragment());
                        return true;
                    case 2:
                        toolbarLayout.setTitle("InFocus");
                        loadFragment(new TrendingFeeds());
                        return true;
                    case 3:
                        toolbarLayout.setTitle("Magazine");
                        loadFragment(new StoriesFragment());
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
        transaction.commit();
    }


}
