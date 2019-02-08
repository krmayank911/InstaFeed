package com.buggyarts.instafeedplus.admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.NewsSpecial;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateArticleActivity extends AppCompatActivity implements View.OnClickListener, DatabaseReference.CompletionListener {

    public RelativeLayout itemCard;
    public ImageView thumbnail;
    public ImageView  share;
    public ImageView  bookmark;
    public TextView metaInfo;
    public TextView title;
    public TextView description;
    public TextView read_more;
    public TextView powered_by;

    public TextView createPage;
    public TextView btnPostArticle;

    NewsArticle article = new NewsArticle();
    NewsSpecial newsSpecial;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dBRef,childRef;

    Map <String, Object> map = new HashMap<String, Object>();

    ArrayList<NewsArticle> cardsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);

        itemCard = findViewById(R.id.card_layout);
        thumbnail = findViewById(R.id.thumbnail);
        bookmark = findViewById(R.id.bookmark);
        share = findViewById(R.id.share);
        metaInfo = findViewById(R.id.meta_info);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        read_more = findViewById(R.id.read_more);
        powered_by = findViewById(R.id.powered_by);

        createPage = findViewById(R.id.button_create_new_page);
        btnPostArticle = findViewById(R.id.button_post_new_article);

        createPage.setOnClickListener(this);
        btnPostArticle.setOnClickListener(this);

        if(getIntent().hasExtra(getResources().getString(R.string.article_object))){
            Gson gson = new Gson();

            article = gson.fromJson(getIntent().getStringExtra(getResources().getString(R.string.article_object)),NewsArticle.class);

            if(article != null){
                configArticleVH();
                setDbReference("");
            }

        }

    }

    void setDbReference (String referencePath){
        firebaseDatabase = FirebaseDatabase.getInstance();
        dBRef = firebaseDatabase.getReference().child("NewsApi_v1").child("pages_v1").child("response");

        childRef = firebaseDatabase.getReference().child("NewsApi_v1").child("pages_v1").child("response").child("3").child("cardsList");
        childRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                cardsList.add(dataSnapshot.getValue(NewsArticle.class));
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

    private void configArticleVH() {

        String meta = "";
        if (article.publishedAt != null) {
            String date =  AppUtils.getFormattedDate(article.publishedAt);
            if(date != null){
                meta = meta + date;
            }
        }

        if(article.getNewsSource().getName() != null) {
            if(meta.length() != 0) {
                meta = meta + " \u2022 " + Html.fromHtml(article.getNewsSource().getName());
            }else {
                meta = meta + Html.fromHtml(article.getNewsSource().getName());
            }
        }

        metaInfo.setText(meta);
        title.setText(Html.fromHtml(article.title));

        if(article.description == null){
            description.setText(" ");
        }else if (article.description.equals(" ") || article.description.equals("null")) {
            description.setText(" ");
        } else {
            description.setText(Html.fromHtml(article.description));
        }


        Glide.with(CreateArticleActivity.this)
                .load(article.urlToImage)
                .apply( new RequestOptions()
                        .placeholder(CreateArticleActivity.this.getResources().getDrawable(R.drawable.placeholder_square))
                        .centerCrop())
                .into(thumbnail);

    }

    private void setNewPage(){

        cardsList.add(0,article);
        newsSpecial = new NewsSpecial();
        newsSpecial.setCards(true);
        newsSpecial.setPageDate("19 Jan,2019");
        newsSpecial.setPageSubText("Election News");
        newsSpecial.setPageTitle("Lok Sabha 2019");
        newsSpecial.setPageThumbnailUrl("url");
        newsSpecial.setCardsList(cardsList);

        map.put("3", newsSpecial);
        dBRef.updateChildren(map,this);

    }



    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_create_new_page){


        }else if(view.getId() == R.id.button_post_new_article){
            setNewPage();
        }
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        Toast.makeText(this,"done",Toast.LENGTH_SHORT).show();
    }


}
