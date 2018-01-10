package com.buggyarts.instafeedplus;

import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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
import static com.buggyarts.instafeedplus.utils.Constants.CATEGORIES;
import static com.buggyarts.instafeedplus.utils.Constants.SOURCE;
import static com.buggyarts.instafeedplus.utils.Constants.SOURCES;
import static com.buggyarts.instafeedplus.utils.Constants.TOP_HEADLINES;

public class FeedsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    FeedsRecyclerViewAdapter adapter;
    String CATEGORY = CATEGORIES[1];
    ArrayList<Article> feeds;

    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);

        CATEGORY = getIntent().getStringExtra("category");
        int index = getIntent().getIntExtra("index", 1);
        Log.v("CATEGORY", "" + index);
        CATEGORY = CATEGORIES[index];


        feeds = new ArrayList<>();
        findSources();

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(CATEGORY.toUpperCase()
                .replace("-AND-MEDICAL", "").replace("-AND-NATURE", ""));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_up);

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolBar);

        toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        toolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));
//        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedText);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.ExpendedText);


        recyclerView = (RecyclerView) findViewById(R.id.feeds_recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new FeedsRecyclerViewAdapter(feeds, this);
        recyclerView.setAdapter(adapter);

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
            Log.v("Category", CATEGORY);
            String listOfSources = filterByCategory(CATEGORY);
            Log.v("listOfSources", listOfSources);
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
            Log.v("SOURCES ARRAY", "DONE");
        }
    }

    String filterByCategory(String reqCategory) {
        int i = 0;
        ArrayList<Source> sourcesByCategory = new ArrayList<>();
        Log.v("SOURCES ARRAY", "CALL");
        if (SOURCES.size() == 0) {
            Log.v("SOURCES COUNT", "ZERO");
        }
        while (i < SOURCES.size()) {
            Source source = SOURCES.get(i);
            if (source.category.equals(reqCategory)) {
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
