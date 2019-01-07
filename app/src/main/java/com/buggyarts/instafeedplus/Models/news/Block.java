package com.buggyarts.instafeedplus.Models.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Block {

    @SerializedName("header")
    private String header;

    @SerializedName("id")
    private Integer id;

    @SerializedName("headerId")
    private Integer headerId;

    @SerializedName("callParams")
    private ArrayList<CallParam> callParams = null;

    @SerializedName("callResult")
    private ArrayList<NewsArticle> callResult = null;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Integer headerId) {
        this.headerId = headerId;
    }

    public ArrayList<CallParam> getCallParams() {
        return callParams;
    }

    public void setCallParams(ArrayList<CallParam> callParams) {
        this.callParams = callParams;
    }

    public ArrayList<NewsArticle> getCallResult() {
        return callResult;
    }

    public void setCallResult(ArrayList<NewsArticle> callResult) {
        this.callResult = callResult;
    }
}
