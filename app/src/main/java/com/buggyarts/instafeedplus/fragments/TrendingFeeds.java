package com.buggyarts.instafeedplus.fragments;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.Models.Link;
import com.buggyarts.instafeedplus.Models.LinksnTagsList;
import com.buggyarts.instafeedplus.Models.Story;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.customViews.EmptyStateView;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
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

public class TrendingFeeds extends Fragment implements EmptyStateView.Callback {

    View trendingFeeds;

    Context context;
    ArrayList<Object> object_array;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    ObjectRecyclerViewAdapter adapter;

    RelativeLayout headingLayout;
    TextView heading_text;
    EmptyStateView noResultView;

    SwipeRefreshLayout swipeRefreshLayout;

    public static TrendingFeeds newInstance(){
        TrendingFeeds fragment = new TrendingFeeds();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(trendingFeeds == null) {

            trendingFeeds = inflater.inflate(R.layout.trending_feeds, container, false);

            headingLayout = trendingFeeds.findViewById(R.id.heading);
            heading_text = trendingFeeds.findViewById(R.id.trending_feeds_heading);

            noResultView = trendingFeeds.findViewById(R.id.noResultView);
            noResultView.showActionButton();
            noResultView.setVisibility(View.GONE);
            noResultView.setCallback(this);

            recyclerView = trendingFeeds.findViewById(R.id.trending_feeds_recyclerView);
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new ObjectRecyclerViewAdapter(object_array, context);
            recyclerView.setAdapter(adapter);

            swipeRefreshLayout = trendingFeeds.findViewById(R.id.swipe_refresh_layout);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(-1)) {
                        // we have reached the top of the list
                        headingLayout.setElevation(0f);
                    } else {
                        // we are not at the top yet
                        headingLayout.setElevation(50f);
                    }

                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    object_array.clear();
                    adapter.object_array = object_array;
                    getData();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

//            OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }

        return trendingFeeds;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        object_array = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        getData();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkConnection.isNetworkAvailale(context)) {
//            Toast.makeText(context, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
            shouldShowNoResultView();
        }
    }

    public void getData(){
        databaseReference = firebaseDatabase.getReference().child("trending").child("body");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    jsonObject.getString("identifier"))
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

    void shouldShowNoResultView(){
        if(object_array.size() < 2){
            noResultView.setVisibility(View.VISIBLE);
            noResultView.getEmptyStateImage().setImageDrawable(context.getResources().getDrawable(R.drawable.no_internet));
            noResultView.getEmptyStateTitle().setText(context.getResources().getString(R.string.no_internet_error));
            noResultView.getEmptyStateText().setText(context.getResources().getString(R.string.no_internet_message));
            noResultView.getButtonAction().setText(context.getResources().getString(R.string.reload));
        }
    }

    void hideNoResultView(){
        noResultView.setVisibility(View.GONE);
    }

    void resetAndReload(){
        object_array.clear();
        adapter.object_array = object_array;
        adapter.notifyDataSetChanged();
        hideNoResultView();

        if(NetworkConnection.isNetworkAvailale(context)) {
//            getData();
        }else {
            shouldShowNoResultView();
        }
    }

    @Override
    public void onEmptyStateActionClick() {
        resetAndReload();
    }

}
