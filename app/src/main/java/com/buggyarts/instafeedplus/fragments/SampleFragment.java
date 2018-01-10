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
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.OptionsRecyclerViewAdapter;

import java.util.ArrayList;

import static com.buggyarts.instafeedplus.utils.Constants.CATEGORIES;

/**
 * Created by mayank on 1/6/18
 */

public class SampleFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> categories;
    RecyclerView.LayoutManager manager;
    OptionsRecyclerViewAdapter adapter;

    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sample_fragment, container, false);

        recyclerView = v.findViewById(R.id.categories_recycler_view);
        manager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(manager);
        adapter = new OptionsRecyclerViewAdapter(categories, new OptionsRecyclerViewAdapter.OnCategoryClick() {
            @Override
            public void onCategoryClickListener(int index, String category) {

                Intent intent = new Intent(context, FeedsActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        int i = 0;
        categories = new ArrayList<>();
        while (i < CATEGORIES.length) {
            categories.add(CATEGORIES[i]);
            i++;
        }
    }
}
