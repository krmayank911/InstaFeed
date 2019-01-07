package com.buggyarts.instafeedplus.Models;

public class PageHeader {

    String pageTitle;

    String pageSubText;

    String pageDate;

    String pageThumbnailUrl;

    public PageHeader(String title, String subText, String date, String thumbnailUrl){
        this.pageTitle = title;
        this.pageSubText = subText;
        this.pageDate = date;
        this.pageThumbnailUrl = thumbnailUrl;
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

    public String getPageThumbnail() {
        return pageThumbnailUrl;
    }

    public void setPageThumbnail(String pageThumbnail) {
        this.pageThumbnailUrl = pageThumbnail;
    }
}
