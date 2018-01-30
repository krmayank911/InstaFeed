package com.buggyarts.instafeedplus.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.buggyarts.instafeedplus.fragments.SampleFragment;
import com.buggyarts.instafeedplus.fragments.SportsFeed;
import com.buggyarts.instafeedplus.fragments.TopFeeds;
import com.buggyarts.instafeedplus.fragments.TwitterNews;

/**
 * Created by mayank on 1/6/18
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    int pages = 2;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new TopFeeds();
                break;
            case 1:
                fragment = new TwitterNews();
                break;
            case 3:
                fragment = new SampleFragment();
                break;
            case 2:
                fragment = new SportsFeed();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return pages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "Recent Feeds";
                break;
            case 1:
                title = "HT News";
                break;
            case 3:
                title = "Categories";
                break;
            case 2:
                title = "SportsFeed";
                break;
        }
        return title;
    }
}
