package com.buggyarts.instafeedplus.Models.news;


import com.google.gson.annotations.SerializedName;

public class NewsArticle {

    @SerializedName("source")
    NewsSource newsSource;

    @SerializedName("author")
    String author;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("url")
    public String url;

    @SerializedName("urlToImage")
    public String thumbnail_url;

    @SerializedName("publishedAt")
    public String publishedAt;

    @SerializedName("content")
    public String content;

    public String time;

//    public String source;

    public String timeFormat = "GoogleTimeFormat";

    private boolean isBookmarked = false;

    private boolean isTimeAvailable = false;

    public NewsArticle(){}

    public NewsArticle(String time, String source, String title, String description, String thumbnail_url, String url) {
        this.time = time;
//        this.source = source;
        this.title = title;
        this.description = description;
        this.thumbnail_url = thumbnail_url;
        this.url = url;
    }

    public NewsArticle(String time, String source, String title, String description, String thumbnail_url, String url,String timeFormat,Boolean isTimeAvailable) {
        this.time = time;
//        this.source = source;
        this.title = title;
        this.description = description;
        this.thumbnail_url = thumbnail_url;
        this.url = url;
        this.isTimeAvailable = isTimeAvailable;
        this.timeFormat = timeFormat;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public NewsSource getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(NewsSource newsSource) {
        this.newsSource = newsSource;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

//    public String getSource() {
//        return source;
//    }

//    public void setSource(String source) {
//        this.source = source;
//    }

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

    public boolean isTimeAvailable() {
        return isTimeAvailable;
    }

    public void setTimeAvailable(boolean timeAvailable) {
        isTimeAvailable = timeAvailable;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

//    @Override
//    public String toString() {
//
//        return "{" +
//                "source:" + newsSource.toString() +
//                ", author:'" + author + '\'' +
//                ", title:'" + title + '\'' +
//                ", description:'" + description + '\'' +
//                ", url:'" + url + '\'' +
//                ", urlToImage:'" + thumbnail_url + '\'' +
//                ", publishedAt:'" + publishedAt + '\'' +
//                ", content:'" + content + '\'' +
//                ", time:'" + time + '\'' +
//                ", timeFormat:'" + timeFormat + '\'' +
//                ", isBookmarked:" + isBookmarked +
//                ", isTimeAvailable:" + isTimeAvailable +
//                '}';
//    }
}