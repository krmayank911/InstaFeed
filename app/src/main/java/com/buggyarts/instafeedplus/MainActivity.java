package com.buggyarts.instafeedplus;

import android.app.SearchManager;
import android.content.ComponentName;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.buggyarts.instafeedplus.adapters.MainPagerAdapter;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    MainPagerAdapter adapter;
    FragmentManager fragmentManager;

    CollapsingToolbarLayout toolbarLayout;
    Toolbar toolbar;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorHeight(0);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolBar);

        toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        toolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));

        fragmentManager = getSupportFragmentManager();

        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        tabLayout.setupWithViewPager(viewPager);

        adapter = new MainPagerAdapter(fragmentManager);
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        switch (item.getItemId()) {
            case R.id.action_search:
                //SearchView
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
