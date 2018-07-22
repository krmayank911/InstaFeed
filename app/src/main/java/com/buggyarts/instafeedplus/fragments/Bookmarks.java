package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.utils.Article;
import com.buggyarts.instafeedplus.utils.data.DbUser;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mayank on 2/16/18
 */

public class Bookmarks extends Fragment {

    Context context;
    ArrayList<Object> list;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ObjectRecyclerViewAdapter adapter;

    AdView adView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sample_fragment, container, false);

        recyclerView = view.findViewById(R.id.categories_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ObjectRecyclerViewAdapter(list, context);
        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
        snapHelper.attachToRecyclerView(recyclerView);

//        adView = view.findViewById(R.id.banner_adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);
//
//        adView.setAdListener(adListener);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        list = new ArrayList<>();

        DbUser user = new DbUser(context);
        ArrayList<String> stringArrayList = user.readArticlesFromDB();

        Log.d("Bookmark: ", "" + stringArrayList.size());

        int i = 0;
        while (i < stringArrayList.size()) {
            createFeeds(stringArrayList.get(i));
            i++;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkConnection.isNetworkAvailale(context)) {
            Toast.makeText(context, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void createFeeds(String jsonResponse) {

        Log.d("Bookmark: ", jsonResponse);

        if (jsonResponse != null) {
            try {
                JSONObject article_ob = new JSONObject(jsonResponse);
                String source = article_ob.getJSONObject("source").getString("name");
                String time = article_ob.getString("publishedAt");
                String title = article_ob.getString("title");
                String description = article_ob.getString("description");
                String thumbnail_url = article_ob.getString("urlToImage");
                String url = article_ob.getString("url");
                String timeFormat = article_ob.getString("timeFormat");
                if(timeFormat.equals("GoogleTimeFormat")){
                    Article article = new Article(time, source, title, description, thumbnail_url, url);
                    article.setBookmarked(true);
                    list.add(article);
                }else{
                    Boolean isTimeAvailable = article_ob.getBoolean("isTimeAvailable");
                    Article article = new Article(time, source, title, description, thumbnail_url, url,timeFormat,isTimeAvailable);
                    article.setBookmarked(true);
                    list.add(article);
                }


            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
    }

//    AdListener adListener = new AdListener(){
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                adView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                adView.setVisibility(View.GONE);
//            }
//    };
}
