package com.buggyarts.instafeedplus.Models;

/**
 * Created by mayank on 2/4/18
 */

public class StoriesModelOne {

    private Story one, two;

    public StoriesModelOne(Story one, Story two) {
        this.one = one;
        this.two = two;
    }

    public Story getOne() {
        return one;
    }

    public void setOne(Story one) {
        this.one = one;
    }

    public Story getTwo() {
        return two;
    }

    public void setTwo(Story two) {
        this.two = two;
    }

}
