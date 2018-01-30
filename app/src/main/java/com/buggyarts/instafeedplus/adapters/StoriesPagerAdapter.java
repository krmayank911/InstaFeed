package com.buggyarts.instafeedplus.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.buggyarts.instafeedplus.fragments.RelationshipFragment;
import com.buggyarts.instafeedplus.fragments.Trending;
import com.buggyarts.instafeedplus.fragments.WomenFragment;

/**
 * Created by mayank on 1/27/18
 */

public class StoriesPagerAdapter extends FragmentPagerAdapter {

    int pageCount = 3;

    public StoriesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Trending();
                break;
            case 1:
                fragment = new WomenFragment();
                break;
            case 2:
                fragment = new RelationshipFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Trending";
                break;
            case 1:
                title = "Women";
                break;
            case 2:
                title = "Relationship";
        }
        return title;
    }

}
