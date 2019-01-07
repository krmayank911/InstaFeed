package com.buggyarts.instafeedplus.Models.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SectionNewFeed {

    @SerializedName("sectionLabel")
    String sectionLabel;

    @SerializedName("sectionSubLabel")
    String sectionSubLabel;

    @SerializedName("newsArticles")
    ArrayList newsArticles;

    public String getSectionLabel() {
        return sectionLabel;
    }

    public void setSectionLabel(String sectionLabel) {
        this.sectionLabel = sectionLabel;
    }

    public String getSectionSubLabel() {
        return sectionSubLabel;
    }

    public void setSectionSubLabel(String sectionSubLabel) {
        this.sectionSubLabel = sectionSubLabel;
    }

    public ArrayList getNewsArticles() {
        return newsArticles;
    }

    public void setNewsArticles(ArrayList newsArticles) {
        this.newsArticles = newsArticles;
    }
}
