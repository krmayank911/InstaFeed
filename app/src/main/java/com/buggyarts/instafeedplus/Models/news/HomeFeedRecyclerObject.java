package com.buggyarts.instafeedplus.Models.news;


import java.util.ArrayList;

public class HomeFeedRecyclerObject {

    public int type;

    public Object object;

    public ArrayList<CallParam> objectArrayList;

    public HomeFeedRecyclerObject(int type, Object object){
        this.type = type;
        this.object = object;
    }

    public HomeFeedRecyclerObject(ArrayList<CallParam> objectList,int type){
        this.type = type;
        this.objectArrayList = objectList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ArrayList<CallParam> getObjectArrayList() {
        return objectArrayList;
    }

    public void setObjectArrayList(ArrayList<CallParam> objectArrayList) {
        this.objectArrayList = objectArrayList;
    }
}
