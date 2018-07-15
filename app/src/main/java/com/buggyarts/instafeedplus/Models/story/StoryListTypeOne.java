package com.buggyarts.instafeedplus.Models.story;

import com.buggyarts.instafeedplus.Models.StoryModelSI;

import java.util.ArrayList;

public class StoryListTypeOne {

    String StoryListTitle;

    ArrayList<Object> storylist;

    public ArrayList<Object> getStorylist() {
        return storylist;
    }

    public void setStorylist(ArrayList<Object> storylist) {
        this.storylist = storylist;
    }

    public String getStoryListTitle() {
        return StoryListTitle;
    }

    public void setStoryListTitle(String storyListTitle) {
        StoryListTitle = storyListTitle;
    }
}
