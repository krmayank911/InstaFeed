package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buggyarts.instafeedplus.Models.StoriesModelOne;
import com.buggyarts.instafeedplus.Models.Story;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.adapters.StoriesPagerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mayank on 2/4/18
 */

public class StoriesFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentManager fragmentManager;
    StoriesPagerAdapter storiesPagerAdapter;

    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stories, container, false);

        setUpViewPager(view);
        setUpTabLayout(view);

        return view;
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

    private void setUpTabLayout(View v) {

        tabLayout = v.findViewById(R.id.hot_stories_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(0);
    }
}
