package com.buggyarts.instafeedplus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.news.HomeFeedRecyclerObject;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.ResponseNewsApi;
import com.buggyarts.instafeedplus.Models.news.ResponseSources;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.recyclerAdapters.HomeFeedRecyclerAdapter;
import com.buggyarts.instafeedplus.customViews.FeedLoader;
import com.buggyarts.instafeedplus.customViews.SimpleToolBar;
import com.buggyarts.instafeedplus.rest.ApiClient;
import com.buggyarts.instafeedplus.rest.ApiInterface;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.buggyarts.instafeedplus.utils.Source;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_MEDIUM;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_SHORT;
import static com.buggyarts.instafeedplus.utils.Constants.NEWS_API_KEY;

public class CategoryExploreActivity extends AppCompatActivity implements SimpleToolBar.TopBarCallback, HomeFeedRecyclerAdapter.Callback {


    ArrayList<HomeFeedRecyclerObject> items = new ArrayList();
    String TAG = CategoryExploreActivity.class.getSimpleName();

    String DEFAULT_COUNTRY = "in";
    String DEFAULT_LANGUAGE = "en";
    String SELECTED_COUNTRY = DEFAULT_COUNTRY;
    String SELECTED_LANGUAGE = DEFAULT_LANGUAGE;
    String SELECTED_CATEGORY;

    int totalResults = 0;
    String listOfSources = "";
    int totalResultsBySource = 0;
    int articlesBySourcesCount = 0;
    int articlesByHeadlines = 0;
    boolean articlesByHeadlinesCompleted = false;

    int next = 1;
    int pageSize = 25;

    boolean callInProgress = false;

    RecyclerView.LayoutManager layoutManager;
    HomeFeedRecyclerAdapter adapter;

    SimpleToolBar toolBar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    FeedLoader feedLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_explore);

        toolBar = findViewById(R.id.toolBar);
        toolBar.setTopbarListener(this);
        toolBar.getOverflowButton().setVisibility(View.GONE);

        recyclerView = findViewById(R.id.feeds_recycler_view);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        feedLoader = findViewById(R.id.loaderView);
        feedLoader.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeFeedRecyclerAdapter(this,items);
        adapter.setCallback(this);
        recyclerView.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        if (preferences.contains("asked_before")) {
            SELECTED_COUNTRY = preferences.getString("country", DEFAULT_COUNTRY);
            SELECTED_LANGUAGE = preferences.getString("language", DEFAULT_LANGUAGE);
        }

        Intent intent = getIntent();
        if(intent !=null) {

            if(intent.hasExtra("categoryJson")) {
                Gson gson = new Gson();
                String categoryJson = intent.getStringExtra("categoryJson");
                Category category = gson.fromJson(categoryJson,Category.class);
                SELECTED_CATEGORY = category.getCategory();
            }
        }

        toolBar.getTitleLabel().setText(SELECTED_CATEGORY);
        toolBar.getTitleLabel().setAllCaps(true);

        getNewsArticles();
    }

    public void getNewsArticles(){

        if(next == 1){
            showLoader();
        }

//        progressBar.setVisibility(View.VISIBLE);
        callInProgress = true;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseNewsApi> call = apiService.getNewsArticlesByCategory(NEWS_API_KEY,SELECTED_COUNTRY,SELECTED_CATEGORY,pageSize,next);

        call.enqueue(new Callback<ResponseNewsApi>() {
            @Override
            public void onResponse(Call<ResponseNewsApi> call, Response<ResponseNewsApi> response) {
                callInProgress = false;
//                progressBar.setVisibility(View.GONE);
                hideLoader();

                if(response.code() == 200){

//                    totalResults = response.body().getTotalResults();
//                    extractItemNAdd(response.body().getNewsArticles(),1);
//                    getSourcesForCategory();

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
                        totalResults = 0;
                        articlesByHeadlines = 0;
                        articlesByHeadlinesCompleted = true;
                        extractItemNAdd(bufferList,next);
                        getSourcesForCategory();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseNewsApi> call, Throwable t) {
                callInProgress = false;
                hideLoader();
//                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t);
                getSourcesForCategory();
            }
        });

    }

    public void getSourcesForCategory(){

        callInProgress = true;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseSources> call = apiService.getSourcesForCategory(NEWS_API_KEY,SELECTED_CATEGORY,SELECTED_LANGUAGE);

        call.enqueue(new Callback<ResponseSources>() {
            @Override
            public void onResponse(Call<ResponseSources> call, Response<ResponseSources> response) {
                callInProgress = false;
                if(response.code() == 200){

                    ArrayList<Source> sourcesList = response.body().getSources();
                    listOfSources = createSourceString(sourcesList);

                    next = 1;
                    pageSize = 20;
                    getNewsArticlesBySource(listOfSources);

                }
            }

            @Override
            public void onFailure(Call<ResponseSources> call, Throwable t) {
                callInProgress = false;
                Log.d(TAG, "onFailure: " + t);
            }
        });

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

                    if(totalResultsBySource > articlesBySourcesCount){
                        next++;
                        extractItemNAdd(bufferList,next);
                        pageSize = AppUtils.getPageSize(totalResults, articlesByHeadlines);
                    }else {
                        next = 0;
                        extractItemNAdd(bufferList,next);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseNewsApi> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                callInProgress = false;
                Log.d(TAG, "onFailure: " + t);
            }
        });

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
        feedLoader.hideFeedLoader();
        feedLoader.setVisibility(View.GONE);
    }

    void showLoader(){
        feedLoader.setVisibility(View.VISIBLE);
        feedLoader.showFeedLoader();
    }

    @Override
    public void backButtonCalled() {
        this.finish();
    }

    @Override
    public void onSearchClick() {

    }

    @Override
    public void onPreferencesClick() {

    }

    @Override
    public void onBookmarksClick() {

    }

    @Override
    public void onRateUsClick() {

    }

    @Override
    public void onPrivacyPolicyClick() {

    }

    @Override
    public void getMoreData() {
        if(next!=0 && !callInProgress){
            getNewsArticlesBySource(listOfSources);
        }
    }

    @Override
    public void onCategoryClick(Category category) {

    }

    @Override
    public void bookmarkAction(boolean save, NewsArticle article) {
        if(save) {
            AppUtils.addToBookmark(article, this);
        }else {
            AppUtils.removeBookmark(article,this);
        }
    }

    @Override
    public void onArticleClick(NewsArticle article) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra("visit", article.url);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in_right,R.anim.activity_hold);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        closeActivityWithAnimation();
        return super.onKeyDown(keyCode, event);
    }

    public void closeActivityWithAnimation(){
        this.finish();
        overridePendingTransition(R.anim.activity_hold, R.anim.activity_slide_out_right);
    }
}
