package com.buggyarts.instafeedplus.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buggyarts.instafeedplus.Models.story.StoryListTypeOne;
import com.buggyarts.instafeedplus.R;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.buggyarts.instafeedplus.utils.Constants.ITEM_TYPE_STORY_LIST;

public class WrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Object> arrayOfList;
    Context mContext;

    public WrapperAdapter(Context context, ArrayList<Object> list){
        this.arrayOfList = list;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_story_list,parent,false);
        return new StoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(arrayOfList.get(position) instanceof StoryListTypeOne){
            setStoryListTypeOneViews((StoryListViewHolder) holder, (StoryListTypeOne) arrayOfList.get(position),position);
        }
    }

    @Override
    public int getItemCount() {
        if(arrayOfList.size() > 0){
            return arrayOfList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        Object object = arrayOfList.get(position);

        if(object instanceof StoryListTypeOne){
            return ITEM_TYPE_STORY_LIST;
        }

        return super.getItemViewType(position);
    }

    class StoryListViewHolder extends RecyclerView.ViewHolder{

        public TextView listTitle;
        public RecyclerView listRecyclerView;

        public StoryListViewHolder(View itemView) {
            super(itemView);
            listTitle  = itemView.findViewById(R.id.listTitle);
            listRecyclerView = itemView.findViewById(R.id.storyListRecyclerView);
        }
    }

    public void setStoryListTypeOneViews(StoryListViewHolder holder,StoryListTypeOne listData, int position){
        holder.listTitle.setText(Html.fromHtml(listData.getStoryListTitle()));
        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.listRecyclerView.setLayoutManager(manager);
        ObjectRecyclerViewAdapter adapter = new ObjectRecyclerViewAdapter(listData.getStorylist(), mContext);
        holder.listRecyclerView.setAdapter(adapter);

//        OverScrollDecoratorHelper.setUpOverScroll(holder.listRecyclerView, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
    }
}
