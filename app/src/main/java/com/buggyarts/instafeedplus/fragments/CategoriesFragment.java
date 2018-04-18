package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buggyarts.instafeedplus.FeedsActivity;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.adapters.OptionsRecyclerViewAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import static com.buggyarts.instafeedplus.utils.Constants.CATEGORIES;
import static com.buggyarts.instafeedplus.utils.Constants.CATEG_S;

/**
 * Created by mayank on 1/6/18
 */

public class CategoriesFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    ObjectRecyclerViewAdapter adapter;

    Context context;
    ArrayList<Object> categoryArrayList;

    AdView adView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sample_fragment, container, false);



        recyclerView = v.findViewById(R.id.categories_recycler_view);
        manager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(manager);
        adapter = new ObjectRecyclerViewAdapter(categoryArrayList, context);
        recyclerView.setAdapter(adapter);

        adView = v.findViewById(R.id.banner_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(adListener);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();

        categoryArrayList = new ArrayList<>();

        int i = 0;
        while (i < CATEGORIES.length) {
            categoryArrayList.add(new Category(CATEGORIES[i]));
            i++;
        }

    }

    AdListener adListener = new AdListener(){
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            adView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            adView.setVisibility(View.GONE);
        }
    };
}
