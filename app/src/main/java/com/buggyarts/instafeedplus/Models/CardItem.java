package com.buggyarts.instafeedplus.Models;


public class CardItem {

    private String imageLink;
    private String lableText;

    private int mTextResource;
    private int mTitleResource;

    public CardItem(int title, int text) {
        mTitleResource = title;
        mTextResource = text;
    }

    public CardItem(String imageLink, String lableText){
        this.imageLink = imageLink;
        this.lableText = lableText;
    }

    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getLableText() {
        return lableText;
    }

    public void setLableText(String lableText) {
        this.lableText = lableText;
    }
}
