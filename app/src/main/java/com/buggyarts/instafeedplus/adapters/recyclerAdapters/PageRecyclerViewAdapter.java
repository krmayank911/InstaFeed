package com.buggyarts.instafeedplus.adapters.recyclerAdapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.instafeedplus.Models.Header;
import com.buggyarts.instafeedplus.Models.PageHeader;
import com.buggyarts.instafeedplus.Models.news.CallParam;
import com.buggyarts.instafeedplus.Models.news.HomeFeedRecyclerObject;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_HEADER;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_LOADER;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_MORE;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_MEDIUM;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_SHORT;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_PAGE_HEADER;

public class PageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


//    public ArrayList<Object> itemsList;

    public ArrayList<HomeFeedRecyclerObject> itemsList;
    public Context mContext;
    public int next = 0;

    public interface Callback{
        void getMoreData();
        void bookmarkAction(boolean save, NewsArticle article);
        void onArticleClick(NewsArticle article);
        void onMoreArticlesClick(CallParam params);
    }

    public Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public PageRecyclerViewAdapter(Context context, ArrayList<HomeFeedRecyclerObject> itemsList){
        this.mContext = context;
        this.itemsList = itemsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder objectVH = null;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view;
        switch (viewType){
            case FEED_TYPE_NA_MEDIUM:
                view = layoutInflater.inflate(R.layout.cell_medium_feed, parent, false);
                objectVH = new IFNewsFeedVH(view);
                break;

            case FEED_TYPE_NA_SHORT:
                view = layoutInflater.inflate(R.layout.cell_short_feed, parent, false);
                objectVH = new IFNewsFeedVH(view);
                break;

            case FEED_TYPE_LOADER:
                view = layoutInflater.inflate(R.layout.cell_bottom_loader, parent, false);
                objectVH = new LoaderVH(view);
                break;

            case FEED_TYPE_PAGE_HEADER:
                view = layoutInflater.inflate(R.layout.cell_special_header, parent, false);
                objectVH = new PageHeaderVH(view);
                break;

            case FEED_TYPE_HEADER:
                view = layoutInflater.inflate(R.layout.cell_header, parent, false);
                objectVH = new HeaderVH(view);
                break;

            case FEED_TYPE_MORE:
                view = layoutInflater.inflate(R.layout.cell_more, parent, false);
                objectVH = new MoreVH(view);
                break;
        }


        return objectVH;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){
            case FEED_TYPE_NA_MEDIUM:
                configArticleVH((IFNewsFeedVH) holder,(NewsArticle) itemsList.get(position).getObject(), position);
                break;
            case FEED_TYPE_NA_SHORT:
                configArticleVH((IFNewsFeedVH) holder,(NewsArticle) itemsList.get(position).getObject(),position);
                break;
            case FEED_TYPE_LOADER:
                LoaderVH loader = (LoaderVH) holder;
                break;
            case FEED_TYPE_PAGE_HEADER:
                configPageHeaderVH((PageHeaderVH) holder,(PageHeader) itemsList.get(position).getObject());
                break;
            case FEED_TYPE_HEADER:
                configHeaderVH((HeaderVH) holder,(Header) itemsList.get(position).getObject());
                break;
            case FEED_TYPE_MORE:

                configMoreVH((MoreVH) holder, (CallParam) itemsList.get(position).getObject());
//                configMoreVH((MoreVH) holder,null);
                break;
        }

