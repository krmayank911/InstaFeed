package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buggyarts.instafeedplus.Models.Story;
import com.buggyarts.instafeedplus.Models.StoryModelSI;
import com.buggyarts.instafeedplus.Models.story.StoryListTypeOne;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.adapters.WrapperAdapter;
import com.buggyarts.instafeedplus.utils.Constants;
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

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by mayank on 1/27/18
 */

public class Trending extends Fragment {

    Context context;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    ObjectRecyclerViewAdapter adapter;
    WrapperAdapter wrapperAdapter;
    TextView heading_title, heading_sub_title;
    String TAG = "Trending";

    ArrayList<Object> list;
    ArrayList<Object> arrayOfList = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.stories_fragment_trending, container, false);

        recyclerView = fragmentView.findViewById(R.id.stories_recycler_view);
        manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        wrapperAdapter = new WrapperAdapter(context,arrayOfList);
        recyclerView.swapAdapter(wrapperAdapter,true);

//        adapter = new ObjectRecyclerViewAdapter(list, context);
//        recyclerView.swapAdapter(wrapperAdapter,true);

        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        heading_title = fragmentView.findViewById(R.id.trending_title);
        heading_sub_title = fragmentView.findViewById(R.id.trending_sub_title);

        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        list = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("MensXp").child("Trending");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String jsonResponse = dataSnapshot.toString().replace("DataSnapshot", "");
//                Log.d("JSON", jsonResponse);
//                extractStories(jsonResponse);
//                adapter.notifyDataSetChanged();
                getCategories(jsonResponse);
                wrapperAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

//        Log.d(TAG,"OnResumed Called");
        if (Constants.TRENDING_TITLE != null) {
            heading_title.setText(Constants.TRENDING_TITLE);
        }
        if (Constants.TRENDING_SUB_TITLE != null) {
            heading_sub_title.setText(Constants.TRENDING_SUB_TITLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void getCategories(String jsonResponse){
        try {
            JSONObject value = new JSONObject(jsonResponse).getJSONObject("value");
            JSONArray items = value.getJSONArray("trending");
            int i = 0;

            while (i < items.length()) {
                JSONObject story = items.getJSONObject(i);
                String category = story.getString("category");

                if(i > 0) {
                    if(!isAddedEarlier(category)){
                        categories.add(category);
                    }
                }else {
                    categories.add(category);
                }
                i++;
            }

            for(String category : categories){
                extractStoriesForCategory(category,items);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void extractStoriesForCategory(String category,JSONArray items){
        try{
            list = new ArrayList<>();
            int i = 0;
            while (i < items.length()) {
                JSONObject story = items.getJSONObject(i);

                if(category.equals(story.getString("category"))){
                    list.add(new StoryModelSI(story.getString("title"),
                            story.getString("imgUrl"),
                            story.getString("fullStoryUrl"),
                            story.getString("category")));
                }
                i++;
            }

            StoryListTypeOne storyListTypeOne = new StoryListTypeOne();
            storyListTypeOne.setStorylist(list);
            storyListTypeOne.setStoryListTitle(category);
            arrayOfList.add(storyListTypeOne);

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void extractStories(String jsonResponse) {
        try {
            JSONObject value = new JSONObject(jsonResponse).getJSONObject("value");
            JSONArray items = value.getJSONArray("trending");
            int i = 0;
            while (i < items.length()) {
                JSONObject story = items.getJSONObject(i);
                list.add(new StoryModelSI(story.getString("title"),
                        story.getString("imgUrl"),
                        story.getString("fullStoryUrl"),
                        story.getString("category")));
                i++;
            }

            JSONObject headline = value.getJSONObject("heading");
            Constants.TRENDING_TITLE = headline.getString("title");
            Constants.TRENDING_SUB_TITLE = headline.getString("subTitle");
            heading_title.setText(Constants.TRENDING_TITLE);
            heading_sub_title.setText(Constants.TRENDING_SUB_TITLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isAddedEarlier(String category){

        for(String cat : categories){
            if(cat.equals(category)){
                return true;
            }
        }
        return false;
    }
}
