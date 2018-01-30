package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;


/**
 * Created by mayank on 1/26/18
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout category_layout;
    public ImageView category_icon;
    public TextView category_title;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        category_layout = itemView.findViewById(R.id.category_layout);
        category_icon = itemView.findViewById(R.id.category_icon);
        category_title = itemView.findViewById(R.id.category_title);
    }
}
