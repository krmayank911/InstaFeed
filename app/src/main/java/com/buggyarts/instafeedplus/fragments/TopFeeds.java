package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.buggyarts.instafeedplus.Models.CricMchInnDetails;
import com.buggyarts.instafeedplus.Models.CricMchTeamScore;
import com.buggyarts.instafeedplus.Models.CricketMatch;
import com.buggyarts.instafeedplus.Models.CricketMatchState;
import com.buggyarts.instafeedplus.Models.ScoreCard;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.customClasses.GridItemDecoration;
import com.buggyarts.instafeedplus.utils.Article;
import com.buggyarts.instafeedplus.utils.Source;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.buggyarts.instafeedplus.utils.Constants.ALLSOURCES;
import static com.buggyarts.instafeedplus.utils.Constants.API_KEY;
import static com.buggyarts.instafeedplus.utils.Constants.BASE_URL;
import static com.buggyarts.instafeedplus.utils.Constants.CATEG_S;
import static com.buggyarts.instafeedplus.utils.Constants.GENERAL;
import static com.buggyarts.instafeedplus.utils.Constants.SOURCE;
import static com.buggyarts.instafeedplus.utils.Constants.SOURCES;
import static com.buggyarts.instafeedplus.utils.Constants.TOP_HEADLINES;

/**
 * Created by mayank on 1/6/18
 */

public class TopFeeds extends Fragment {

    View feedsView;

    String TAG = "TopFeeds";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;

    ObjectRecyclerViewAdapter adapter;
    GridItemDecoration gridItemDecoration;

    ProgressBar progressBar;

    Context context;
    ArrayList<Object> items;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cric_databaseReference, newsPoolReference;
    ArrayList<CricketMatch> matches;
    String SELECTED_COUNTRY = "in", SELECTED_LANGUAGE = "en";


