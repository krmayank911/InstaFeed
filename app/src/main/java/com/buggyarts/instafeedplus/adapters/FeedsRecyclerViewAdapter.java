package com.buggyarts.instafeedplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.utils.Article;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mayank on 1/6/18
 */

public class FeedsRecyclerViewAdapter extends RecyclerView.Adapter<FeedsRecyclerViewAdapter.VH> {

    ArrayList<Article> feeds;
    Context context;

    public FeedsRecyclerViewAdapter(ArrayList<Article> feeds, Context context) {
        this.feeds = feeds;
        this.context = context;
    }

    @Override
    public FeedsRecyclerViewAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View feeds = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_view_short, parent, false);
        return new FeedsRecyclerViewAdapter.VH(feeds);
    }

    @Override
    public void onBindViewHolder(FeedsRecyclerViewAdapter.VH holder, int position) {
        final Article article = feeds.get(position);
        String published = "";

        if (!article.time.equals("null")) {
            published = publishedTime(article.time).concat(" - ");
        }
        String text = published;
        holder.time.setText(text);
        holder.source.setText(article.source);
        holder.title.setText(article.title);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("visit", article.url);
                context.startActivity(intent);
            }
        });
        holder.read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("visit", article.url);
                context.startActivity(intent);
            }
        });
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("visit", article.url);
                context.startActivity(intent);
            }
        });
        holder.description.setText(article.description);
        Glide.with(context).load(article.thumbnail_url).asBitmap().centerCrop().into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        if (feeds == null) {
            return 0;
        }
        return feeds.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView time, source, title, description, read_more;

        public VH(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            time = itemView.findViewById(R.id.time);
            source = itemView.findViewById(R.id.source);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            read_more = itemView.findViewById(R.id.read_more);

        }
    }

    public String publishedTime(String string) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTime = format.format(calendar.getTime());

        String timestamp = null;

//        int p_year = Integer.parseInt(string.substring(0,3));
//        int p_month = Integer.parseInt(string.substring(8,9));
//        int p_sec = Integer.parseInt(string.substring(15,16));
        int p_date = Integer.parseInt(string.substring(8, 10));
        int p_hr = Integer.parseInt(string.substring(11, 13));
        int p_min = Integer.parseInt(string.substring(14, 16));
        int c_date = Integer.parseInt(currentTime.substring(8, 10));
        int c_hr = Integer.parseInt(currentTime.substring(11, 13));
        int c_min = Integer.parseInt(currentTime.substring(14, 16));

//        int c_year = Integer.parseInt(currentTime.substring(0,3));
//        int c_month = Integer.parseInt(currentTime.substring(8,9));
//        int c_sec = Integer.parseInt(currentTime.substring(15,16));

        int dateDiff = compairValues(p_date, c_date);
        if (dateDiff > 1) {
            timestamp = " " + dateDiff + " days ago..";
        } else if (dateDiff == 1) {
            timestamp = " Yesterday";
        } else if (dateDiff == 0) {
            int hourDiff = compairValues(p_hr, c_hr);
//            Log.d("C_Date",currentTime);
//            Log.d("P_Date",string);
//            Log.d("HourDiff", "" +p_hr+" - "+c_hr + " = "+ hourDiff);
            timestamp = hourDiff + " hours ago";
            if (hourDiff == 0) {
                int minDiff = compairValues(p_min, c_min);
                timestamp = minDiff + " mins ago";
                if (minDiff <= 1) {
                    timestamp = "moments ago";
                }
            }
            if (hourDiff == 1) {
                timestamp = hourDiff + " hour ago";
            }
        }

        return timestamp;
    }

    int compairValues(int p, int c) {

        if (p < c) {
            return c - p;
        }
        return 0;
    }

}
