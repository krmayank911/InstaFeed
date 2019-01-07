package com.buggyarts.instafeedplus.Models.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NewsSpecial {

    @SerializedName("pageTitle")
    private String pageTitle;

    @SerializedName("pageSubText")
    private String pageSubText;

    @SerializedName("pageDate")
    private String pageDate;

    @SerializedName("pageThumbnailUrl")
    private String pageThumbnailUrl;

    @SerializedName("blocks")
    private ArrayList<Block> blocks = null;

    @SerializedName("cards")
    private Boolean cards = false;

    @SerializedName("cardsList")
    private ArrayList<NewsArticle> cardsList = null;

    public ArrayList<NewsArticle> getCardsList() {
        return cardsList;
    }

    public void setCardsList(ArrayList<NewsArticle> cardsList) {
        this.cardsList = cardsList;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageSubText() {
        return pageSubText;
    }

    public void setPageSubText(String pageSubText) {
        this.pageSubText = pageSubText;
    }

    public String getPageDate() {
        return pageDate;
    }

    public void setPageDate(String pageDate) {
        this.pageDate = pageDate;
    }

    public String getPageThumbnailUrl() {
        return pageThumbnailUrl;
    }

    public void setPageThumbnailUrl(String pageThumbnailUrl) {
        this.pageThumbnailUrl = pageThumbnailUrl;
    }

    public Boolean getCards() {
        return cards;
    }

    public void setCards(Boolean cards) {
        this.cards = cards;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }
}
