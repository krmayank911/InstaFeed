package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.buggyarts.instafeedplus.R;

/**
 * Created by mayank on 1/23/18
 */

public class ScoreCardViewHolder extends RecyclerView.ViewHolder {

    public RecyclerView score_card_recyclerView;

    public ScoreCardViewHolder(View itemView) {
        super(itemView);
        score_card_recyclerView = itemView.findViewById(R.id.scores_recyclerView);
    }
}