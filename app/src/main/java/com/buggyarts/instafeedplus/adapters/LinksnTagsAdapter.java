package com.buggyarts.instafeedplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.Models.Link;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.customViewHolders.LinkAdapter;

import java.util.ArrayList;

/**
 * Created by mayank on 2/19/18
 */

public class LinksnTagsAdapter extends RecyclerView.Adapter<LinkAdapter> {

    ArrayList<Link> links;
    Context context;

    public LinksnTagsAdapter(Context context, ArrayList<Link> links) {
        this.context = context;
        this.links = links;
    }

    @Override
    public LinkAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.link_layout, parent, false);
        return new LinkAdapter(v);
    }

    @Override
    public void onBindViewHolder(LinkAdapter holder, int position) {
        final Link link = links.get(position);
        holder.link_title.setText(link.getLink_title());
        holder.link_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("visit", link.getLink_url());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (links == null) {
            return 0;
        }
        return links.size();
    }
}
