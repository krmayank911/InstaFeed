package com.buggyarts.instafeedplus.Models.news;

import com.buggyarts.instafeedplus.utils.Source;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseSources {

    @SerializedName("status")
    String status;

    @SerializedName("sources")
    ArrayList<Source> sources;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Source> getSources() {
        return sources;
    }

    public void setSources(ArrayList<Source> sources) {
        this.sources = sources;
    }
}
