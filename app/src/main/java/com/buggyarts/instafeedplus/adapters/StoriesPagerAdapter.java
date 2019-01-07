package com.buggyarts.instafeedplus.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.buggyarts.instafeedplus.fragments.MenFragment;
import com.buggyarts.instafeedplus.fragments.RelationshipFragment;
import com.buggyarts.instafeedplus.fragments.Trending;
import com.buggyarts.instafeedplus.fragments.WomenFragment;

/**
 * Created by mayank on 1/27/18
 */

public class StoriesPagerAdapter extends FragmentPagerAdapter {

    int pageCount = 4;

    public StoriesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new Trending();
                return fragment;
            case 1:
                fragment = new WomenFragment();
                return fragment;
            case 2:
                fragment = new MenFragment();
                return fragment;
            case 3:
                fragment = new RelationshipFragment();
                return fragment;
            default:
                return null;
        }

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
                title = "Men";
                break;
            case 3:
                title = "Relationship";
                break;
        }
        return title;
    }

}
