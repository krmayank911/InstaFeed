package com.buggyarts.instafeedplus.Models.news;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FBSection {

    @SerializedName("navBarLinks")
    ArrayList<NavBar> navbarLinks = new ArrayList();

    @SerializedName("articles")
    ArrayList<ArrayList<NewsArticle>>  articles = new ArrayList<>();

    public ArrayList<NavBar> getNavbarLinks() {
        return navbarLinks;
    }

    public void setNavbarLinks(ArrayList<NavBar> navbarLinks) {
        this.navbarLinks = navbarLinks;
    }

    public ArrayList<ArrayList<NewsArticle>> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<ArrayList<NewsArticle>> articles) {
        this.articles = articles;
    }

    class NavBar {
        String name;
        String link;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

}
