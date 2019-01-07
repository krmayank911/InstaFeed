package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.StoriesPagerAdapter;
import com.buggyarts.instafeedplus.customViews.EmptyStateView;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;

/**
 * Created by mayank on 2/4/18
 */

public class StoriesFragment extends Fragment implements EmptyStateView.Callback {

    View storiesFragment;

    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentManager fragmentManager;
    StoriesPagerAdapter storiesPagerAdapter;
    EmptyStateView noResultView;

    Context context;

    public static StoriesFragment newInstance(){
        StoriesFragment fragment = new StoriesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(storiesFragment == null) {
            storiesFragment = inflater.inflate(R.layout.fragment_stories, container, false);

            noResultView = storiesFragment.findViewById(R.id.noResultView);
            noResultView.showActionButton();
            noResultView.setVisibility(View.GONE);
            noResultView.setCallback(this);
        }

        return storiesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    private void setUpViewPager(View v) {
        viewPager = v.findViewById(R.id.hot_stories_view_pager);

        fragmentManager = getChildFragmentManager();
        viewPager = v.findViewById(R.id.hot_stories_view_pager);
        storiesPagerAdapter = new StoriesPagerAdapter(fragmentManager);
        viewPager.setAdapter(storiesPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkConnection.isNetworkAvailale(context)) {
            shouldShowNoResultView();
        }

        setUpViewPager(storiesFragment);
        setUpTabLayout(storiesFragment);
    }

    private void setUpTabLayout(View v) {

        tabLayout = v.findViewById(R.id.hot_stories_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(0);
    }


    void shouldShowNoResultView(){
        noResultView.setVisibility(View.VISIBLE);
        noResultView.getEmptyStateImage().setImageDrawable(context.getResources().getDrawable(R.drawable.no_internet));
        noResultView.getEmptyStateTitle().setText(context.getResources().getString(R.string.no_internet_error));
        noResultView.getEmptyStateText().setText(context.getResources().getString(R.string.no_internet_message));
        noResultView.getButtonAction().setText(context.getResources().getString(R.string.reload));
    }

    void hideNoResultView(){
        noResultView.setVisibility(View.GONE);
    }

    @Override
    public void onEmptyStateActionClick() {
        firebaseCheck();
    }

    void firebaseCheck(){

        if(NetworkConnection.isNetworkAvailale(getContext())) {
            hideNoResultView();
        }else {
            shouldShowNoResultView();
        }
    }

}
