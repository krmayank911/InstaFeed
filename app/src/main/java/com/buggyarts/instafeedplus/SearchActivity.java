package com.buggyarts.instafeedplus;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.buggyarts.instafeedplus.adapters.FeedsRecyclerViewAdapter;
import com.buggyarts.instafeedplus.utils.Article;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.buggyarts.instafeedplus.utils.Constants.API_KEY;
import static com.buggyarts.instafeedplus.utils.Constants.BASE_URL;
import static com.buggyarts.instafeedplus.utils.Constants.EVERYTHING;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    FeedsRecyclerViewAdapter adapter;
    ArrayList<Article> feeds;
    String query = "";

    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("QUERY", "onCreate: " + query);
        }

        feeds = new ArrayList<>();
        loadFeeds(query);

        setupAppBar();

        recyclerView = (RecyclerView) findViewById(R.id.feeds_recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new FeedsRecyclerViewAdapter(feeds, this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkConnection.isNetworkAvailale(this)) {
            Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    void setupAppBar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(query);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolBar);

        toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        toolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbarLayout.setExpandedTitleGravity(Gravity.CENTER);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedText);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.ExpendedText);
    }

    public void loadFeeds(String query) {
        String url = BASE_URL + EVERYTHING + "q=" + query + "&sortBy=popularity" + "&apiKey=" + API_KEY;
        Log.v("URL", url);
        new GetFeeds().execute(url);
    }

    public class GetFeeds extends AsyncTask<String, Void, String> {

        String json_res;

        @Override
        protected String doInBackground(String... strings) {
            json_res = getJson(strings[0]);
            return json_res;
        }

        @Override
        protected void onPostExecute(String res) {
            createFeeds(json_res);
            adapter.notifyDataSetChanged();
            super.onPostExecute(res);
        }
    }

    public String getJson(String src) {
        String jsonResponse = null;

        try {
            URL url = new URL(src);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (httpURLConnection.getResponseCode() != 200) {
//                throw new IOException(httpURLConnection.getResponseMessage());
                Log.v("Response", httpURLConnection.getResponseMessage());
            } else {
                Log.v("Connection Status", httpURLConnection.getResponseMessage());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();
                jsonResponse = stringBuilder.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    public void createFeeds(String jsonResponse) {
        if (jsonResponse != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                int article_count = jsonObject.getInt("totalResults");
                int i = 0;
                while (i < article_count) {
                    JSONArray articles = jsonObject.getJSONArray("articles");
                    JSONObject article_ob = articles.getJSONObject(i);
                    String source = article_ob.getJSONObject("source").getString("name");
                    String time = article_ob.getString("publishedAt");
                    String title = article_ob.getString("title");
                    String description = article_ob.getString("description");
                    String thumbnail_url = article_ob.getString("urlToImage");
                    String url = article_ob.getString("url");

                    feeds.add(new Article(time, source, title, description, thumbnail_url, url));
                    i++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
