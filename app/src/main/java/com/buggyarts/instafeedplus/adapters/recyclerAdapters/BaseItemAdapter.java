package com.buggyarts.instafeedplus.adapters.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.customClasses.GlideApp;
import com.buggyarts.instafeedplus.customViews.CircularImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.buggyarts.instafeedplus.utils.Constants.BUSINESS;
import static com.buggyarts.instafeedplus.utils.Constants.ENTERTAINMENT;
import static com.buggyarts.instafeedplus.utils.Constants.GENERAL;
import static com.buggyarts.instafeedplus.utils.Constants.HEALTH;
import static com.buggyarts.instafeedplus.utils.Constants.ITEM_TYPE_CATEGORY;
import static com.buggyarts.instafeedplus.utils.Constants.ITEM_TYPE_NEWS_FEED;
import static com.buggyarts.instafeedplus.utils.Constants.SCIENCE;
import static com.buggyarts.instafeedplus.utils.Constants.SPORTS;
import static com.buggyarts.instafeedplus.utils.Constants.TECHNOLOGY;

public class BaseItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    public ArrayList<Object> baseItemList;
    private Context mContext;
    private Callback callback;

    public interface Callback{
        void onNewsCardClick(NewsArticle article);
        void onCatClick(Category category);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public BaseItemAdapter(Context context, ArrayList<Object> baseItemList){
        this.mContext = context;
        this.baseItemList = baseItemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case ITEM_TYPE_CATEGORY:
                view = LayoutInflater.from(mContext).inflate(R.layout.cell_category_item,parent,false);
                viewHolder = new IFCatVH(view);
                break;
            case ITEM_TYPE_NEWS_FEED:
                view = LayoutInflater.from(mContext).inflate(R.layout.cell_short_feed,parent,false);
                viewHolder = new IFNewsFeedVH(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == ITEM_TYPE_CATEGORY){

            setupCatView((IFCatVH) holder,(Category) baseItemList.get(position),position);

        }else if(holder.getItemViewType() == ITEM_TYPE_NEWS_FEED) {

            setupFeedView((IFNewsFeedVH) holder,(NewsArticle) baseItemList.get(position),position);

        }
    }

    @Override
    public int getItemCount() {
        if(baseItemList.size() > 0){
            return baseItemList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        if(baseItemList.get(position) instanceof NewsArticle){
            return ITEM_TYPE_NEWS_FEED;
        }else if(baseItemList.get(position) instanceof Category){
            return ITEM_TYPE_CATEGORY;
        }

        return super.getItemViewType(position);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.card_layout){
            NewsArticle article = (NewsArticle) v.getTag(R.string.card_item_object);
            callback.onNewsCardClick(article);

        }else if(v.getId() == R.id.cat_cell_layout){
            Category category = (Category) v.getTag(R.string.card_item_object);
            int position = (int) v.getTag(R.string.card_item_position);
            callback.onCatClick(category);
        }
    }

    public class IFCatVH extends RecyclerView.ViewHolder{

        RelativeLayout itemCell;
        CircularImageView imageView;
        TextView label;

        public IFCatVH(View itemView) {
            super(itemView);

            itemCell = itemView.findViewById(R.id.cat_cell_layout);
            imageView = itemView.findViewById(R.id.circularImageView);
            label = itemView.findViewById(R.id.cat_label);
        }
    }

    public class IFNewsFeedVH extends RecyclerView.ViewHolder{

        public RelativeLayout itemCard;
        public ImageView thumbnail;
        public ImageView  share;
        public ImageView  bookmark;
        public TextView metaInfo;
        public TextView title;
        public TextView description;
        public TextView read_more;
        public TextView powered_by;

        public IFNewsFeedVH(View itemView) {
            super(itemView);

            itemCard = itemView.findViewById(R.id.card_layout);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            bookmark = itemView.findViewById(R.id.bookmark);
            share = itemView.findViewById(R.id.share);
            metaInfo = itemView.findViewById(R.id.meta_info);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            read_more = itemView.findViewById(R.id.read_more);
            powered_by = itemView.findViewById(R.id.powered_by);
        }
    }

    public void setupCatView(IFCatVH catVH, Category category, int position){

        switch (category.getCategory()) {
            case BUSINESS:
                catVH.label.setText(BUSINESS);
                catVH.imageView.setImageResource(R.drawable.cat_business);
                break;
            case ENTERTAINMENT:
                catVH.label.setText(ENTERTAINMENT);
                catVH.imageView.setImageResource(R.drawable.cat_entertainment);
                break;
            case GENERAL:
                catVH.label.setText(GENERAL);
                catVH.imageView.setImageResource(R.drawable.cat_general);
                break;
            case HEALTH:
                catVH.label.setText(HEALTH);
                catVH.imageView.setImageResource(R.drawable.cat_health);
                break;
            case SCIENCE:
                catVH.label.setText(SCIENCE);
                catVH.imageView.setImageResource(R.drawable.cat_science);
                break;
            case SPORTS:
                catVH.label.setText(SPORTS);
                catVH.imageView.setImageResource(R.drawable.cat_sports);
                break;
            case TECHNOLOGY:
                catVH.label.setText(TECHNOLOGY);
                catVH.imageView.setImageResource(R.drawable.cat_technology);
                break;
        }

//        catVH.label.setText(category.getCategory());
        catVH.itemCell.setTag(R.string.card_item_object,category);
        catVH.itemCell.setTag(R.string.card_item_holder,catVH);
        catVH.itemCell.setTag(R.string.card_item_position,position);
        catVH.itemCell.setOnClickListener(this);
    }

    public void setupFeedView(IFNewsFeedVH holder, NewsArticle article, int position){

        String meta = "6 min ago";


        if(article.getNewsSource().getName() != null) {
            meta = meta + " \u2022 " + Html.fromHtml(article.getNewsSource().getName());
        }

        holder.metaInfo.setText(meta);

        holder.title.setText(Html.fromHtml(article.title));

        if (article.description.equals(" ") || article.description.equals("null")) {
            holder.description.setText(" ");
        } else {
            holder.description.setText(Html.fromHtml(article.description));
        }
        GlideApp.with(mContext).load(article.thumbnail_url).centerCrop().into(holder.thumbnail);

        if (!article.isBookmarked()) {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_border_pink_24dp);
        } else {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_pink_24dp);
        }

        holder.itemCard.setTag(R.string.card_item_object, article);
        holder.itemCard.setTag(R.string.card_item_holder, holder);
        holder.itemCard.setTag(R.string.card_item_position, position);
        holder.itemCard.setOnClickListener(this);
    }

}
