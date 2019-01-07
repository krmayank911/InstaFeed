package com.buggyarts.instafeedplus.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.StrictMode;

import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AppUtils {

    private static Gson gson = new Gson();
    private static String TAG = AppUtils.class.getSimpleName();

    private static void saveValue(String key, String value,
                                  Context context) {

        SharedPreferences sp = context.getSharedPreferences("if_app_preferences",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getValue(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences("if_app_preferences",
                Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static void saveBookmarks(ArrayList<NewsArticle> bookmarks, Context context) {
        Gson gson = new Gson();
        String jsonOptions = gson.toJson(bookmarks);
        saveValue("bookmarks", jsonOptions, context);
    }

    public static ArrayList<NewsArticle> getBookmarks(Context context) {

        String json = getValue("bookmarks", context);

        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<NewsArticle>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return null;
    }

    public static void addToBookmark(NewsArticle article, Context context){

        ArrayList<NewsArticle> bookmarks;
        bookmarks = getBookmarks(context);

        if(bookmarks != null){
            bookmarks.add(0, article);
        }else {
            bookmarks = new ArrayList<>();
            bookmarks.add(article);
        }

        saveBookmarks(bookmarks,context);

    }

    public static void removeBookmark(NewsArticle article, Context context){

        ArrayList<NewsArticle> bookmarks;
        bookmarks = getBookmarks(context);

        if(bookmarks != null){

            int index = containsBookmark(article,bookmarks);

            if(index != -1) {
                bookmarks.remove(index);
                saveBookmarks(bookmarks,context);
            }
        }

    }

    public static void clearAllBookmark(Context context){
        ArrayList<NewsArticle> bookmarks = new ArrayList<>();
        saveBookmarks(bookmarks,context);
    }

    public static int containsBookmark(NewsArticle article, ArrayList<NewsArticle> a){

        for (int i = 0; i < a.size(); i++) {
            if(a.get(i).getUrl().equals(article.getUrl())){
                return i;
            }
        }

        return -1;
    }

    public static void shareImageAction(Context context, File file, String message){
        if(context != null){

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Uri uri = Uri.fromFile(file);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
    }

    public static void shareScreenShot(Context mContext, File imageFile, String message) {

        if(mContext != null) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Uri uri = Uri.fromFile(imageFile);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
        }
    }

    public static String getFormattedDate(String date){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat myFormat = new SimpleDateFormat("hh:mm dd, MMM yy");

        try {

            String reformattedStr = myFormat.format(fromUser.parse(date));
            return reformattedStr;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getPageSize(int total, int reached){

        if(total > reached){

            int remaining = total - reached;

            if(remaining > 20) {
                return 20;
            }else if(remaining > 15){
                return 15;
            }else if(remaining > 10){
                return 10;
            }else if(remaining > 5){
                return 5;
            }else if(remaining > 4){
                return 4;
            }else if(remaining > 3){
                return 3;
            }else if(remaining > 2){
                return 2;
            }else if(remaining >= 1){
                return 1;
            }

        }else if(total == reached){
            return 0;
        }

        return 0;
    }

    public static void setClickCount(Context context,String count){
        saveValue("clickCount", count, context);
    }
    public static int getClickCount(Context context){

        String count = getValue("clickCount",context);
        if(count != null){
            return Integer.parseInt(count);
        }
        return 15;
    }

}
