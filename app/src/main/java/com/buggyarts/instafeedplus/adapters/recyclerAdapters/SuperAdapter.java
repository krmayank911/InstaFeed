package com.buggyarts.instafeedplus.adapters.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.SectionCategory;
import com.buggyarts.instafeedplus.Models.news.SectionNewFeed;
import com.buggyarts.instafeedplus.R;

import java.util.ArrayList;

import static com.buggyarts.instafeedplus.utils.Constants.ITEM_TYPE_CATEGORY_LIST;
import static com.buggyarts.instafeedplus.utils.Constants.ITEM_TYPE_NEWS_FEED_LIST;

public class SuperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseItemAdapter.Callback {

    public ArrayList<Object> itemList;
    public Context mContext;

    public SuperAdapter(Context context, ArrayList<Object> itemList){
        this.mContext = context;
        this.itemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case ITEM_TYPE_CATEGORY_LIST:
                view = LayoutInflater.from(mContext).inflate(R.layout.cell_category_list,parent,false);
                viewHolder = new IFCatListVH(view);
                break;
            case ITEM_TYPE_NEWS_FEED_LIST:
                view = LayoutInflater.from(mContext).inflate(R.layout.cell_feed_list,parent,false);
                viewHolder = new IFNewsListVH(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == ITEM_TYPE_CATEGORY_LIST){

            setupCatListView((IFCatListVH) holder,(SectionCategory) itemList.get(position));

        }else if(holder.getItemViewType() == ITEM_TYPE_NEWS_FEED_LIST) {

            setupFeedListView((IFNewsListVH) holder,(SectionNewFeed) itemList.get(position));

        }

    }

    @Override
    public int getItemCount() {
        if(itemList.size() > 0){
            return itemList.size();
        }
        return 0;
    }

    public class IFCatListVH extends RecyclerView.ViewHolder{
        TextView catHeader;
        RecyclerView catsRV;
        public IFCatListVH(View itemView) {
            super(itemView);

            catHeader = itemView.findViewById(R.id.listHeader);
            catsRV = itemView.findViewById(R.id.catListRV);

        }
    }

    public class IFNewsListVH extends RecyclerView.ViewHolder{

        TextView newsFeedsHeader;
        RecyclerView newsFeedRV;

        public IFNewsListVH(View itemView) {
            super(itemView);
            newsFeedsHeader = itemView.findViewById(R.id.listHeader);
            newsFeedRV = itemView.findViewById(R.id.feedListRV);
        }
    }

    public void setupFeedListView(IFNewsListVH ifNewsListVH, SectionNewFeed sectionNewFeed){

        ifNewsListVH.newsFeedsHeader.setText(sectionNewFeed.getSectionLabel());
        ifNewsListVH.newsFeedsHeader.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        ifNewsListVH.newsFeedRV.setLayoutManager(layoutManager);

        BaseItemAdapter itemAdapter = new BaseItemAdapter(mContext,sectionNewFeed.getNewsArticles());
        itemAdapter.setCallback(this);
        ifNewsListVH.newsFeedRV.swapAdapter(itemAdapter,true);

    }

    public void setupCatListView(IFCatListVH ifCatListVH, SectionCategory sectionCategory){

        ifCatListVH.catHeader.setText(sectionCategory.getLabel());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
        ifCatListVH.catsRV.setLayoutManager(layoutManager);

        BaseItemAdapter itemAdapter = new BaseItemAdapter(mContext,sectionCategory.getCategories());
        itemAdapter.setCallback(this);
        ifCatListVH.catsRV.swapAdapter(itemAdapter,true);
    }

    @Override
    public int getItemViewType(int position) {
        if(itemList.get(position) instanceof SectionNewFeed){
            return ITEM_TYPE_NEWS_FEED_LIST;
        }else if(itemList.get(position) instanceof SectionCategory){
            return ITEM_TYPE_CATEGORY_LIST;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onNewsCardClick(NewsArticle article) {
        Intent intent = new Intent(mContext, BrowserActivity.class);
        intent.putExtra("visit", article.url);
        mContext.startActivity(intent);
    }

    @Override
    public void onCatClick(Category category) {

    }

}
