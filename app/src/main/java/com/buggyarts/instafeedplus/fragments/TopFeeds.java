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
import android.widget.ProgressBar;

import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.FeedsRecyclerViewAdapter;
import com.buggyarts.instafeedplus.utils.Article;
import com.buggyarts.instafeedplus.utils.Source;

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

import static com.buggyarts.instafeedplus.utils.Constants.ALLSOURCES;
import static com.buggyarts.instafeedplus.utils.Constants.API_KEY;
import static com.buggyarts.instafeedplus.utils.Constants.BASE_URL;
import static com.buggyarts.instafeedplus.utils.Constants.GENERAL;
import static com.buggyarts.instafeedplus.utils.Constants.SOURCE;
import static com.buggyarts.instafeedplus.utils.Constants.SOURCES;
import static com.buggyarts.instafeedplus.utils.Constants.TOP_HEADLINES;

/**
 * Created by mayank on 1/6/18
 */

public class TopFeeds extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    FeedsRecyclerViewAdapter adapter;

    ProgressBar progressBar;

    Context context;
    ArrayList<Article> feeds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View feedsView = inflater.inflate(R.layout.topfeeds, container, false);

        recyclerView = feedsView.findViewById(R.id.topfeeds_recyclerview);
        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        adapter = new FeedsRecyclerViewAdapter(feeds, context);
        recyclerView.setAdapter(adapter);

        progressBar = feedsView.findViewById(R.id.progressBar);

        return feedsView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();

        if (savedInstanceState != null) {
            feeds = savedInstanceState.getParcelableArrayList("feeds");
        } else {
            Log.v("ON SAVED INSTANCE", "null");
            feeds = new ArrayList<>();
            findSources();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {

        Bundle outState = new Bundle();
        outState.putParcelableArrayList("feeds", feeds);
        Log.v("ON SAVED INSTANCE", "CALL");
        onSaveInstanceState(outState);
        super.onStop();
    }

    //Find All Sources
    public void findSources() {
        String url = BASE_URL + ALLSOURCES + "&language=en" + "&apiKey=" + API_KEY;
        SOURCES = new ArrayList<>();
        new GetSources().execute(url);
    }

    public class GetSources extends AsyncTask<String, Void, String> {

        String json_res;

        @Override
        protected String doInBackground(String... strings) {
            json_res = getJson(strings[0]);
            return json_res;
        }

        @Override
        protected void onPostExecute(String res) {
            createSources(json_res);
            progressBar.setProgress(25);
            String listOfSources = filterByCategory(GENERAL);
            loadFeeds(listOfSources);
            super.onPostExecute(res);
        }
    }

    public void createSources(String jsonResponse) {
        if (jsonResponse != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray sourcesArray = jsonObject.getJSONArray("sources");
                int i = 0;
                while (i < sourcesArray.length()) {
                    JSONObject source_ob = sourcesArray.getJSONObject(i);
                    String id = source_ob.getString("id");
                    String name = source_ob.getString("name");
                    String category = source_ob.getString("category");
                    String language = source_ob.getString("language");
                    String country = source_ob.getString("country");
                    String url = source_ob.getString("url");
                    SOURCES.add(new Source(id, name, category, language, country, url));
                    i++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    String filterByCategory(String reqCategory) {
        int i = 0;
        ArrayList<Source> sourcesByCategory = new ArrayList<>();
        Log.v("SOURCES ARRAY", "CALL");
        while (i < SOURCES.size()) {
            Source source = SOURCES.get(i);
            if (source.category.equals(reqCategory) && source.country.equals("in")) {
                sourcesByCategory.add(source);
            }
            i++;
        }
        return createSourceString(sourcesByCategory);
    }

    public String createSourceString(ArrayList<Source> sources) {
        String listOfSources = "";
        int i = 0;
        int limit = 18;
        while (i < sources.size() && i < limit) {
            listOfSources = listOfSources + sources.get(i).id + ",";
            i++;
        }
        return listOfSources;
    }

    public void loadFeeds(String listOfSources) {
        String url = BASE_URL + TOP_HEADLINES + SOURCE + listOfSources + "&sortBy=popularity" + "&apiKey=" + API_KEY;
        Log.v("URL", url);
        new GetFeeds().execute(url);
    }

    public class GetFeeds extends AsyncTask<String, Integer, String> {

        String json_res;

        @Override
        protected void onPreExecute() {
            progressBar.setMax(100);
            publishProgress(50);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            json_res = getJson(strings[0]);
            publishProgress(75);
            return json_res;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String res) {
            progressBar.setProgress(100);
            progressBar.setVisibility(View.GONE);
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

                int total_length = httpURLConnection.getContentLength();
                int total = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    total += line.length();
//                    publishProgress((int)((total*100)/total_length));
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
                while (i <= article_count) {
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