    public static TopFeeds newInstance() {
        TopFeeds fragment = new TopFeeds();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(feedsView == null) {

            feedsView = inflater.inflate(R.layout.topfeeds, container, false);

            progressBar = feedsView.findViewById(R.id.progressBar);

            recyclerView = feedsView.findViewById(R.id.topfeeds_recyclerview);
            manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            adapter = new ObjectRecyclerViewAdapter(items, context);

            gridItemDecoration = new GridItemDecoration(20,true);
            recyclerView.addItemDecoration(gridItemDecoration);

            OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            recyclerView.setAdapter(adapter);

//        SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
//        snapHelper.attachToRecyclerView(recyclerView);

        }

        return feedsView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();

        SharedPreferences preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        if (preferences.contains("asked_before")) {
            SELECTED_COUNTRY = preferences.getString("country", "in");
            SELECTED_LANGUAGE = preferences.getString("language", "en");
        }

        items = new ArrayList<>();
        CATEG_S = new ArrayList<>();
        new GetFeeds().execute(BASE_URL + TOP_HEADLINES + "country=" + SELECTED_COUNTRY + "&apiKey=" + API_KEY);

        findSources();

        if(SELECTED_COUNTRY.equals("in")) {
            //cricket
            firebaseDatabase = FirebaseDatabase.getInstance();
            cric_databaseReference = firebaseDatabase.getReference().child("News").child("CricketNews");
            matches = new ArrayList<>();
            cric_databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    matches.clear();
                    String sourceString = dataSnapshot.toString().replace("DataSnapshot", "");
//                Log.d("JSON",sourceString);
                    extractSportsFeeds(sourceString);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            receiveNStoreFBData();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkConnection.isNetworkAvailale(context)) {
            Toast.makeText(context, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //Find All Sources
    public void findSources() {
        String url;
        if (SELECTED_LANGUAGE != null) {
            url = BASE_URL + ALLSOURCES + "&language=" + SELECTED_LANGUAGE + "&apiKey=" + API_KEY;
        } else {
            url = BASE_URL + ALLSOURCES + "&apiKey=" + API_KEY;
        }
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

                    boolean result = isInCategory(category);
                    if (!result) {
                        CATEG_S.add(category);
                    }

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
//        Log.v("SOURCES ARRAY", "CALL");
        while (i < SOURCES.size()) {
            Source source = SOURCES.get(i);
            if (source.category.equals(reqCategory) && source.country.equals(SELECTED_COUNTRY)) {
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

//        Log.v("URL", url);
        new GetFeeds().execute(url);
    }

    public class GetFeeds extends AsyncTask<String, Integer, String> {

        String json_res;

        @Override
        protected void onPreExecute() {
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
            Log.d("createFeeds: ", "Called");
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
                    items.add(new Article(time, source, title, description, thumbnail_url, url));
                    i++;
                }
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
    }

    public void extractSportsFeeds(String jsonResponse) {
        try {
            JSONObject responseObject = new JSONObject(jsonResponse);
            JSONArray mchs = responseObject.getJSONObject("value").getJSONObject("mchdata").getJSONArray("match");
            int i = 0;
            while (i < mchs.length()) {
                CricketMatch cricketMatch = new CricketMatch();
                JSONObject match = mchs.getJSONObject(i);
                JSONObject state = match.getJSONObject("state");


                CricketMatchState matchState = new CricketMatchState();
                matchState.setState(state.getString("@mchState"));
                matchState.setStatus(state.getString("@status"));


                CricMchInnDetails mchInnDetails = new CricMchInnDetails();
                CricMchTeamScore btnTeam = new CricMchTeamScore();
                CricMchTeamScore blngTeam = new CricMchTeamScore();

                if (!(matchState.getState().equals("Result") || matchState.getState().equals("nextlive") || matchState.getState().equals("preview"))) {

                    matchState.setTossWon(state.getString("@TW"));
                    matchState.setDecisn(state.getString("@decisn"));

                    JSONObject mScore = match.getJSONObject("mscr");
                    JSONObject inngDetail = mScore.getJSONObject("inngsdetail");
                    JSONObject btnTeamScore = mScore.getJSONObject("btTm");
                    JSONObject blngTeamScore = mScore.getJSONObject("blgTm");

                    mchInnDetails.setNoOfOvers(inngDetail.getString("@noofovers"));
                    mchInnDetails.setRrr(inngDetail.getString("@rrr"));
                    mchInnDetails.setCrr(inngDetail.getString("@crr"));
                    mchInnDetails.setCprtshp(inngDetail.getString("@cprtshp"));

                    btnTeam.setId(btnTeamScore.getString("@id"));
                    btnTeam.setsName(btnTeamScore.getString("@sName"));
                    blngTeam.setId(blngTeamScore.getString("@id"));
                    blngTeam.setsName(blngTeamScore.getString("@sName"));

//                    if(match.getString("@type").equals("TEST")){

                    try {
                        JSONObject btn_score = btnTeamScore.getJSONObject("Inngs");

                        btnTeam.setRuns(btn_score.getString("@r"));
                        btnTeam.setOvrs(btn_score.getString("@ovrs"));
                        btnTeam.setWkts(btn_score.getString("@wkts"));


                    } catch (JSONException btnObject_err) {

                        JSONArray btn_inngsArray = btnTeamScore.getJSONArray("Inngs");
                        int j = 0;
                        while (i < btn_inngsArray.length()) {
                            btnTeam.setRuns(btn_inngsArray.getJSONObject(j).getString("@r"));
                            btnTeam.setOvrs(btn_inngsArray.getJSONObject(j).getString("@ovrs"));
                            btnTeam.setWkts(btn_inngsArray.getJSONObject(j).getString("@wkts"));
                            i++;
                        }
                    }


                    try {

                        JSONObject blng_score = blngTeamScore.getJSONObject("Inngs");

                        blngTeam.setRuns(blng_score.getString("@r"));
                        blngTeam.setOvrs(blng_score.getString("@ovrs"));
                        blngTeam.setWkts(blng_score.getString("@wkts"));

                    } catch (JSONException blngObject_err) {

                        try {
                            JSONArray blng_inngsArray = blngTeamScore.getJSONArray("Inngs");
                            int k = 0;
                            while (k < blng_inngsArray.length()) {
                                blngTeam.setRuns(blng_inngsArray.getJSONObject(k).getString("@r"));
                                blngTeam.setOvrs(blng_inngsArray.getJSONObject(k).getString("@ovrs"));
                                blngTeam.setWkts(blng_inngsArray.getJSONObject(k).getString("@wkts"));
                                k++;
                            }
                        } catch (JSONException e) {

                            blngTeam.setRuns(" ");
                            blngTeam.setOvrs(" ");
                            blngTeam.setWkts(" ");
                        }


                    }

                } else if (matchState.getState().equals("Result")) {
                    cricketMatch.setMom(match.getJSONObject("manofthematch").getJSONObject("mom").getString("@Name"));
                    mchInnDetails = null;
                    btnTeam = null;
                    blngTeam = null;
                } else {
                    mchInnDetails = null;
                    btnTeam = null;
                    blngTeam = null;
                }

                cricketMatch.setType(match.getString("@type"));
                try {
                    cricketMatch.setMchDesc(match.getString("@mchDesc"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    cricketMatch.setMchDesc(" ");
                }
                try {
                    cricketMatch.setGrnd(match.getString("@grnd"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    cricketMatch.setGrnd(" ");
                }
                try {
                    cricketMatch.setNmch(match.getString("@nmch"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    try {
                        cricketMatch.setNmch(match.getString("@mnum"));
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                        cricketMatch.setNmch(" ");
                    }
                }
                try {
                    cricketMatch.setInngCnt(match.getString("@inngCnt"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    cricketMatch.setInngCnt(" ");
                }
                try {
                    cricketMatch.setTme_Dt(match.getJSONObject("Tme").getString("@Dt"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    cricketMatch.setTme_Dt(" ");
                }

                cricketMatch.setMatchState(matchState);
                cricketMatch.setInnDetails(mchInnDetails);
                cricketMatch.setBtnTeam(btnTeam);
                cricketMatch.setBlgnTeam(blngTeam);


                matches.add(cricketMatch);


                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (items.size() == 0) {
            items.add(0, new ScoreCard(matches));
        } else {
            items.set(0, new ScoreCard(matches));
        }

    }

    public boolean isInCategory(String category) {

        boolean result = false;

        int i = 0;
        while (i < CATEG_S.size()) {

            result = category.equals(CATEG_S.get(i));

            if (result) {
                return result;
            }

            i++;
        }

        return result;
    }

    public void receiveNStoreFBData(){
        newsPoolReference = firebaseDatabase.getReference().child("newsPoolIndia").child("English");
        newsPoolReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+ dataSnapshot.getValue().toString());
                String obj = dataSnapshot.getValue().toString();
                try {
                    JSONArray jsonArray = new JSONObject(obj).getJSONArray("articles");
                    int i = 0;
                    while(i < jsonArray.length()){
                        int j = 0;
                        JSONArray articles = (JSONArray) jsonArray.get(i);
                        while(j < articles.length()){

                            JSONObject article_ob = articles.getJSONObject(j);
                            String source = article_ob.getJSONObject("source").getString("name");
                            String time = article_ob.getString("publishedAt");
                            String title = article_ob.getString("title");
                            String description = article_ob.getString("description");
                            String thumbnail_url = article_ob.getString("urlToImage");
                            String url = article_ob.getString("url");
                            Boolean isTimeAvailable = article_ob.getBoolean("dateAvailable");
                            String timeFormat = article_ob.getString("dateFormat");
                            items.add(new Article(time, source, title, description, thumbnail_url, url,timeFormat,isTimeAvailable));
                            j++;

                        }
                        i++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
