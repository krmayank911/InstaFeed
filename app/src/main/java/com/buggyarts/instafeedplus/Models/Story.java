package com.buggyarts.instafeedplus.Models;


/**
 * Created by mayank on 1/27/18
 */

public class Story {
    private String title, url, thumbnail_url, category;

    public Story(String title, String thumbnail_url, String url, String category) {
        this.title = title;
        this.thumbnail_url = thumbnail_url;
        this.url = url;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
