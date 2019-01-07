package com.buggyarts.instafeedplus.adapters.pagerAdapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.buggyarts.instafeedplus.fragments.introFragments.SelectCountryFragment;
import com.buggyarts.instafeedplus.fragments.introFragments.SelectLanguageFragment;

public class IntroPagerAdapter extends FragmentPagerAdapter implements SelectCountryFragment.Callback,
        SelectLanguageFragment.Callback {

    int fragmentCount = 2;
    Callback callback;
    SelectCountryFragment selectCountryFragment;
    SelectLanguageFragment selectLanguageFragment;

    public interface Callback{
        void onCountrySelected(String country);
        void onLanguageSelected(String language);
    }

    public IntroPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                selectCountryFragment = new SelectCountryFragment();
                selectCountryFragment.setCallback(this);
                return selectCountryFragment;

            case 1:
                selectLanguageFragment = new SelectLanguageFragment();
                selectLanguageFragment.setCallback(this);
                return selectLanguageFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return fragmentCount;
    }

    @Override
    public void onCountrySelected(String country) {
        callback.onCountrySelected(country);
    }

    @Override
    public void onLanguageSelected(String language) {
        callback.onLanguageSelected(language);
    }
}
