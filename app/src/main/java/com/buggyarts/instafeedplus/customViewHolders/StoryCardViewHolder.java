package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

/**
 * Created by mayank on 1/27/18.
 */

public class StoryCardViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnail;
    public TextView title, category;
    public CardView cardView;

    public StoryCardViewHolder(View itemView) {
        super(itemView);
        thumbnail = itemView.findViewById(R.id.story_thumbnail);
        title = itemView.findViewById(R.id.story_title);
        category = itemView.findViewById(R.id.story_category);
        cardView = itemView.findViewById(R.id.story_card);
    }
}
