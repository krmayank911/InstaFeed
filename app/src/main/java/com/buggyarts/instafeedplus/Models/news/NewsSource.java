package com.buggyarts.instafeedplus.Models.news;

import com.google.gson.annotations.SerializedName;

public class NewsSource {

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" + "id:'" + id + '\'' + ", name:'" + name + '\'' + '}';
    }
}
