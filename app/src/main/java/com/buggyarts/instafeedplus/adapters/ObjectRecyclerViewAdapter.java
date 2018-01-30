package com.buggyarts.instafeedplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.FeedsActivity;
import com.buggyarts.instafeedplus.MainActivity;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.CricketMatch;
import com.buggyarts.instafeedplus.Models.ScoreCard;
import com.buggyarts.instafeedplus.Models.Story;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.customViewHolders.ArticleViewHolder;
import com.buggyarts.instafeedplus.customViewHolders.CategoryViewHolder;
import com.buggyarts.instafeedplus.customViewHolders.ObjectViewHolder;
import com.buggyarts.instafeedplus.customViewHolders.ScoreCardViewHolder;
import com.buggyarts.instafeedplus.customViewHolders.StoryCardViewHolder;
import com.buggyarts.instafeedplus.utils.Article;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.buggyarts.instafeedplus.utils.Constants.BUSINESS;
import static com.buggyarts.instafeedplus.utils.Constants.ENTERTAINMENT;
import static com.buggyarts.instafeedplus.utils.Constants.GENERAL;
import static com.buggyarts.instafeedplus.utils.Constants.HEALTH;
import static com.buggyarts.instafeedplus.utils.Constants.SCIENCE;
import static com.buggyarts.instafeedplus.utils.Constants.SPORTS;
import static com.buggyarts.instafeedplus.utils.Constants.TECHNOLOGY;

/**
 * Created by mayank on 1/23/18
 */

public class ObjectRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Object> object_array;
    Context context;
    final int ARTICLE = 0, SCORE_CARD = 1, CATEGORY = 2, STORY = 3;
    boolean extra_visible = false;

    public ObjectRecyclerViewAdapter(ArrayList<Object> object_array, Context context) {
        this.context = context;
        this.object_array = object_array;
    }

    @Override
    public int getItemViewType(int position) {

        if (object_array.get(position) instanceof Article) {
            return ARTICLE;
        } else if (object_array.get(position) instanceof ScoreCard) {
            return SCORE_CARD;
        } else if (object_array.get(position) instanceof Category) {
            return CATEGORY;
        } else if (object_array.get(position) instanceof Story) {
            return STORY;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder objectVH;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        switch (viewType) {
            case ARTICLE:
                View v1 = layoutInflater.inflate(R.layout.feed_view_short, parent, false);
                objectVH = new ArticleViewHolder(v1);
                break;
            case SCORE_CARD:
                View v2 = layoutInflater.inflate(R.layout.sports_feed, parent, false);
                objectVH = new ScoreCardViewHolder(v2);
                break;
            case CATEGORY:
                View v3 = layoutInflater.inflate(R.layout.category_item_option, parent, false);
                objectVH = new CategoryViewHolder(v3);
                break;
            case STORY:
                View v4 = layoutInflater.inflate(R.layout.story_card_short_model, parent, false);
                objectVH = new StoryCardViewHolder(v4);
                break;
            default:
                objectVH = null;
        }
        return objectVH;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ARTICLE:
                ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
                configArticleVH(articleViewHolder, position);
                break;
            case SCORE_CARD:
                ScoreCardViewHolder scoreCardViewHolder = (ScoreCardViewHolder) holder;
                configScoreCardVH(scoreCardViewHolder, position);
                break;
            case CATEGORY:
                CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
                configCategoryVH(categoryViewHolder, position);
                break;
            case STORY:
                StoryCardViewHolder storyCardViewHolder = (StoryCardViewHolder) holder;
                configStoryCardVH(storyCardViewHolder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (object_array == null) {
            return 0;
        }
        return object_array.size();
    }

    private void configArticleVH(ArticleViewHolder holder, int position) {
        final Article article = (Article) object_array.get(position);

        String published_at = "";

        if (!article.time.equals("null")) {
            published_at = publishedTime(article.time).concat(" - ");
        }

        holder.time.setText(published_at);
        holder.source.setText(article.source);
        holder.title.setText(article.title);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("visit", article.url);
                context.startActivity(intent);
            }
        });
        holder.read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("visit", article.url);
                context.startActivity(intent);
            }
        });
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("visit", article.url);
                context.startActivity(intent);
            }
        });
        if (article.description.equals(" ") || article.description.equals("null")) {
            holder.description.setText(" ");
        } else {
            holder.description.setText(article.description);
        }
        Glide.with(context).load(article.thumbnail_url).asBitmap().centerCrop().into(holder.thumbnail);
    }

    private void configScoreCardVH(ScoreCardViewHolder holder, int position) {
        ScoreCard scoreCard = (ScoreCard) object_array.get(position);

        ArrayList<CricketMatch> score_cards = scoreCard.getScore_cards();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.score_card_recyclerView.setLayoutManager(layoutManager);
        CricMchAdapter adapter = new CricMchAdapter(score_cards, context);
        holder.score_card_recyclerView.setAdapter(adapter);
    }

    private void configCategoryVH(CategoryViewHolder holder, final int position) {

        final Category category = (Category) object_array.get(position);

        switch (category.getCategory()) {
            case BUSINESS:
                holder.category_title.setText(BUSINESS);
                holder.category_icon.setImageResource(R.drawable.cat_business);
                break;
            case ENTERTAINMENT:
                holder.category_title.setText(ENTERTAINMENT);
                holder.category_icon.setImageResource(R.drawable.cat_entertainment);
                break;
            case GENERAL:
                holder.category_title.setText(GENERAL);
                holder.category_icon.setImageResource(R.drawable.cat_general);
                break;
            case HEALTH:
                holder.category_title.setText(HEALTH);
                holder.category_icon.setImageResource(R.drawable.cat_health);
                break;
            case SCIENCE:
                holder.category_title.setText(SCIENCE);
                holder.category_icon.setImageResource(R.drawable.cat_science);
                break;
            case SPORTS:
                holder.category_title.setText(SPORTS);
                holder.category_icon.setImageResource(R.drawable.cat_sports);
                break;
            case TECHNOLOGY:
                holder.category_title.setText(TECHNOLOGY);
                holder.category_icon.setImageResource(R.drawable.cat_technology);
                break;
        }

        holder.category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedsActivity.class);
                intent.putExtra("category", category.getCategory());
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });

    }

    private void configStoryCardVH(StoryCardViewHolder holder, int position) {

        final Story story = (Story) object_array.get(position);

        Glide.with(context).load("http://" + story.getThumbnail_url()).asBitmap().into(holder.thumbnail);
        holder.title.setText(story.getTitle());
        holder.category.setText(story.getCategory());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("visit", story.getUrl());
                context.startActivity(intent);
            }
        });
    }


    public String publishedTime(String string) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTime = format.format(calendar.getTime());

        String timestamp = null;

