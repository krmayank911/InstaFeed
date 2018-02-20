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

import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.utils.Article;
import com.buggyarts.instafeedplus.utils.data.DbUser;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sample_fragment, container, false);

        recyclerView = view.findViewById(R.id.categories_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ObjectRecyclerViewAdapter(list, context);
        recyclerView.setAdapter(adapter);

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
                list.add(new Article(time, source, title, description, thumbnail_url, url));
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
    }
}
