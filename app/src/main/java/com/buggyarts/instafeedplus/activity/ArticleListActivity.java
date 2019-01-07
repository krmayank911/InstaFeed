package com.buggyarts.instafeedplus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.news.CallParam;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.NewsSpecial;
import com.buggyarts.instafeedplus.Models.news.PagesResponse;
import com.buggyarts.instafeedplus.Models.news.ResponseNewsApi;
import com.buggyarts.instafeedplus.Models.news.ResponseSources;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.VerticalPagerAdapter;
import com.buggyarts.instafeedplus.customViews.EmptyStateView;
import com.buggyarts.instafeedplus.customViews.SimpleToolBar;
import com.buggyarts.instafeedplus.customViews.VerticalViewPager;
import com.buggyarts.instafeedplus.rest.ApiClient;
import com.buggyarts.instafeedplus.rest.ApiInterface;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.buggyarts.instafeedplus.utils.Source;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buggyarts.instafeedplus.utils.Constants.NEWS_API_KEY;

public class ArticleListActivity extends AppCompatActivity implements VerticalPagerAdapter.Callback,
        SimpleToolBar.TopBarCallback, ViewPager.OnPageChangeListener, EmptyStateView.Callback {

    String TAG = ArticleListActivity.class.getSimpleName();
    ArrayList<NewsArticle> articles = new ArrayList<>();
    int next = 1;
    int totalResults = 0;
    boolean callInProgress = false;

    String listOfSources = "";
    int totalResultsBySource = 0;
    int articlesBySourcesCount = 0;
    int articlesByHeadlines = 0;
    boolean articlesByHeadlinesCompleted = false;
    int pageSize = 20;

    EmptyStateView noResultView;
    SimpleToolBar toolBar;
    FrameLayout loaderView;
    VerticalViewPager viewPager;
    VerticalPagerAdapter pagerAdapter;
    CallParam callParams;

    String DEFAULT_COUNTRY = "in";
    String DEFAULT_LANGUAGE = "en";
    String SELECTED_COUNTRY = DEFAULT_COUNTRY;
    String SELECTED_LANGUAGE = DEFAULT_LANGUAGE;
    String SELECTED_CATEGORY;

    FirebaseDatabase firebaseDatabase;
    PagesResponse pagesResponse;
    DatabaseReference pageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        toolBar = findViewById(R.id.toolBar);
        toolBar.getSearchButton().setVisibility(View.GONE);
        toolBar.getOverflowButton().setVisibility(View.GONE);
        toolBar.setTopbarListener(this);
        toolBar.setVisibility(View.GONE);
        toolBar.getTitleLabel().setText(getResources().getString(R.string.top_feeds_title));

        loaderView = findViewById(R.id.loader_view);
        viewPager = findViewById(R.id.vertical_view_pager);

        viewPager.addOnPageChangeListener(this);

        noResultView = findViewById(R.id.noResultView);
        noResultView.showActionButton();
        noResultView.setVisibility(View.GONE);
        noResultView.setCallback(this);

        initSwipePager();

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        if (preferences.contains("asked_before")) {
            SELECTED_COUNTRY = preferences.getString("country", DEFAULT_COUNTRY);
            SELECTED_LANGUAGE = preferences.getString("language", DEFAULT_LANGUAGE);
        }

        Intent intent = getIntent();
        if(intent!= null){

            if(intent.hasExtra("callParamsJson")){
                Gson gson = new Gson();
                String params = intent.getStringExtra("callParamsJson");
                callParams = gson.fromJson(params,CallParam.class);
                next = callParams.getPage() + 1;

                toolBar.getTitleLabel().setText(callParams.getQ());

                getSearchResults();

            }else if(intent.hasExtra("categoryJson")) {

                Gson gson = new Gson();
                String categoryJson = intent.getStringExtra("categoryJson");
                Category category = gson.fromJson(categoryJson,Category.class);
                SELECTED_CATEGORY = category.getCategory();

                toolBar.getTitleLabel().setText(SELECTED_CATEGORY);
                toolBar.getTitleLabel().setAllCaps(true);

                getNewsArticles();

            }else if(intent.hasExtra(getResources().getString(R.string.news_special_json))){

                firebaseCheck();
//                String json = intent.getStringExtra(getResources().getString(R.string.news_special_json));
//                Gson gson = new Gson();
//
//                NewsSpecial newsSpecial = gson.fromJson(json,NewsSpecial.class);
//
//                if(newsSpecial != null) {
//
//                    articles = newsSpecial.getCardsList();
//                    this.next = 0;
//                    pagerAdapter.articles = articles;
//                    pagerAdapter.notifyDataSetChanged();
//
//                }
            }
        }

    }

    private void resetAndReload(){
        hideNoResultView();
        if(callParams != null){
            next = callParams.getPage() + 1;
            getSearchResults();
        }else if(SELECTED_CATEGORY != null){
            getNewsArticles();
        }else {
            firebaseCheck();
        }
    }

    private void initSwipePager(){
        pagerAdapter = new VerticalPagerAdapter(this,articles);
        pagerAdapter.setCallback(this);
        viewPager.setAdapter(pagerAdapter);
    }

    public void getSearchResults(){

        if(next == 1){
            showLoader();
        }

        callInProgress = true;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseNewsApi> call = apiService.searchEveryThingWithParams(NEWS_API_KEY,
                callParams.getQ(),
                next,
                callParams.getLanguage(),
                callParams.getSortBy(),
                callParams.getTo(),
                callParams.getFrom());

        call.enqueue(new Callback<ResponseNewsApi>() {
            @Override
            public void onResponse(Call<ResponseNewsApi> call, Response<ResponseNewsApi> response) {

                callInProgress = false;
                if(next == 1) {
                    hideLoader();
                }
                if(response.code() == 200){

                    totalResults = response.body().getTotalResults();
                    if(totalResults > articles.size()){
                        next++;
                        pageSize = AppUtils.getPageSize(totalResults, articlesByHeadlines);
                    }else {
                        next = 0;
                    }
                    extractItemNAdd(response.body().getNewsArticles(),next);
                }
            }

            @Override
            public void onFailure(Call<ResponseNewsApi> call, Throwable t) {
                callInProgress = false;
                shouldShowNoResultView();
                if(next == 1) {
                    hideLoader();
                }
                Log.d(TAG, "onFailure: " + t);
            }
        });

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

//                    getSourcesForCategory();
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
                shouldShowNoResultView();
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
                callInProgress = false;
                Log.d(TAG, "onFailure: " + t);
                shouldShowNoResultView();
            }
        });

    }

    void extractItemNAdd(ArrayList<NewsArticle> list, int next){

        articles.addAll(list);
        this.next = next;
        pagerAdapter.articles = articles;
        pagerAdapter.notifyDataSetChanged();

        if(articles.size() > 0){
            hideLoader();
        }
    }

    public void showLoader(){
        loaderView.setVisibility(View.VISIBLE);
    }

    public void hideLoader(){
        loaderView.setVisibility(View.GONE);
    }

    @Override
    public void getMoreData() {
        if(next!=0 && !callInProgress){
            if(!articlesByHeadlinesCompleted) {
                getNewsArticles();
            }else if(callParams != null) {
                getSearchResults();
            } else{
                getNewsArticlesBySource(listOfSources);
            }
        }
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
    public void onArticleShareClick(NewsArticle article) {
        loadWithGlide(article.getThumbnail_url(),article.getTitle());
    }

    @Override
    public void onFocusClick() {
        if(toolBar.getVisibility() == View.GONE) {
            toolBar.setVisibility(View.VISIBLE);
            showToolBar(toolBar);
        }else {
            hideToolbar(toolBar);
        }
    }

    private void loadWithGlide(String image, final String title){
        Glide.with(this).asBitmap().load(image)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        shareArticle(resource, title);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

    private void shareArticle(Bitmap bitmap, String title){
        String fileName = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + ".jpg";

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = new File(filePath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
//            shareScreenShot(file);

            String message = title + "\n\n" + "Latest news feeds just 1 click away. Download InstaFeed+ " + "https://goo.gl/enVwXf";

            AppUtils.shareImageAction(this,file,message);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void backButtonCalled() {
        closeActivityWithAnimation();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_DEL){
            return false;
        }

        closeActivityWithAnimation();
        return super.onKeyDown(keyCode, event);
    }

    public void closeActivityWithAnimation(){
        this.finish();
        overridePendingTransition(R.anim.activity_hold, R.anim.activity_slide_out_right);
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
    public void onPrivacyPolicyClick() {

    }

    private void hideToolbar(View view) {

        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);
        slideDown.setDuration(300);
        view.startAnimation(slideDown);


        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

//        view.setVisibility(View.GONE);// use animate.translateY(height); instead
    }

    private void showToolBar(View view) {

        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        slideUp.setDuration(300);
        view.startAnimation(slideUp);

//        view.setVisibility(View.VISIBLE);// use animate.translateY(-height); instead
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state == ViewPager.SCROLL_STATE_IDLE){
            if(toolBar.getVisibility() == View.VISIBLE){
                hideToolbar(toolBar);
            }
        }
    }

    void shouldShowNoResultView(){
        if(articles.size() == 0){
            noResultView.setVisibility(View.VISIBLE);
            noResultView.getEmptyStateImage().setImageDrawable(getResources().getDrawable(R.drawable.no_internet));
            noResultView.getEmptyStateTitle().setText(getResources().getString(R.string.no_internet_error));
            noResultView.getEmptyStateText().setText(getResources().getString(R.string.no_internet_message));
            noResultView.getButtonAction().setText(getResources().getString(R.string.reload));
        }
    }

    void hideNoResultView(){
        noResultView.setVisibility(View.GONE);
    }

    @Override
    public void onEmptyStateActionClick() {
        resetAndReload();
    }

    void firebaseCheck(){

        if(NetworkConnection.isNetworkAvailale(this)) {
            retrievePageData();
        }else {
            shouldShowNoResultView();
        }
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
                    NewsSpecial newsSpecial = pagesResponse.getPages().get(0);
                    if(newsSpecial != null) {
                        articles = newsSpecial.getCardsList();
                        next = 0;
                        pagerAdapter.articles = articles;
                        pagerAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

}
