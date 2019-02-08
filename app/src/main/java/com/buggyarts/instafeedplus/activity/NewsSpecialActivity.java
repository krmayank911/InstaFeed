package com.buggyarts.instafeedplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.Header;
import com.buggyarts.instafeedplus.Models.PageHeader;
import com.buggyarts.instafeedplus.Models.news.Block;
import com.buggyarts.instafeedplus.Models.news.CallParam;
import com.buggyarts.instafeedplus.Models.news.HomeFeedRecyclerObject;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.NewsSpecial;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.recyclerAdapters.PageRecyclerViewAdapter;
import com.buggyarts.instafeedplus.customClasses.GlideApp;
import com.buggyarts.instafeedplus.customViews.IFToolBar;
import com.buggyarts.instafeedplus.utils.data.NetworkConnection;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_HEADER;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_MORE;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_MEDIUM;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_SHORT;

public class NewsSpecialActivity extends AppCompatActivity implements IFToolBar.Callback, PageRecyclerViewAdapter.Callback, AppBarLayout.OnOffsetChangedListener {

    AppBarLayout appBarLayout;
    IFToolBar simpleToolBar;
    RecyclerView recyclerView;

    TextView title;
    TextView subText;
    TextView publishTime;
    ImageView thumbnail;

    RecyclerView.LayoutManager layoutManager;
    PageRecyclerViewAdapter pageRecyclerViewAdapter;
    ArrayList<HomeFeedRecyclerObject> homeFeedRecyclerObjects = new ArrayList<>();

    NewsSpecial newsSpecial;

    String TAG = NewsSpecialActivity.class.getSimpleName();
    int newsSpecialPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_special);

        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        simpleToolBar = findViewById(R.id.toolBar);
        simpleToolBar.setCallback(this);
        simpleToolBar.getTitleLabel().setAlpha(0);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        pageRecyclerViewAdapter = new PageRecyclerViewAdapter(this, homeFeedRecyclerObjects);
        pageRecyclerViewAdapter.setCallback(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pageRecyclerViewAdapter);

        title = findViewById(R.id.title_label);
        subText = findViewById(R.id.sub_text);
        publishTime = findViewById(R.id.published_at);
        thumbnail = findViewById(R.id.thumbnail);

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra(getResources().getString(R.string.news_special_json))){
                String json = intent.getStringExtra(getResources().getString(R.string.news_special_json));
                Gson gson = new Gson();
                newsSpecial = gson.fromJson(json,NewsSpecial.class);

                if(newsSpecial != null) {
                    extractItemNAdd();
                }
            }else if(intent.hasExtra(getResources().getString(R.string.news_special_notification))){
                if(intent.hasExtra(getResources().getString(R.string.news_special_page_index))) {
                    newsSpecialPageIndex = intent.getIntExtra(getResources().getString(R.string.news_special_page_index), 0);
                }

                firebaseCheck();
            }
        }
    }

    void extractItemNAdd(){

        ArrayList<HomeFeedRecyclerObject> bufferList = new ArrayList<>();


        PageHeader pageHeader = new PageHeader(newsSpecial.getPageTitle(),
                newsSpecial.getPageSubText(),
                newsSpecial.getPageDate(),
                newsSpecial.getPageThumbnailUrl());

        simpleToolBar.getTitleLabel().setText(pageHeader.getPageTitle());
        title.setText(pageHeader.getPageTitle());
        subText.setText(pageHeader.getPageSubText());
        publishTime.setText(pageHeader.getPageDate());
//        GlideApp.with(this).load(pageHeader.getPageThumbnail()).centerCrop().into(thumbnail);

        if(pageHeader.getPageThumbnail().length()>5) {
            GlideApp.with(this).load("https://www.dailynews.com/wp-content/uploads/2017/09/img_3776.jpg").centerInside().into(thumbnail);
        }else {

            if(newsSpecial.getBlocks().size() > 0) {
                Random random = new Random();
                int blockIndex = 0;
                if(newsSpecial.getBlocks().size() > 1) {
                    blockIndex = random.nextInt(newsSpecial.getBlocks().size());
                }
                Block block = newsSpecial.getBlocks().get(blockIndex);

                if(block.getCallResult().size()>0) {
                    int articleIndex = 0;
                    if (block.getCallResult().size() > 1) {
                        articleIndex = random.nextInt(block.getCallResult().size());
                    }

                    GlideApp.with(this).load(block.getCallResult().get(articleIndex).getUrlToImage())
                            .placeholder(getResources().getDrawable(R.drawable.placeholder_landscape))
                            .optionalCenterCrop().into(thumbnail);
                }else {
                    thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.placeholder_landscape));
                }
            }else {
                thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.placeholder_landscape));
            }
        }


        for(Block block : newsSpecial.getBlocks()){

            Header header = new Header(block.getHeader());
            bufferList.add(new HomeFeedRecyclerObject(FEED_TYPE_HEADER,header));

            for(int i = 0; i < block.getCallResult().size(); i++){
                int type = (i == 0) ? FEED_TYPE_NA_MEDIUM : FEED_TYPE_NA_SHORT;
                bufferList.add(new HomeFeedRecyclerObject(type,block.getCallResult().get(i)));
            }

            for(int i = 0; i < block.getCallParams().size(); i++){
                bufferList.add(new HomeFeedRecyclerObject(FEED_TYPE_MORE,block.getCallParams().get(i)));
            }

        }

        homeFeedRecyclerObjects.addAll(bufferList);
        pageRecyclerViewAdapter.next = 0;
        pageRecyclerViewAdapter.itemsList = homeFeedRecyclerObjects;
        pageRecyclerViewAdapter.notifyDataSetChanged();

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
    public void getMoreData() {
    }

    @Override
    public void bookmarkAction(boolean save, NewsArticle article) {

    }

    @Override
    public void onArticleClick(NewsArticle article) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra("visit", article.url);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in_right,R.anim.activity_hold);
    }

    @Override
    public void onMoreArticlesClick(CallParam params) {

        Gson gson = new Gson();
        String paramsJson = gson.toJson(params);

        Intent intent = new Intent(NewsSpecialActivity.this, ArticleListActivity.class);
        intent.putExtra("callParamsJson",paramsJson);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in_right,R.anim.activity_hold);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        Log.d(TAG, "onOffsetChanged: "+verticalOffset);

        float threshold = 180;
        float percentage;
        float positiveOffset = (-verticalOffset);

        if (Math.abs(positiveOffset) < threshold) {
            percentage = 0;
        } else if(Math.abs(positiveOffset)>threshold &&  Math.abs(positiveOffset) <= 480){
            percentage = (Math.abs(positiveOffset)) / 240;
        } else {
            percentage = 1;
        }

        Log.d(TAG, "percentage: "+ percentage);

        simpleToolBar.updateToolBarAlpha(percentage,this);
    }

    void firebaseCheck(){

        if(NetworkConnection.isNetworkAvailale(this)) {
            retrievePageData();
        }else {
            closeActivityWithAnimation();
        }
    }

    public void retrievePageData(){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference pageReference = firebaseDatabase.getReference().child("NewsApi_v1").child("pages_v1").child("response");
        final ArrayList<NewsSpecial> pages = new ArrayList<>();

        pageReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                pages.add(dataSnapshot.getValue(NewsSpecial.class));

                if(pages.size() > newsSpecialPageIndex){
                    if(!pages.get(newsSpecialPageIndex).getCards() && pages.get(newsSpecialPageIndex).getBlocks().size() > 0){
                        newsSpecial = pages.get(newsSpecialPageIndex);
                        extractItemNAdd();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
