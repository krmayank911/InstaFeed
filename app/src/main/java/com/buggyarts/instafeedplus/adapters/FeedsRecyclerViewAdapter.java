package com.buggyarts.instafeedplus.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.customClasses.GlideApp;
import com.buggyarts.instafeedplus.utils.Article;
import com.buggyarts.instafeedplus.utils.data.DbUser;

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
        View feeds = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_short_feed, parent, false);
        return new FeedsRecyclerViewAdapter.VH(feeds);
    }

    @Override
    public void onBindViewHolder(final FeedsRecyclerViewAdapter.VH holder, int position) {
        final Article article = feeds.get(position);
        String meta = "";

        if (!article.time.equals("null")) {
            meta = publishedTime(article.time);
        }

        if(article.source != null) {
            meta = meta + " \u2022 " + Html.fromHtml(article.source);
        }

        holder.meta.setText(meta);
        holder.title.setText(Html.fromHtml(article.title));
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
            holder.description.setText(Html.fromHtml(article.description));
        }
        GlideApp.with(context).load(article.thumbnail_url).centerCrop().into(holder.thumbnail);
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
        TextView meta, title, description, read_more, powered_by;

        public VH(View itemView) {
            super(itemView);
            meta = itemView.findViewById(R.id.meta_info);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            share = itemView.findViewById(R.id.share);
            bookmark = itemView.findViewById(R.id.bookmark);
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
        int p_date = Integer.parseInt(string.substring(8, 10));
        int p_hr = Integer.parseInt(string.substring(11, 13));
        int p_min = Integer.parseInt(string.substring(14, 16));
        int c_date = Integer.parseInt(currentTime.substring(8, 10));
        int c_hr = Integer.parseInt(currentTime.substring(11, 13));
        int c_min = Integer.parseInt(currentTime.substring(14, 16));
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
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }else {
                takeScreenShot(v);
            }
        }
    };

    private void takeScreenShot(View v) {
        View view = v.getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        String fileName = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + ".jpg";

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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


}
