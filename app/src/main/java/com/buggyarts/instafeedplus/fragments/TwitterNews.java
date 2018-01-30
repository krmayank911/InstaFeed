package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.os.AsyncTask;
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
import com.buggyarts.instafeedplus.adapters.FeedsRecyclerViewAdapter;
import com.buggyarts.instafeedplus.utils.Article;
import com.buggyarts.instafeedplus.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import twitter4j.ExtendedMediaEntity;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Location;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.TweetEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by mayank on 1/16/18
 */

public class TwitterNews extends Fragment {

    Context context;
    TwitterFactory twitterFactory;
    Twitter twitter;

    ArrayList<Article> articles;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FeedsRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View twitterView = inflater.inflate(R.layout.twitter_fragment, container, false);
        recyclerView = twitterView.findViewById(R.id.twitter_recyclerview);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FeedsRecyclerViewAdapter(articles, context);
        recyclerView.setAdapter(adapter);
        return twitterView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        articles = new ArrayList<>();
        initTwitter();

//        BackgroundTasks tasks = new BackgroundTasks();
//        tasks.execute();
//        GetTends getTends = new GetTends();
//        getTends.execute();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("News").child("HtTopNews");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                articles.clear();
                String feeds = dataSnapshot.toString();
//                Log.d("TAG", "onDataChange: "+feeds);
                extractFeeds(feeds);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(Constants.CONSUMER_KEY)
                .setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
                .setOAuthAccessToken(Constants.TWITTER_AUTH_TOKEN)
                .setOAuthAccessTokenSecret(Constants.TWITTER_AUTH_SECRET);

        twitterFactory = new TwitterFactory(cb.build());
        twitter = twitterFactory.getInstance();
    }

    public class BackgroundTasks extends AsyncTask<Object, Void, Void> {


        @Override
        protected Void doInBackground(Object... objects) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            try {
                List<twitter4j.Status> statuses = twitter.getHomeTimeline();
                for (twitter4j.Status st : statuses) {

                    String source = st.getUser().getName();
                    String title = st.getText();
                    String time = format.format(st.getCreatedAt().getTime());
                    String thumbnail_url = "", url = "";

                    ExtendedMediaEntity[] entities = st.getExtendedMediaEntities();
                    int i = 0;
                    while (i < entities.length) {
                        ExtendedMediaEntity mediaEntity = entities[i];
                        thumbnail_url = mediaEntity.getMediaURLHttps();
                        i++;
                    }
                    int j = 0;
                    URLEntity[] urlEntities = st.getURLEntities();
                    while (j < urlEntities.length) {
                        URLEntity urlEntity = urlEntities[j];
                        url = urlEntity.getURL();
                        j++;
                    }
                    articles.add(new Article(time, source, title, " ", thumbnail_url, url));
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }

    public class GetTends extends AsyncTask<Object, Void, Void> {

        ResponseList<Location> locationResponseList;
        String req_location = "india";
        int woeid = 0;

        @Override
        protected Void doInBackground(Object... objects) {

            try {
                locationResponseList = twitter.getAvailableTrends();
                for (Location location : locationResponseList) {
                    if (location.getName().toLowerCase().equals(req_location.toLowerCase())) {
                        woeid = location.getWoeid();
                        Log.d("WOEID", "" + woeid);
                    }
                }

                Trends trends = twitter.getPlaceTrends(woeid);
                Log.d("Location", "" + trends.getLocation());
                Log.d("Date", "" + trends.getAsOf());
                for (int i = 0; i < trends.getTrends().length; i++) {
                    Log.d("doInBackground: ", trends.getTrends()[i].getQuery());
                }
                List<twitter4j.Status> statuses = twitter.search(new Query(trends.getTrends()[0].getQuery())).getTweets();
                Log.d("doInBackground: ", statuses.toString());

            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void extractFeeds(String jsonResponse) {
        JSONObject jsonResponseObject;
        jsonResponse = jsonResponse.replace("DataSnapshot", "");
        try {
            jsonResponseObject = new JSONObject(jsonResponse);
//            Log.d("JSONob", "extractFeeds: "+ jsonResponseObject.toString());
            JSONObject channel = jsonResponseObject.getJSONObject("value").getJSONObject("rss").getJSONObject("channel");
            JSONArray items = channel.getJSONArray("item");
            String source = channel.getString("title");
            source = source.substring(0, source.indexOf("-"));

            int i = 0;
            while (i < items.length()) {
                JSONObject article = items.getJSONObject(i);
                String time = "";
                String title = article.getString("title");
                String description = article.getString("description");
                if (description == null) {
                    description = " ";
                }
                String url = article.getString("link");

                String published_date = article.getString("pubDate").replace("GMT", "");
//                Log.d("pubDate",published_date);
                SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat fromFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
                try {
                    java.util.Date pubDate = fromFormat.parse(published_date);
                    time = toFormat.format(pubDate.getTime());
//                    Log.d("Formatted Date",time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                JSONObject mediaContent = article.getJSONObject("media:content");
                String thumbnail_url = mediaContent.getString("@url");

                articles.add(new Article(time, source, title, description, thumbnail_url, url));

                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
