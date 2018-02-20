package com.buggyarts.instafeedplus.Models;

import java.util.ArrayList;

/**
 * Created by mayank on 2/19/18
 */

public class LinksnTagsList {

    private ArrayList<Link> links;

    public LinksnTagsList(ArrayList<Link> links) {
        this.links = links;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }
}
