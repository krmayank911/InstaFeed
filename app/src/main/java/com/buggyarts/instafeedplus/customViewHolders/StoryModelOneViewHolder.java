package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

/**
 * Created by mayank on 2/4/18
 */

public class StoryModelOneViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnail_1, thumbnail_2, share_1, share_2;
    public TextView title_1, category_1, title_2, category_2;
    public CardView cardView_1, cardView_2;

    public StoryModelOneViewHolder(View itemView) {
        super(itemView);

        thumbnail_1 = itemView.findViewById(R.id.story_thumbnail_1);
        thumbnail_2 = itemView.findViewById(R.id.story_thumbnail_2);

        title_1 = itemView.findViewById(R.id.story_title_1);
        category_1 = itemView.findViewById(R.id.story_category_1);
        cardView_1 = itemView.findViewById(R.id.story_card_1);

        title_2 = itemView.findViewById(R.id.story_title_2);
        category_2 = itemView.findViewById(R.id.story_category_2);
        cardView_2 = itemView.findViewById(R.id.story_card_2);
        share_1 = itemView.findViewById(R.id.share_1);
        share_2 = itemView.findViewById(R.id.share_2);
    }
}
