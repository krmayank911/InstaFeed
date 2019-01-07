package com.buggyarts.instafeedplus.Models;

import java.util.ArrayList;

public class CatListObject {

    ArrayList<Category> categories = new ArrayList<>();

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
