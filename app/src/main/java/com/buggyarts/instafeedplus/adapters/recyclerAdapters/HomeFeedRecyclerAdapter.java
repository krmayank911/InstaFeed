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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.instafeedplus.Models.CatListObject;
import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.Models.news.HomeFeedRecyclerObject;
import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.NewsSpecial;
import com.buggyarts.instafeedplus.Models.news.PagesResponse;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.activity.ArticleListActivity;
import com.buggyarts.instafeedplus.activity.NewsSpecialActivity;
import com.buggyarts.instafeedplus.customViews.HorizontalListerPagerView;
import com.buggyarts.instafeedplus.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_CAT;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_LOADER;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_MEDIUM;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_NA_SHORT;
import static com.buggyarts.instafeedplus.utils.Constants.FEED_TYPE_PAGER;

public class HomeFeedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, CategoriesAdapter.Callback, HorizontalListerPagerView.Callback {


//    public ArrayList<Object> itemsList;

    public ArrayList<HomeFeedRecyclerObject> itemsList;
    public Context mContext;
    public int next = 0;

    public interface Callback{
        void getMoreData();
        void onCategoryClick(Category category);
        void bookmarkAction(boolean save, NewsArticle article);
        void onArticleClick(NewsArticle article);
    }

    public Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public HomeFeedRecyclerAdapter(Context context, ArrayList<HomeFeedRecyclerObject> itemsList){
        this.mContext = context;
        this.itemsList = itemsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder objectVH = null;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        switch (viewType){
            case FEED_TYPE_NA_MEDIUM:
                View v1 = layoutInflater.inflate(R.layout.cell_medium_feed, parent, false);
                objectVH = new IFNewsFeedVH(v1);
                break;

            case FEED_TYPE_NA_SHORT:
                View v2 = layoutInflater.inflate(R.layout.cell_short_feed, parent, false);
                objectVH = new IFNewsFeedVH(v2);
                break;

            case FEED_TYPE_CAT:
                View v3 = layoutInflater.inflate(R.layout.cell_category_list, parent, false);
                objectVH = new CatListVH(v3);
                break;

            case FEED_TYPE_LOADER:
                View v4 = layoutInflater.inflate(R.layout.cell_bottom_loader, parent, false);
                objectVH = new LoaderVH(v4);
                break;

            case FEED_TYPE_PAGER:
                View v5 = layoutInflater.inflate(R.layout.cell_view_pager, parent, false);
                objectVH = new PagerVH(v5);
                break;
        }


        return objectVH;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){
            case FEED_TYPE_NA_MEDIUM:
                configArticleVH((IFNewsFeedVH) holder,(NewsArticle) itemsList.get(position).getObject(), position, true);
                break;
            case FEED_TYPE_NA_SHORT:
                configArticleVH((IFNewsFeedVH) holder,(NewsArticle) itemsList.get(position).getObject(),position, false);
                break;
            case FEED_TYPE_CAT:
                configCatListVH((CatListVH) holder,(CatListObject) itemsList.get(position).getObject());
                break;
            case FEED_TYPE_PAGER:
                configPageHeaderVH((PagerVH) holder, (PagesResponse) itemsList.get(position).getObject());
                break;
            case FEED_TYPE_LOADER:
                LoaderVH loader = (LoaderVH) holder;
                break;
        }

        if(reachedEndOfList(position)){
            callback.getMoreData();
        }

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
            case FEED_TYPE_CAT:
                return FEED_TYPE_CAT;
            case FEED_TYPE_PAGER:
                return FEED_TYPE_PAGER;
        }

        return super.getItemViewType(position);
    }

    private boolean reachedEndOfList(int position) {
        return position == itemsList.size() - 2;
    }

    private void configArticleVH(final IFNewsFeedVH holder, final NewsArticle article, int position, boolean mediumSize) {

        String meta = "";
        if (article.publishedAt != null) {
            String date =  AppUtils.getFormattedDate(article.publishedAt);
            if(date != null){
                meta = meta + date;
            }else {
                meta = publishedTime(article.publishedAt);
            }
        }

        if(article.getNewsSource().getName() != null) {
            if(meta.length() != 0) {
                meta = meta + " \u2022 " + Html.fromHtml(article.getNewsSource().getName());
            }else {
                meta = meta + Html.fromHtml(article.getNewsSource().getName());
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

        if(mediumSize) {

            Glide.with(mContext)
                    .load(article.thumbnail_url)
                    .apply(new RequestOptions()
                    .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder_landscape))
                    .centerCrop())
                    .into(holder.thumbnail);
        }else {

            Glide.with(mContext)
                    .load(article.thumbnail_url)
                    .apply( new RequestOptions()
                    .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder_square))
                    .centerCrop())
                    .into(holder.thumbnail);
        }

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
                    callback.bookmarkAction(true,article);

                    Toast.makeText(mContext,"Added to bookmark",Toast.LENGTH_SHORT).show();

                } else {
                    article.setBookmarked(false);
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_border_pink_24dp);
                    callback.bookmarkAction(false,article);
                }
            }
        });

        holder.share.setTag(R.string.card_item_object, article);
        holder.share.setOnClickListener(this);
    }

    private void configCatListVH(CatListVH holder, CatListObject catListObject){

        holder.header.setText("Categories");

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
        holder.catListRV.setLayoutManager(manager);

        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(mContext,catListObject.getCategories(),this);
        holder.catListRV.setAdapter(categoriesAdapter);

    }

    private void configPageHeaderVH(PagerVH holder, PagesResponse response){

        holder.listPagerView.setupData(response.getPages());
        holder.listPagerView.setCallback(this);

//        GlideApp.with(mContext).load("https://www.dailynews.com/wp-content/uploads/2017/09/img_3776.jpg").centerCrop().into(holder.thumbnail);

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

    public class CatListVH extends RecyclerView.ViewHolder{

        TextView header;
        RecyclerView catListRV;

        public CatListVH(View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.listHeader);
            catListRV = itemView.findViewById(R.id.catListRV);
        }
    }

    public class PagerVH extends RecyclerView.ViewHolder{

        HorizontalListerPagerView listPagerView;

        public PagerVH(View itemView) {
            super(itemView);

            listPagerView = itemView.findViewById(R.id.hl_view_pager);

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
    public void onCategoryClick(Category category) {
        callback.onCategoryClick(category);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.card_layout){
            NewsArticle article = (NewsArticle) v.getTag(R.string.card_item_object);
            callback.onArticleClick(article);
        }else if(v.getId() == R.id.share){

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mContext.getPackageName()));
                mContext.startActivity(intent);

//                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }else {
                NewsArticle article = (NewsArticle) v.getTag(R.string.card_item_object);
                loadWithGlide(article.getThumbnail_url(),article.getTitle());
            }
        }
    }


    @Override
    public void onPageCellClick(NewsSpecial newsSpecial) {

        Gson gson = new Gson();
        String responseJson = gson.toJson(newsSpecial);

        if(newsSpecial.getCards()){

            Intent intent = new Intent(mContext, ArticleListActivity.class);
            intent.putExtra(mContext.getResources().getString(R.string.news_special_json), responseJson);
            mContext.startActivity(intent);

        }else {
            Intent intent = new Intent(mContext, NewsSpecialActivity.class);
            intent.putExtra(mContext.getResources().getString(R.string.news_special_json), responseJson);
            mContext.startActivity(intent);
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
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
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

            String message = title + "\n\n" + "Latest news feeds just 1 click away. Download InstaFeed+ " + "https://goo.gl/enVwXf";

            AppUtils.shareImageAction(mContext,file,message);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
