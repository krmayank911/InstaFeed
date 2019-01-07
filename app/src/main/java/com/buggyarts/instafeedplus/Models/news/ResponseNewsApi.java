package com.buggyarts.instafeedplus.Models.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseNewsApi {

    @SerializedName("status")
    String status;

    @SerializedName("totalResults")
    Integer totalResults;

    @SerializedName("articles")
    ArrayList<NewsArticle> newsArticles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public void setNewsArticles(ArrayList<NewsArticle> newsArticles) {
        this.newsArticles = newsArticles;
    }

    public ArrayList<NewsArticle> getNewsArticles() {
        return newsArticles;
    }
}
