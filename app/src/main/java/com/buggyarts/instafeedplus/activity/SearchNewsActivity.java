package com.buggyarts.instafeedplus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.news.HomeFeedRecyclerObject;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.ResponseNewsApi;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.recyclerAdapters.HomeFeedRecyclerAdapter;
import com.buggyarts.instafeedplus.customViews.EmptyStateView;
import com.buggyarts.instafeedplus.customViews.FeedLoader;
import com.buggyarts.instafeedplus.customViews.SearchBar;
import com.buggyarts.instafeedplus.rest.ApiClient;
import com.buggyarts.instafeedplus.rest.ApiInterface;
import com.buggyarts.instafeedplus.utils.AppUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_MEDIUM;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_SHORT;
import static com.buggyarts.instafeedplus.utils.Constants.NEWS_API_KEY;

public class SearchNewsActivity extends AppCompatActivity implements SearchBar.Callbacks, HomeFeedRecyclerAdapter.Callback {

    SearchBar searchBar;
    RecyclerView searchRV;
    RecyclerView.LayoutManager layoutManager;
    HomeFeedRecyclerAdapter feedRecyclerAdapter;
    EmptyStateView emptyStateView;
    FeedLoader feedLoader;

    String TAG = SearchNewsActivity.class.getSimpleName();
    ArrayList<HomeFeedRecyclerObject> articleArrayList = new ArrayList<>();
    int totalResults;
    int next = 1;
    boolean callInProgress = false;

    String currentSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_news);

        feedLoader = findViewById(R.id.loaderView);
        feedLoader.setVisibility(View.GONE);

        emptyStateView = findViewById(R.id.empty_state_view);

        searchBar = findViewById(R.id.searchBar);
        searchBar.getSearchText().setVisibility(View.VISIBLE);
        searchBar.setCallbacks(this);

        searchRV = findViewById(R.id.search_rv);
        layoutManager = new LinearLayoutManager(this);
        feedRecyclerAdapter = new HomeFeedRecyclerAdapter(this,articleArrayList);
        feedRecyclerAdapter.setCallback(this);
        searchRV.setLayoutManager(layoutManager);
        searchRV.setAdapter(feedRecyclerAdapter);

        showEmptyState();
    }

    void showEmptyState(){
        emptyStateView.getEmptyStateImage().setImageResource(R.drawable.if_search_big);
        emptyStateView.getEmptyStateTitle().setText("Explore for more");
        emptyStateView.getEmptyStateText().setText("Search for sources, people, topics... ");
        emptyStateView.setVisibility(View.VISIBLE);
    }

    void hideEmptyState(){
        emptyStateView.setVisibility(View.GONE);
    }

    public void getSearchResults(String searchString){

        if(next == 1){
            showLoader();
        }

        callInProgress = true;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseNewsApi> call = apiService.searchEveryThing(NEWS_API_KEY,searchString,next);

        call.enqueue(new Callback<ResponseNewsApi>() {
            @Override
            public void onResponse(Call<ResponseNewsApi> call, Response<ResponseNewsApi> response) {

                callInProgress = false;
                if(next == 1) {
                    hideLoader();
                }
                if(response.code() == 200){

                    totalResults = response.body().getTotalResults();
                    if(totalResults > articleArrayList.size()){
                        next++;
                    }else {
                        next = 0;
                    }
                    extractItemNAdd(response.body().getNewsArticles(),next);
//                    if(next == 1){
//                        articleArrayList = response.body().getNewsArticles();
//                    }else {
//                        articleArrayList.addAll(response.body().getNewsArticles());
//                    }

//                    feedRecyclerAdapter.itemsList = articleArrayList;
//                    feedRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseNewsApi> call, Throwable t) {
                callInProgress = false;
                if(next == 1) {
                    hideLoader();
                }
                Log.d(TAG, "onFailure: " + t);
            }
        });

    }

    @Override
    public void onSearchClick(String searchString) {
        hideSoftKeyboard(this);
        articleArrayList.clear();

        if(feedRecyclerAdapter.itemsList.size()>0) {
            feedRecyclerAdapter.itemsList.clear();
            feedRecyclerAdapter.notifyDataSetChanged();
        }
        getSearchResults(searchString);
        next = 1;
        currentSearchString = searchString;
    }

    @Override
    public void onBackButtonClick() {
        hideSoftKeyboard(this);
        closeActivityWithAnimation();
    }


    @Override
    public void getMoreData() {
//        if(articleArrayList.size() < totalResults && !callInProgress){
//            next = next + 1;
//            getSearchResults(currentSearchString);
//        }
        if(next!=0 && !callInProgress){
            getSearchResults(currentSearchString);
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

    void extractItemNAdd(ArrayList<NewsArticle> list, int next){

        ArrayList<HomeFeedRecyclerObject> bufferList = new ArrayList<>();

        for(NewsArticle article : list){
            int type = (Math.random() > 0.5) ? FEED_TYPE_NA_MEDIUM : FEED_TYPE_NA_SHORT;
            bufferList.add(new HomeFeedRecyclerObject(type,article));
        }

        articleArrayList.addAll(bufferList);
        this.next = next;
        feedRecyclerAdapter.next = this.next;
        feedRecyclerAdapter.itemsList = articleArrayList;
        feedRecyclerAdapter.notifyDataSetChanged();

        if(articleArrayList.size() > 0){
            hideEmptyState();
        }
    }

    void hideLoader(){
        feedLoader.hideFeedLoader();
        feedLoader.setVisibility(View.GONE);
    }

    void showLoader(){
        feedLoader.setVisibility(View.VISIBLE);
        feedLoader.showFeedLoader();
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void openSoftKeyboard(Activity activity) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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
}
