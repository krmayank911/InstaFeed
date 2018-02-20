package com.buggyarts.instafeedplus.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mayank on 1/6/18
 */

public class Article implements Parcelable {
    public String time, source, title, description, thumbnail_url, url;
    private boolean isBookmarked = false;

    public Article(String time, String source, String title, String description, String thumbnail_url, String url) {
        this.time = time;
        this.source = source;
        this.title = title;
        this.description = description;
        this.thumbnail_url = thumbnail_url;
        this.url = url;
    }

    protected Article(Parcel in) {
        time = in.readString();
        source = in.readString();
        title = in.readString();
        description = in.readString();
        thumbnail_url = in.readString();
        url = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(time);
        parcel.writeString(source);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(thumbnail_url);
        parcel.writeString(url);
    }

    @Override
    public String toString() {

        String article = "{ \"source\": { \"id\": \"reuters\",\"name\": \""
                + getSource() + "\" }, \"author\": \"Yara Bayoumy\", \"title\": \""
                + getTitle() + "\",\"description\": \""
                + getDescription() + "\",\"url\": \""
                + getUrl() + "\",\"urlToImage\": \""
                + getThumbnail_url() + "\",\"publishedAt\": \""
                + getTime() + "\"}";

        return article;
    }
}
