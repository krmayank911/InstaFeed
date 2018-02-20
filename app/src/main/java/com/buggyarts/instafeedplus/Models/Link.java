package com.buggyarts.instafeedplus.Models;

/**
 * Created by mayank on 2/19/18
 */

public class Link {

    private String link_title;
    private String link_url;

    public Link() {
    }

    public Link(String link_title, String link_url) {
        this.link_title = link_title;
        this.link_url = link_url;
    }

    public String getLink_title() {
        return link_title;
    }

    public void setLink_title(String link_title) {
        this.link_title = link_title;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }
}
