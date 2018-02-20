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

import com.buggyarts.instafeedplus.Models.Link;
import com.buggyarts.instafeedplus.Models.LinksnTagsList;
import com.buggyarts.instafeedplus.Models.Story;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mayank on 1/20/18
 */

public class TrendingFeeds extends Fragment {

    Context context;
    ArrayList<Object> object_array;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    ObjectRecyclerViewAdapter adapter;

    TextView heading_text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.trending_feeds, container, false);

        heading_text = view.findViewById(R.id.trending_feeds_heading);

        recyclerView = view.findViewById(R.id.trending_feeds_recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ObjectRecyclerViewAdapter(object_array, context);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        object_array = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("trending").child("body");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("Trending", "onDataChange: " + dataSnapshot.toString());
                String jsonResponse = dataSnapshot.toString().replace("DataSnapshot", "");
                try {
                    extractTrendingFeeds(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void extractTrendingFeeds(String json) throws JSONException {

        JSONObject jsonResponseObject = new JSONObject(json).getJSONObject("value");

        String source = jsonResponseObject.getJSONObject("source").getString("name");
        heading_text.setText(source);

        JSONArray jsonArray = jsonResponseObject.getJSONArray("articles");
        int i = 0;
        while (i < jsonArray.length()) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            object_array.add(new Story(jsonObject.getString("title"),
                    jsonObject.getString("imgUrl").replace("https://", ""),
                    jsonObject.getString("fullStoryUrl"),
                    jsonObject.getString("category"))
            );
            i++;
        }

        JSONArray links_array = jsonResponseObject.getJSONArray("navBarLinks");
        ArrayList<Link> links = new ArrayList<>();
        int j = 0;
        while (j < links_array.length()) {
            JSONObject linkOb = links_array.getJSONObject(j);
            links.add(new Link(linkOb.getString("name"), linkOb.getString("link")));
            j++;
        }
        object_array.add(0, new LinksnTagsList(links));
    }
}
