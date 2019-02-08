package com.buggyarts.instafeedplus.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class VerticalPagerAdapter extends PagerAdapter implements View.OnClickListener {

    Context mContext;
    LayoutInflater mLayoutInflater;
    Callback callback;

    public ArrayList<NewsArticle> articles;

    public VerticalPagerAdapter(Context context, ArrayList<NewsArticle> articles) {
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.articles = articles;
    }

    public interface Callback{
        void getMoreData();
        void bookmarkAction(boolean save, NewsArticle article);
        void onArticleClick(NewsArticle article);
        void onArticleShareClick(NewsArticle article);
        void onFocusClick();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public int getCount() {

        if(articles.size() > 0) {
            return articles.size();
        }

        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.feeds_view_big, container, false);
        configArticleVH(itemView, articles.get(position),position);
        container.addView(itemView);

        if(reachedEndOfList(position)){
            callback.getMoreData();
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private boolean reachedEndOfList(int position) {
        return position == articles.size() - 2;
    }

    private void configArticleVH(final View holder, final NewsArticle article, int position) {

        RelativeLayout itemCard;
        ImageView thumbnail;
        ImageView  share;
        final ImageView  bookmark;
        TextView metaInfo;
        TextView title;
        TextView description;
        TextView read_more;
        TextView powered_by;
        final AdView adView;

        itemCard = holder.findViewById(R.id.card_view);
        thumbnail = holder.findViewById(R.id.thumbnail);
        bookmark = holder.findViewById(R.id.bookmark);
        share = holder.findViewById(R.id.share);
        metaInfo = holder.findViewById(R.id.meta_info);
        title = holder.findViewById(R.id.title);
        description = holder.findViewById(R.id.description);
        read_more = holder.findViewById(R.id.read_more);
        powered_by = holder.findViewById(R.id.powered_by);
        adView = holder.findViewById(R.id.banner_adView);

        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.setVisibility(View.GONE);
            }
        });

        String meta = "";
        if (article.publishedAt != null) {
            String date =  AppUtils.getFormattedDate(article.publishedAt);
            if(date != null){
                meta = meta + date;
            }else {
                meta = publishedTime(article.publishedAt);
            }
        }

        if(article.getNewsSource()!=null) {
            if (article.getNewsSource().getName() != null) {
                if (meta.length() != 0) {
                    meta = meta + " \u2022 " + Html.fromHtml(article.getNewsSource().getName());
                } else {
                    meta = meta + Html.fromHtml(article.getNewsSource().getName());
                }
            }
        }

        metaInfo.setText(meta);
        title.setText(Html.fromHtml(article.title));

        if(article.description == null){
            description.setText(" ");
        }else if (article.description.equals(" ") || article.description.equals("null")) {
            description.setText(" ");
        } else {
            description.setText(Html.fromHtml(article.description));
        }

//        if(getItemViewType() == TYPE_MEDIUM) {
//            description.setVisibility(View.GONE);
//        }

        Glide.with(mContext)
                .load(article.urlToImage).apply(new RequestOptions()
                .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder_landscape))
                .centerCrop())
                .into(thumbnail);

        itemCard.setTag(R.string.card_item_object, article);
        itemCard.setTag(R.string.card_item_holder, holder);
        itemCard.setTag(R.string.card_item_position, position);

        read_more.setTag(R.string.card_item_object,article);
        title.setTag(R.string.card_item_object,article);
        thumbnail.setTag(R.string.card_item_object,article);
        read_more.setOnClickListener(this);
        title.setOnClickListener(this);
        thumbnail.setOnClickListener(this);
        itemCard.setOnClickListener(this);

        if (!article.isBookmarked()) {
            bookmark.setImageResource(R.drawable.ic_bookmark_border_pink_24dp);
        } else {
            bookmark.setImageResource(R.drawable.ic_bookmark_pink_24dp);
        }

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!article.isBookmarked()) {
                    article.setBookmarked(true);
                    bookmark.setImageResource(R.drawable.ic_bookmark_pink_24dp);

                    callback.bookmarkAction(true,article);

                    Toast.makeText(mContext,"Added to bookmark",Toast.LENGTH_SHORT).show();

                } else {
                    article.setBookmarked(false);
                    bookmark.setImageResource(R.drawable.ic_bookmark_border_pink_24dp);

//                    DbUser dbUser = new DbUser(mContext);
//                    dbUser.deleteArticleFromDB(article.toString());
                    callback.bookmarkAction(false,article);
                }
            }
        });

        share.setTag(R.string.card_item_object,article);
        share.setOnClickListener(this);
    }


    public String publishedTime(String string) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTime = format.format(calendar.getTime());
        String timestamp = "";
        try {
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
        }catch (Exception e){

        }

        return timestamp;
    }

    int compairValues(int p, int c) {

        if (p < c) {
            return c - p;
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.read_more || v.getId() == R.id.title || v.getId() == R.id.thumbnail){
            NewsArticle article = (NewsArticle) v.getTag(R.string.card_item_object);
            callback.onArticleClick(article);
        }else if(v.getId() == R.id.share){
            NewsArticle article = (NewsArticle) v.getTag(R.string.card_item_object);
            callback.onArticleShareClick(article);
        }else if(v.getId() == R.id.card_view){
            callback.onFocusClick();
        }
    }

}
