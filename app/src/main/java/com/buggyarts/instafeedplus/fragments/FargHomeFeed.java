package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.CatListObject;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.CricMchInnDetails;
import com.buggyarts.instafeedplus.Models.CricMchTeamScore;
import com.buggyarts.instafeedplus.Models.CricketMatch;
import com.buggyarts.instafeedplus.Models.CricketMatchState;
import com.buggyarts.instafeedplus.Models.news.HomeFeedRecyclerObject;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.NewsSpecial;
import com.buggyarts.instafeedplus.Models.news.PagesResponse;
import com.buggyarts.instafeedplus.Models.news.ResponseNewsApi;
import com.buggyarts.instafeedplus.Models.news.ResponseSources;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.activity.ArticleListActivity;
import com.buggyarts.instafeedplus.adapters.recyclerAdapters.HomeFeedRecyclerAdapter;
import com.buggyarts.instafeedplus.customClasses.GridItemDecoration;
import com.buggyarts.instafeedplus.customViews.EmptyStateView;
import com.buggyarts.instafeedplus.customViews.FeedLoader;
import com.buggyarts.instafeedplus.rest.ApiClient;
import com.buggyarts.instafeedplus.rest.ApiInterface;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.buggyarts.instafeedplus.utils.Source;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buggyarts.instafeedplus.utils.Constants.CATEGORIES;
import static com.buggyarts.instafeedplus.utils.Constants.CATEG_S;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_CAT;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_MEDIUM;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_SHORT;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_PAGER;
import static com.buggyarts.instafeedplus.utils.Constants.NEWS_API_KEY;

public class FargHomeFeed extends Fragment implements HomeFeedRecyclerAdapter.Callback, EmptyStateView.Callback, SwipeRefreshLayout.OnRefreshListener {

    View feedsView;

    String TAG = FargHomeFeed.class.getSimpleName();

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;

    HomeFeedRecyclerAdapter adapter;
    GridItemDecoration gridItemDecoration;

    LinearLayout progressBar;
    FeedLoader feedLoader;
    EmptyStateView noResultView;

    Context context;
    ArrayList<HomeFeedRecyclerObject> items = new ArrayList();

    FirebaseDatabase firebaseDatabase;
    PagesResponse pagesResponse;
    DatabaseReference cric_databaseReference, newsPoolReference, pageReference;
    ArrayList<CricketMatch> matches;

    String DEFAULT_COUNTRY = "in";
    String DEFAULT_LANGUAGE = "en";
    String SELECTED_COUNTRY;
    String SELECTED_LANGUAGE;
    int totalResults = 0;
    String listOfSources = "";
    int totalResultsBySource = 0;
    int articlesByHeadlines = 0;
    int articlesBySourcesCount = 0;
    int next = 1;
    int pageSize = 20;
    boolean callInProgress = false;
    boolean articlesByHeadlinesCompleted = false;

//    public static FargHomeFeed newInstance() {
//        FargHomeFeed fragment = new FargHomeFeed();
//        return fragment;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(feedsView == null) {

            context = getContext();
            feedsView = inflater.inflate(R.layout.topfeeds, container, false);

            progressBar = feedsView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);

            feedLoader = feedsView.findViewById(R.id.loaderView);
            feedLoader.setVisibility(View.GONE);

            noResultView = feedsView.findViewById(R.id.noResultView);
            noResultView.showActionButton();
            noResultView.setVisibility(View.GONE);
            noResultView.setCallback(this);

            swipeRefreshLayout = feedsView.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(this);

            recyclerView = feedsView.findViewById(R.id.topfeeds_recyclerview);
            manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            adapter = new HomeFeedRecyclerAdapter(context,items);
            adapter.setCallback(this);

