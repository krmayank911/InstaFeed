package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

/**
 * Created by mayank on 2/19/18
 */

public class LinkAdapter extends RecyclerView.ViewHolder {

    public TextView link_title;
    public RelativeLayout link_layout;

    public LinkAdapter(View itemView) {
        super(itemView);

        link_title = itemView.findViewById(R.id.link_title);
        link_layout = itemView.findViewById(R.id.link_layout);
    }
}
