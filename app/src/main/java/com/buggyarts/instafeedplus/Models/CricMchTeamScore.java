package com.buggyarts.instafeedplus.Models;

/**
 * Created by mayank on 1/20/18
 */

public class CricMchTeamScore {

    private String id, sName, desc, runs, Decl, FollowOn, ovrs, wkts;

    public CricMchTeamScore() {
    }

    public CricMchTeamScore(String id, String sName, String desc, String runs, String Decl, String FollowOn, String ovrs, String wkts) {
        this.id = id;
        this.sName = sName;
        this.desc = desc;
        this.runs = runs;
        this.Decl = Decl;
        this.FollowOn = FollowOn;
        this.ovrs = ovrs;
        this.wkts = wkts;
    }

    public CricMchTeamScore(String sName, String runs, String ovrs, String wkts) {
        this.sName = sName;
        this.runs = runs;
        this.ovrs = ovrs;
        this.wkts = wkts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getRuns() {
        return runs;
    }

    public void setRuns(String runs) {
        this.runs = runs;
    }

    public String getOvrs() {
        return ovrs;
    }

    public void setOvrs(String ovrs) {
        this.ovrs = ovrs;
    }

    public String getWkts() {
        return wkts;
    }

    public void setWkts(String wkts) {
        this.wkts = wkts;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDecl() {
        return Decl;
    }

    public void setDecl(String decl) {
        Decl = decl;
    }

    public String getFollowOn() {
        return FollowOn;
    }

    public void setFollowOn(String followOn) {
        FollowOn = followOn;
    }
}
