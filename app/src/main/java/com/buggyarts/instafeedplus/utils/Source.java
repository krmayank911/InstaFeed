package com.buggyarts.instafeedplus.utils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mayank on 1/7/18
 */

public class Source {


    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("category")
    public String category;

    @SerializedName("language")
    public String language;

    @SerializedName("country")
    public String country;

    @SerializedName("url")
    public String url;

    public Source(){}

    public Source(String id, String name, String category, String language, String country, String url) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.language = language;
        this.country = country;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
