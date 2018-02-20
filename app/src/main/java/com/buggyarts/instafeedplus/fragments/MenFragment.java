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

import com.buggyarts.instafeedplus.Models.StoriesModelOne;
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
import java.util.Collections;

/**
 * Created by mayank on 2/10/18
 */

public class MenFragment extends Fragment {

    Context context;

    ArrayList<Object> stories;
    ArrayList<Story> cosmo_maxim, xp;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ObjectRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View stories_view = inflater.inflate(R.layout.sample_dev_fragment, container, false);

        recyclerView = stories_view.findViewById(R.id.scores_card_recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ObjectRecyclerViewAdapter(stories, context);
        recyclerView.setAdapter(adapter);

        return stories_view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stories = new ArrayList<>();
        cosmo_maxim = new ArrayList<>();
        xp = new ArrayList<>();

        context = getContext();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Maxim").child("man");

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

    public void extractStories(String jsonResponse) {
        try {
            JSONArray items;
            try {
                items = new JSONObject(jsonResponse).getJSONArray("value");
            } catch (JSONException e2) {
                items = new JSONObject(jsonResponse).getJSONObject("value").getJSONArray("man");
            }

            int i = 0;
            while (i < items.length()) {
                JSONObject story = items.getJSONObject(i);
                String identifier = story.getString("identifier");
                String img_url;
                if (identifier.equals("maxim")) {
                    img_url = story.getString("imgUrl").replace("https://", "");

                    stories.add(new Story(story.getString("title"),
                            img_url,
                            story.getString("fullStoryUrl"),
                            story.getString("category")));

                } else {

                    img_url = story.getString("imgUrl");
                    xp.add(new Story(story.getString("title"),
                            img_url,
                            story.getString("fullStoryUrl"),
                            story.getString("category")));
                }
                i++;
            }

            int j = 0;
            int even_odd = xp.size() % 2;

            if (even_odd == 0) {
                while (j < xp.size()) {

                    StoriesModelOne model = new StoriesModelOne(xp.get(j), xp.get(j + 1));
                    stories.add(model);
                    j = j + 2;
                }
            } else {
                while (j < xp.size() - 1) {

                    StoriesModelOne model = new StoriesModelOne(xp.get(j), xp.get(j + 1));
                    stories.add(model);
                    j = j + 2;
                }
            }

            Collections.shuffle(stories);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
