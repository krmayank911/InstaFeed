package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

/**
 * Created by mayank on 1/27/18
 */

public class StoryCardViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnail, share;
    public TextView title, category;
    public CardView cardView;

    public StoryCardViewHolder(View itemView) {
        super(itemView);
        thumbnail = itemView.findViewById(R.id.story_thumbnail_1);
        share = itemView.findViewById(R.id.share_1);
        title = itemView.findViewById(R.id.story_title_1);
        category = itemView.findViewById(R.id.story_category_1);
        cardView = itemView.findViewById(R.id.story_card_1);
    }
}
