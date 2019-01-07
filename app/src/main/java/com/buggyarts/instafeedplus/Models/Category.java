package com.buggyarts.instafeedplus.Models;

/**
 * Created by mayank on 1/26/18
 */

public class Category {

    private String category;

    private String icon;

    public Category(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
