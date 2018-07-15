package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buggyarts.instafeedplus.Models.StoryModelSI;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
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

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by mayank on 1/27/18
 */

public class RelationshipFragment extends Fragment {

    Context context;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    ObjectRecyclerViewAdapter adapter;

    ArrayList<Object> list;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView heading_title, heading_sub_title;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.stories_fragment_trending, container, false);


        recyclerView = fragmentView.findViewById(R.id.stories_recycler_view);
        manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new ObjectRecyclerViewAdapter(list, context);
        recyclerView.setAdapter(adapter);

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
        databaseReference = firebaseDatabase.getReference().child("MensXp").child("Relationship");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String jsonResponse = dataSnapshot.toString().replace("DataSnapshot", "");
//                Log.d( "JSON",jsonResponse);
                extractStories(jsonResponse);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Constants.RELATIONSHIP_TITLE != null) {
            heading_title.setText(Constants.RELATIONSHIP_TITLE);
        }
        if (Constants.RELATIONSHIP_SUB_TITLE != null) {
            heading_sub_title.setText(Constants.RELATIONSHIP_SUB_TITLE);
        }
    }

    public void extractStories(String jsonResponse) {
        try {
            JSONObject value = new JSONObject(jsonResponse).getJSONObject("value");
            JSONArray items = value.getJSONArray("relationship").getJSONArray(0);
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

            Constants.RELATIONSHIP_TITLE = headline.getString("title");
            Constants.RELATIONSHIP_SUB_TITLE = headline.getString("subTitle");
            heading_title.setText(Constants.RELATIONSHIP_TITLE);
            heading_sub_title.setText(Constants.RELATIONSHIP_SUB_TITLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