//        int p_year = Integer.parseInt(string.substring(0,3));
//        int p_month = Integer.parseInt(string.substring(8,9));
//        int p_sec = Integer.parseInt(string.substring(15,16));
        int p_date = Integer.parseInt(string.substring(8, 10));
        int p_hr = Integer.parseInt(string.substring(11, 13));
        int p_min = Integer.parseInt(string.substring(14, 16));
        int c_date = Integer.parseInt(currentTime.substring(8, 10));
        int c_hr = Integer.parseInt(currentTime.substring(11, 13));
        int c_min = Integer.parseInt(currentTime.substring(14, 16));

//        int c_year = Integer.parseInt(currentTime.substring(0,3));
//        int c_month = Integer.parseInt(currentTime.substring(8,9));
//        int c_sec = Integer.parseInt(currentTime.substring(15,16));

        int dateDiff = compairValues(p_date, c_date);
        if (dateDiff > 1) {
            timestamp = " " + dateDiff + " days ago..";
        } else if (dateDiff == 1) {
            timestamp = " Yesterday";
        } else if (dateDiff == 0) {
            int hourDiff = compairValues(p_hr, c_hr);
//            Log.d("C_Date",currentTime);
//            Log.d("P_Date",string);
//            Log.d("HourDiff", "" +p_hr+" - "+c_hr + " = "+ hourDiff);
            timestamp = hourDiff + " hours ago";
            if (hourDiff == 0) {
                int minDiff = compairValues(p_min, c_min);
                timestamp = minDiff + " mins ago";
                if (minDiff <= 1) {
                    timestamp = "moments ago";
                }
            }
            if (hourDiff == 1) {
                timestamp = hourDiff + " hour ago";
            }
        }

        return timestamp;
    }

    int compairValues(int p, int c) {

        if (p < c) {
            return c - p;
        }
        return 0;
    }

}
