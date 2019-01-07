package com.buggyarts.instafeedplus.Models.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PagesResponse {

    @SerializedName("response")
    ArrayList<NewsSpecial> pages;

    public PagesResponse(){}

    public ArrayList<NewsSpecial> getPages() {
        return pages;
    }

    public void setPages(ArrayList<NewsSpecial> pages) {
        this.pages = pages;
    }
}
