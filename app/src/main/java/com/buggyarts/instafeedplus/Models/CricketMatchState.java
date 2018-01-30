package com.buggyarts.instafeedplus.Models;

/**
 * Created by mayank on 1/20/18
 */

public class CricketMatchState {

    String state, status, tossWon, decisn, addnStatus, splStatus;

    public CricketMatchState() {
    }

    public CricketMatchState(String state, String status, String tossWon, String decisn, String addnStatus, String splStatus) {
        this.state = state;
        this.status = status;
        this.tossWon = tossWon;
        this.decisn = decisn;
        this.addnStatus = addnStatus;
        this.splStatus = splStatus;
    }

    public CricketMatchState(String state, String status, String tossWon, String decisn) {
        this.state = state;
        this.status = status;
        this.tossWon = tossWon;
        this.decisn = decisn;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDecisn() {
        return decisn;
    }

    public void setDecisn(String decisn) {
        this.decisn = decisn;
    }

    public String getTossWon() {
        return tossWon;
    }

    public void setTossWon(String tossWon) {
        this.tossWon = tossWon;
    }

    public String getAddnStatus() {
        return addnStatus;
    }

    public void setAddnStatus(String addnStatus) {
        this.addnStatus = addnStatus;
    }

    public String getSplStatus() {
        return splStatus;
    }

    public void setSplStatus(String splStatus) {
        this.splStatus = splStatus;
    }
}
