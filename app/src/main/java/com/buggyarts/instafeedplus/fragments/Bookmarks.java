package com.buggyarts.instafeedplus.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.news.HomeFeedRecyclerObject;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.ObjectRecyclerViewAdapter;
import com.buggyarts.instafeedplus.adapters.recyclerAdapters.HomeFeedRecyclerAdapter;
import com.buggyarts.instafeedplus.customViews.EmptyStateView;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.buggyarts.instafeedplus.utils.Article;
import com.buggyarts.instafeedplus.utils.data.DbUser;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_MEDIUM;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_SHORT;

/**
 * Created by mayank on 2/16/18
 */

public class Bookmarks extends Fragment implements HomeFeedRecyclerAdapter.Callback {

    View feedsView;
    Context mContext;
    ArrayList<HomeFeedRecyclerObject> articleArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    HomeFeedRecyclerAdapter adapter;
    EmptyStateView emptyStateView;
    int next = 0;

    AdView adView;

    public static Bookmarks newInstance() {
        Bookmarks fragment = new Bookmarks();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if(feedsView == null) {

            mContext = getContext();

            feedsView = inflater.inflate(R.layout.sample_fragment, container, false);

            emptyStateView = feedsView.findViewById(R.id.empty_state_view);
            emptyStateView.setVisibility(View.GONE);

            recyclerView = feedsView.findViewById(R.id.categories_recycler_view);
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new HomeFeedRecyclerAdapter(mContext, articleArrayList);
            adapter.setCallback(this);
            recyclerView.setAdapter(adapter);

//            if(AppUtils.getBookmarks(mContext) != null){
//                extractItemNAdd(AppUtils.getBookmarks(mContext),0);
//            }else{
//                showEmptyState();
//            }

//            SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
//            snapHelper.attachToRecyclerView(recyclerView);

//        adView = view.findViewById(R.id.banner_adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);
//
//        adView.setAdListener(adListener);
        }

        return feedsView;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        list = new ArrayList<>();
//
////        DbUser user = new DbUser(mContext);
////        ArrayList<String> stringArrayList = user.readArticlesFromDB();
//
//
//
//        Log.d("Bookmark: ", "" + stringArrayList.size());
//
//        int i = 0;
//        while (i < stringArrayList.size()) {
//            createFeeds(stringArrayList.get(i));
//            i++;
//        }
//
//    }

    @Override
    public void onResume() {
        super.onResume();

        if (!NetworkConnection.isNetworkAvailale(mContext)) {
            Toast.makeText(mContext, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
        }

//        AppUtils.clearAllBookmark(mContext);

        if(AppUtils.getBookmarks(mContext) != null){
            articleArrayList.clear();
            extractItemNAdd(AppUtils.getBookmarks(mContext),0);
        }else{
            showEmptyState();
        }
    }

    void extractItemNAdd(ArrayList<NewsArticle> list, int next){

        ArrayList<HomeFeedRecyclerObject> bufferList = new ArrayList<>();

        for(NewsArticle article : list){
            int type = (Math.random() > 0.5) ? FEED_TYPE_NA_MEDIUM : FEED_TYPE_NA_SHORT;
            bufferList.add(new HomeFeedRecyclerObject(type,article));
        }

        articleArrayList.addAll(bufferList);
        this.next = next;
        adapter.next = this.next;
        adapter.itemsList = articleArrayList;
        adapter.notifyDataSetChanged();

        if(articleArrayList.size() > 0){
            hideEmptyState();
        }else {
            showEmptyState();
        }
    }

    void showEmptyState(){
        emptyStateView.getEmptyStateImage().setImageResource(R.drawable.if_bookmark_big);
        emptyStateView.getEmptyStateTitle().setText("Save");
        emptyStateView.getEmptyStateText().setText("Save news articles to read later.. ");
        emptyStateView.setVisibility(View.VISIBLE);
    }

    void hideEmptyState(){
        emptyStateView.setVisibility(View.GONE);
    }

    @Override
    public void getMoreData() {

    }

    @Override
    public void onCategoryClick(Category category) {

    }

    @Override
    public void bookmarkAction(boolean save, NewsArticle article) {
        if(save) {
            AppUtils.addToBookmark(article, mContext);
        }else {
            AppUtils.removeBookmark(article,mContext);
            if(AppUtils.getBookmarks(mContext) != null){
                articleArrayList.clear();
                extractItemNAdd(AppUtils.getBookmarks(mContext),0);
            }else{
                showEmptyState();
            }
        }
    }

    @Override
    public void onArticleClick(NewsArticle article) {
        Intent intent = new Intent(mContext, BrowserActivity.class);
        intent.putExtra("visit", article.url);
        mContext.startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.activity_slide_in_right,R.anim.activity_hold);
    }

//    public void createFeeds(String jsonResponse) {
//
//        Log.d("Bookmark: ", jsonResponse);
//
//        if (jsonResponse != null) {
//            try {
//                JSONObject article_ob = new JSONObject(jsonResponse);
//                String source = "";
//                if(article_ob.getJSONObject("source") != null){
//                    source = article_ob.getJSONObject("source").getString("name");
//                }
//
//                String time = article_ob.getString("publishedAt");
//
//                String title = article_ob.getString("title");
//
//                String description = article_ob.getString("description");
//
//                String urlToImage = article_ob.getString("urlToImage");
//
//                String url = article_ob.getString("url");
//
//                String timeFormat = article_ob.getString("timeFormat");
//
//                if(timeFormat.equals("GoogleTimeFormat")){
//                    Article article = new Article(time, source, title, description, urlToImage, url);
//                    article.setBookmarked(true);
//                    list.add(article);
//                }else{
//                    Boolean isTimeAvailable = article_ob.getBoolean("isTimeAvailable");
//                    Article article = new Article(time, source, title, description, urlToImage, url,timeFormat,isTimeAvailable);
//                    article.setBookmarked(true);
//                    list.add(article);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    AdListener adListener = new AdListener(){
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                adView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                adView.setVisibility(View.GONE);
//            }
//    };
}
