package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.buggyarts.instafeedplus.R;

/**
 * Created by mayank on 2/19/18
 */

public class LinksnTagsViewHolder extends RecyclerView.ViewHolder {

    public RecyclerView links_n_tags_recyclerView;

    public LinksnTagsViewHolder(View itemView) {
        super(itemView);
        links_n_tags_recyclerView = itemView.findViewById(R.id.links_n_tags_recycler_view);
    }
}