//        if(reachedEndOfList(position)){
//            callback.getMoreData();
//        }

    }

    @Override
    public int getItemCount() {
        if(itemsList.size() > 0){

            if(next > 1){
                return itemsList.size() + 1;
            }else {
                return itemsList.size();
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        if(position == itemsList.size()){
            return FEED_TYPE_LOADER;
        }

        switch (itemsList.get(position).getType()){
            case FEED_TYPE_NA_MEDIUM:
                return FEED_TYPE_NA_MEDIUM;
            case FEED_TYPE_NA_SHORT:
                return FEED_TYPE_NA_SHORT;
            case FEED_TYPE_PAGE_HEADER:
                return FEED_TYPE_PAGE_HEADER;
            case FEED_TYPE_HEADER:
                return FEED_TYPE_HEADER;
            case FEED_TYPE_MORE:
                return FEED_TYPE_MORE;
        }

        return super.getItemViewType(position);
    }

    private boolean reachedEndOfList(int position) {
        return position == itemsList.size() - 2;
    }

    private void configArticleVH(final IFNewsFeedVH holder, final NewsArticle article, int position) {

        String meta = "";
        if (article.publishedAt != null) {
            meta = publishedTime(article.publishedAt);
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

        holder.metaInfo.setText(meta);
        holder.title.setText(Html.fromHtml(article.title));

        if(article.description == null){
            holder.description.setText(" ");
        }else if (article.description.equals(" ") || article.description.equals("null")) {
            holder.description.setText(" ");
        } else {
            holder.description.setText(Html.fromHtml(article.description));
        }

//        if(holder.getItemViewType() == TYPE_MEDIUM) {
//            holder.description.setVisibility(View.GONE);
//        }

        Glide.with(mContext).load(article.urlToImage).apply(new RequestOptions().centerCrop()).into(holder.thumbnail);

        holder.itemCard.setTag(R.string.card_item_object, article);
        holder.itemCard.setTag(R.string.card_item_holder, holder);
        holder.itemCard.setTag(R.string.card_item_position, position);
        holder.itemCard.setOnClickListener(this);

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
//                    DbUser dbUser = new DbUser(mContext, article.toString());
//                    dbUser.addArticleInDB();
                    callback.bookmarkAction(true,article);

                    Toast.makeText(mContext,"Added to bookmark",Toast.LENGTH_SHORT).show();

                } else {
                    article.setBookmarked(false);
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_border_pink_24dp);

//                    DbUser dbUser = new DbUser(mContext);
//                    dbUser.deleteArticleFromDB(article.toString());
                    callback.bookmarkAction(false,article);
                }
            }
        });

        holder.share.setTag(R.string.card_item_object,article);
        holder.share.setOnClickListener(this);
    }

    private void configPageHeaderVH(PageHeaderVH holder, PageHeader page){

        holder.title.setText(page.getPageTitle());
        holder.subText.setText(page.getPageSubText());
        holder.publishTime.setText(page.getPageDate());

        Glide.with(mContext).load("https://www.dailynews.com/wp-content/uploads/2017/09/img_3776.jpg").apply(new RequestOptions().centerCrop()).into(holder.thumbnail);
    }

    private void configMoreVH(MoreVH holder,CallParam params){

        holder.cellMore.setTag(R.string.more_articles_object,params);
        holder.cellMore.setOnClickListener(this);
        holder.more.setText("Read More");

    }

    private void configHeaderVH(HeaderVH holder, Header headerText){
        if(headerText.getHeaderText() != null) {
            holder.header.setText(headerText.getHeaderText());
        }
    }

    public class IFNewsFeedVH extends RecyclerView.ViewHolder{

        public RelativeLayout itemCard;
        public ImageView thumbnail;
        public ImageView  share;
        public ImageView  bookmark;
        public TextView metaInfo;
        public TextView title;
        public TextView description;
        public TextView read_more;
        public TextView powered_by;

        public IFNewsFeedVH(View itemView) {
            super(itemView);

            itemCard = itemView.findViewById(R.id.card_layout);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            bookmark = itemView.findViewById(R.id.bookmark);
            share = itemView.findViewById(R.id.share);
            metaInfo = itemView.findViewById(R.id.meta_info);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            read_more = itemView.findViewById(R.id.read_more);
            powered_by = itemView.findViewById(R.id.powered_by);
        }
    }

    public class PageHeaderVH extends RecyclerView.ViewHolder{

        TextView title;
        TextView subText;
        TextView publishTime;
        ImageView thumbnail;

        public PageHeaderVH(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_label);
            subText = itemView.findViewById(R.id.sub_text);
            publishTime = itemView.findViewById(R.id.published_at);
            thumbnail = itemView.findViewById(R.id.thumbnail);

        }
    }

    public class HeaderVH extends RecyclerView.ViewHolder{

        TextView header;
        public HeaderVH(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.listHeader);
        }
    }

    public class MoreVH extends RecyclerView.ViewHolder{

        LinearLayout cellMore;
        TextView more;

        public MoreVH(View itemView) {
            super(itemView);
            cellMore = itemView.findViewById(R.id.cell_more);
            more = itemView.findViewById(R.id.more);
        }
    }

    public class LoaderVH extends RecyclerView.ViewHolder{

        FrameLayout bottomLoader;

        public LoaderVH(View itemView) {
            super(itemView);

            bottomLoader = itemView.findViewById(R.id.bottom_loader);
        }
    }

    public String getFormattedTime(String unformated){

        String formatedTime = unformated;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        long publishedAt = Date.parse(unformated);


                String currentTime = format.format(calendar.getTime());

        String timeString = new SimpleDateFormat("d MMM, h:mm a").format(new Date(unformated));

        return formatedTime;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.card_layout){
            NewsArticle article = (NewsArticle) v.getTag(R.string.card_item_object);

            callback.onArticleClick(article);
        }else if(v.getId() == R.id.cell_more){
            CallParam param = (CallParam) v.getTag(R.string.more_articles_object);
            callback.onMoreArticlesClick(param);
        }else if(v.getId() == R.id.share){

            NewsArticle article = (NewsArticle) v.getTag(R.string.card_item_object);

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mContext.getPackageName()));
                mContext.startActivity(intent);
            }else {
                loadWithGlide(article.getUrlToImage(),article.getTitle());
            }
        }
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
            AppUtils.shareScreenShot(mContext,file,"");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWithGlide(String image, final String title){
        Glide.with(mContext).asBitmap().load(image)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        shareArticle(resource, title);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

    private void shareArticle(Bitmap bitmap, String title){
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
//            shareScreenShot(file);

            String message = title + "\n\n" + "Latest news feeds just 1 click away. Download InstaFeed+ " + "https://goo.gl/enVwXf";

            AppUtils.shareImageAction(mContext,file,message);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
