package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

/**
 * Created by mayank on 2/10/18
 */

public class StoryCardSmallImageViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnail, share;
    public TextView title, category;
    public CardView cardView;

    public StoryCardSmallImageViewHolder(View itemView) {
        super(itemView);
        share = itemView.findViewById(R.id.share);
        thumbnail = itemView.findViewById(R.id.story_thumbnail);
        title = itemView.findViewById(R.id.story_title);
        category = itemView.findViewById(R.id.story_category);
        cardView = itemView.findViewById(R.id.story_card);
    }
}
