package com.buggyarts.instafeedplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
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
import com.buggyarts.instafeedplus.utils.Share;
import com.buggyarts.instafeedplus.utils.data.DbUser;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    public void onBindViewHolder(final FeedsRecyclerViewAdapter.VH holder, int position) {
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
        if (article.description.equals(" ") || article.description.equals("null")) {
            holder.description.setText(" ");
        } else {
            holder.description.setText(article.description);
        }
        Glide.with(context).load(article.thumbnail_url).asBitmap().centerCrop().into(holder.thumbnail);
        holder.share.setOnClickListener(takeSnapShotAndShare);

        if (!article.isBookmarked()) {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_border_pink_24dp);
        } else {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_pink_24dp);
        }

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!article.isBookmarked()) {
                    article.setBookmarked(true);
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_pink_24dp);
//                    Log.d("BookMark", "JsonString" + article.toString());
                    DbUser dbUser = new DbUser(context, article.toString());
                    dbUser.addArticleInDB();
                } else {
                    article.setBookmarked(false);
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_border_pink_24dp);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (feeds == null) {
            return 0;
        }
        return feeds.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView thumbnail, share, bookmark;
        TextView time, source, title, description, read_more, powered_by;

        public VH(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            share = itemView.findViewById(R.id.share);
            bookmark = itemView.findViewById(R.id.bookmark);
            time = itemView.findViewById(R.id.time);
            source = itemView.findViewById(R.id.source);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            read_more = itemView.findViewById(R.id.read_more);
            powered_by = itemView.findViewById(R.id.powered_by);
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

    View.OnClickListener takeSnapShotAndShare = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takeScreenShot(v);
        }
    };

    private void takeScreenShot(View v) {
        View view = v.getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        String fileName = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + ".jpg";

        File file = new File(filePath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            shareScreenShot(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareScreenShot(File imageFile) {
        Uri uri = Uri.fromFile(imageFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TEXT, "Latest news feeds just 1 click away. Download InstaFeed+ " + "https://goo.gl/enVwXf");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    //    View.OnClickListener takeSnapShotAndShare = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Share shareItem = new Share(v);
//            String image_path = shareItem.shareScreenShot();
//
//            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//            intent.setData(Uri.parse(image_path));
//            intent.setAction("android.intent.action.SEND");
//            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(image_path));
//            intent.putExtra(Intent.EXTRA_TEXT, "Latest news feeds just 1 click away. Download InstaFeed+ " + "https://goo.gl/enVwXf");
//            if (intent.resolveActivity(context.getPackageManager()) != null) {
//                context.startActivity(intent);
//            }
//        }
//    };

}
