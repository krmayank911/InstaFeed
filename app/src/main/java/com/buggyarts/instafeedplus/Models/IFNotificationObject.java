package com.buggyarts.instafeedplus.Models;

import android.content.Context;

import java.util.Map;

public class IFNotificationObject {

    private String body;
    private String title;
    private String icon;
    private String largeIcon;
    private String dataUrl;
    private String type;
    private String typeIndex;
    private String channelID;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(String typeIndex) {
        this.typeIndex = typeIndex;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public IFNotificationObject(Map<String, String> notifMap, Context context) {
        this.body = notifMap.get("body");
        this.title = notifMap.get("title");
        this.icon = notifMap.get("icon");
        this.largeIcon = notifMap.get("largeIcon");
        this.type = notifMap.get("type");
        this.typeIndex = notifMap.get("typeIndex");
        this.dataUrl = notifMap.get("dataUrl");
        this.channelID =  context.getPackageName() + "." + notifMap.get("channelID");
    }

}
