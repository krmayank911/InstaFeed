package com.buggyarts.instafeedplus.Models.news;

import com.google.gson.annotations.SerializedName;

public class CallParam {

    @SerializedName("q")
    private String q;

    @SerializedName("news_api_key")
    private String newsApiKey;

    @SerializedName("sortBy")
    private String sortBy;

    @SerializedName("language")
    private String language;

    @SerializedName("page")
    private Integer page;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("pageSize")
    private Integer pageSize;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getNewsApiKey() {
        return newsApiKey;
    }

    public void setNewsApiKey(String newsApiKey) {
        this.newsApiKey = newsApiKey;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
