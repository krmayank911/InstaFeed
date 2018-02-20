package com.buggyarts.instafeedplus.customViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

/**
 * Created by mayank on 1/23/18
 */

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnail, share, bookmark;
    public TextView time, source, title, description, read_more, powered_by;

    public ArticleViewHolder(View itemView) {
        super(itemView);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        bookmark = itemView.findViewById(R.id.bookmark);
        share = itemView.findViewById(R.id.share);
        time = itemView.findViewById(R.id.time);
        source = itemView.findViewById(R.id.source);
        title = itemView.findViewById(R.id.title);
        description = itemView.findViewById(R.id.description);
        read_more = itemView.findViewById(R.id.read_more);
        powered_by = itemView.findViewById(R.id.powered_by);
    }

}