//            OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            recyclerView.setAdapter(adapter);

            SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
            snapHelper.attachToRecyclerView(recyclerView);

            SharedPreferences preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
            if (preferences.contains("asked_before")) {
                SELECTED_COUNTRY = preferences.getString("country", DEFAULT_COUNTRY);
                SELECTED_LANGUAGE = preferences.getString("language", DEFAULT_LANGUAGE);
            }

            addCategories();
            retrievePageData();
            getNewsArticles();
        }

        return feedsView;
    }

    @Override
    public void onResume() {
        super.onResume();

//        if (!NetworkConnection.isNetworkAvailale(context)) {
//            Toast.makeText(context, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
//        }
    }

    public void retrievePageData(){

        firebaseDatabase = FirebaseDatabase.getInstance();
        pageReference = firebaseDatabase.getReference().child("NewsApi").child("pages");

        pageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String obj = dataSnapshot.getValue().toString();

                try {
                    Gson gson = new Gson();
                    pagesResponse = gson.fromJson(obj,PagesResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(pagesResponse != null){

                    if(items.size() >= 2) {
                        items.remove(1);
                        items.add(1, new HomeFeedRecyclerObject(FEED_TYPE_PAGER, pagesResponse));
                    }else {
                        items.add(1, new HomeFeedRecyclerObject(FEED_TYPE_PAGER, pagesResponse));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getNewsArticles(){

//        progressBar.setVisibility(View.VISIBLE);
        if(next == 1){
            showLoader();
        }
        callInProgress = true;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseNewsApi> call = apiService.getNewsArticles(NEWS_API_KEY,SELECTED_COUNTRY,pageSize,next);

        call.enqueue(new Callback<ResponseNewsApi>() {
            @Override
            public void onResponse(Call<ResponseNewsApi> call, Response<ResponseNewsApi> response) {
                callInProgress = false;
                hideLoader();
//                progressBar.setVisibility(View.GONE);

                if(response.code() == 200){

//                    totalResults = response.body().getTotalResults();
//                    extractItemNAdd(response.body().getNewsArticles(),1);
                    totalResults = response.body().getTotalResults();
                    ArrayList bufferList = response.body().getNewsArticles();

                    if(next == 1){
                        articlesByHeadlines = bufferList.size();
                    }else {
                        articlesByHeadlines = articlesByHeadlines + bufferList.size();
                    }

                    if(totalResults > articlesByHeadlines){

                        next++;
                        extractItemNAdd(bufferList,next);
                        pageSize = AppUtils.getPageSize(totalResults, articlesByHeadlines);

                    }else {
                        next = 0;
                        articlesByHeadlines = 0;
                        totalResults = 0;
                        articlesByHeadlinesCompleted = true;
                        extractItemNAdd(bufferList,next);
                        getSourcesForCountry();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseNewsApi> call, Throwable t) {
                callInProgress = false;
                hideLoader();
                Log.d(TAG, "onFailure: " + t);
                getSourcesForCountry();
            }
        });

    }

    public void getSourcesForCountry(){

        callInProgress = true;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseSources> call = apiService.getSourcesForCountry(NEWS_API_KEY,SELECTED_COUNTRY,SELECTED_LANGUAGE);

        call.enqueue(new Callback<ResponseSources>() {
            @Override
            public void onResponse(Call<ResponseSources> call, Response<ResponseSources> response) {
                callInProgress = false;
                if(response.code() == 200){

                    ArrayList<Source> sourcesList = response.body().getSources();
//                    createSourceString(filterByCategory(GENERAL,sourcesList));
                    listOfSources = createSourceString(sourcesList);

                    next = 1;
                    pageSize = 20;
                    getNewsArticlesBySource(listOfSources);

                }
            }

            @Override
            public void onFailure(Call<ResponseSources> call, Throwable t) {
                callInProgress = false;
                shouldShowNoResultView();
                Log.d(TAG, "onFailure: " + t);
            }
        });

    }

    public ArrayList<Source> filterByCategory(String reqCategory,ArrayList<Source> allSources) {
        ArrayList<Source> sourcesByCategory = new ArrayList<>();
        for(Source source : allSources){
            if (source.getCategory().equals(reqCategory)) {
                sourcesByCategory.add(source);
            }
        }
        return sourcesByCategory;
    }

    public String createSourceString(ArrayList<Source> sources) {
        String listOfSources = "";
        for(Source source : sources){
            listOfSources = listOfSources + source.getId() + ",";
        }
        return listOfSources;
    }

    public void getNewsArticlesBySource(String listOfSources){

        callInProgress = true;
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseNewsApi> call = apiService.getNewsArticlesBySources(NEWS_API_KEY,listOfSources,pageSize,next);

        call.enqueue(new Callback<ResponseNewsApi>() {
            @Override
            public void onResponse(Call<ResponseNewsApi> call, Response<ResponseNewsApi> response) {
                callInProgress = false;
                progressBar.setVisibility(View.GONE);

                if(response.code() == 200){

                    totalResultsBySource = response.body().getTotalResults();
                    ArrayList bufferList = response.body().getNewsArticles();

                    if(next == 1){
                        articlesBySourcesCount = bufferList.size();
                    }else {
                        articlesBySourcesCount = articlesBySourcesCount + bufferList.size();
                    }

//                    if(items.size() > 0){
//                        items.addAll(bufferList);
//                    }else {
//                        items = bufferList;
//                    }
//                    adapter.itemsList = items;
//                    adapter.notifyDataSetChanged();

                    if(totalResultsBySource > articlesBySourcesCount){
                        next++;
                        extractItemNAdd(bufferList,next);
                    }else {
                        next = 0;
                        extractItemNAdd(bufferList,next);
                        if(SELECTED_COUNTRY.equals("in")) {
                            getIndianNews();
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseNewsApi> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                callInProgress = false;
                Log.d(TAG, "onFailure: " + t);
                if(items.size() == 0){
                    if(SELECTED_COUNTRY.equals("in")) {
                        getIndianNews();
                    }else {
                        shouldShowNoResultView();
                    }
                }
            }
        });

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
//                    items.add(new Article(time, source, title, description, thumbnail_url, url));
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
//            items.add(0, new ScoreCard(matches));
        } else {
//            items.set(0, new ScoreCard(matches));
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

    public void getIndianNews(){

        final Gson gson = new Gson();

        firebaseDatabase = FirebaseDatabase.getInstance();
        newsPoolReference = firebaseDatabase.getReference().child("newsPoolIndia").child("English");
        newsPoolReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: "+ dataSnapshot.getValue().toString());
                String obj = dataSnapshot.getValue().toString();

                try {
                    JSONArray mainArray = new JSONObject(obj).getJSONArray("articles");
                    ArrayList<NewsArticle> bufferList = new ArrayList<>();
                    int i = 0;
                    while(i < mainArray.length()){
                        int j = 0;
                        JSONArray listOfArticles = (JSONArray) mainArray.get(i);
                        while(j < listOfArticles.length()) {

                            NewsArticle article = gson.fromJson(listOfArticles.get(j).toString(),NewsArticle.class);
                            bufferList.add(article);
                            j++;

//                            JSONObject article_ob = listOfArticles.getJSONObject(j);
//                            String source = article_ob.getJSONObject("source").getString("name");
//                            String time = article_ob.getString("publishedAt");
//                            String title = article_ob.getString("title");
//                            String description = article_ob.getString("description");
//                            String thumbnail_url = article_ob.getString("urlToImage");
//                            String url = article_ob.getString("url");
//                            Boolean isTimeAvailable = article_ob.getBoolean("dateAvailable");
//                            String timeFormat = article_ob.getString("dateFormat");
//                            items.add(new NewsArticle(time, source, title, description, thumbnail_url, url,timeFormat,isTimeAvailable));
//                            j++;
                        }
                        i++;
                    }

                    extractItemNAdd(bufferList,0);
//                    adapter.itemsList = items;
//                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                shouldShowNoResultView();
            }
        });
    }

    @Override
    public void getMoreData() {
        if(next!=0 && !callInProgress){
            if(!articlesByHeadlinesCompleted) {
                getNewsArticles();
            }else {
                getNewsArticlesBySource(listOfSources);
            }
        }
    }

    @Override
    public void onCategoryClick(Category category) {

        Gson gson = new Gson();
        String categoryJson = gson.toJson(category);

        Intent intent = new Intent(context, ArticleListActivity.class);
        intent.putExtra("categoryJson", categoryJson);
        context.startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.activity_slide_in_right,R.anim.activity_hold);
    }

    @Override
    public void bookmarkAction(boolean save, NewsArticle article) {
        if(save) {
            AppUtils.addToBookmark(article, context);
        }else {
            AppUtils.removeBookmark(article,context);
        }
    }

    @Override
    public void onArticleClick(NewsArticle article) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra("visit", article.url);
        context.startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.activity_slide_in_right,R.anim.activity_hold);
    }

    void addCategories(){

        CatListObject catListObject = new CatListObject();
        int i = 0;
        while (i < CATEGORIES.length) {
            catListObject.getCategories().add(new Category(CATEGORIES[i]));
            i++;
        }
        items.add(0,new HomeFeedRecyclerObject(FEED_TYPE_CAT,catListObject));
    }

    void extractItemNAdd(ArrayList<NewsArticle> articleArrayList, int next){

        ArrayList<HomeFeedRecyclerObject> bufferList = new ArrayList<>();

        for(NewsArticle article : articleArrayList){
            int type = (Math.random() > 0.5) ? FEED_TYPE_NA_MEDIUM : FEED_TYPE_NA_SHORT;
            bufferList.add(new HomeFeedRecyclerObject(type,article));
        }

        items.addAll(bufferList);
        this.next = next;
        adapter.next = this.next;
        adapter.itemsList = items;
        adapter.notifyDataSetChanged();
    }

    void hideLoader(){
        feedLoader.hideHomeFeedLoader();
        feedLoader.setVisibility(View.GONE);
    }

    void showLoader(){
        feedLoader.setVisibility(View.VISIBLE);
        feedLoader.showHomeFeedLoader();
    }

    void shouldShowNoResultView(){
        if(items.size() < 3){
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

    @Override
    public void onEmptyStateActionClick() {
        resetAndReload();
    }

    void resetAndReload(){
        items.clear();
        adapter.itemsList = items;
        adapter.notifyDataSetChanged();
        totalResultsBySource = 0;
        totalResults = 0;
        articlesBySourcesCount = 0;
        next = 1;

        hideNoResultView();
        addCategories();
        retrievePageData();
        getNewsArticles();
    }

    public void showCards(Context context){

        Intent intent = new Intent(context, ArticleListActivity.class);
        intent.putExtra(context.getResources().getString(R.string.news_special_json), true);
        context.startActivity(intent);

    }

    public void showNotification(){
        NewsSpecial newsSpecial = pagesResponse.getPages().get(0);
        if(newsSpecial.getCards()) {

            Gson gson = new Gson();
            String responseJson = gson.toJson(newsSpecial);

            Intent intent = new Intent(context, ArticleListActivity.class);
            intent.putExtra(context.getResources().getString(R.string.news_special_json), responseJson);
            context.startActivity(intent);
            this.getActivity().overridePendingTransition(R.anim.activity_slide_in_right,R.anim.activity_hold);
        }
    }

    @Override
    public void onRefresh() {
        resetAndReload();
        swipeRefreshLayout.setRefreshing(false);
    }

}
