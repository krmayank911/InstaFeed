package com.buggyarts.instafeedplus.Models;

import com.google.gson.annotations.SerializedName;

public class AppVersion {

    @SerializedName("version")
    String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
