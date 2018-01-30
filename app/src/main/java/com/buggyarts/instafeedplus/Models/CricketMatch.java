package com.buggyarts.instafeedplus.Models;

/**
 * Created by mayank on 1/20/18
 */

public class CricketMatch {

    /**
     * srs - series
     * mch - match
     * Des - description
     * nmch - match number
     * vcity - visiting city
     * vcountry - visiting country
     * grnd - ground
     * inngCnt - inng Count
     */

    private String type, srs, mchDesc, nmch, vcity, vcountry, grnd, inngCnt;
    //    String TM_1_id,TM_2_id,TM_1_sName,TM_2_sName;
    private String tme_Dt, tme_stTme, tme_endDt;
    private String mom;

    private CricketMatchState matchState;
    private CricMchTeamScore btnTeam;
    private CricMchTeamScore blgnTeam;
    private CricMchInnDetails innDetails;

    public CricketMatch() {
    }

    public CricketMatch(String type, String srs, String mchDesc, String nmch, String vcity,
                        String vcountry, String grnd, String inngCnt, String tme_Dt, String tme_stTme,
                        String tme_endDt, CricketMatchState matchState, CricMchInnDetails innDetails,
                        CricMchTeamScore btnTeam, CricMchTeamScore blgnTeam) {

        this.type = type;
        this.srs = srs;
        this.mchDesc = mchDesc;
        this.nmch = nmch;
        this.vcity = vcity;
        this.vcountry = vcountry;
        this.grnd = grnd;
        this.inngCnt = inngCnt;
        this.tme_Dt = tme_Dt;
        this.tme_stTme = tme_stTme;
        this.tme_endDt = tme_endDt;
        this.matchState = matchState;
        this.innDetails = innDetails;
        this.btnTeam = btnTeam;
        this.blgnTeam = blgnTeam;
    }

    public CricketMatch(String type, String mchDesc, String nmch, String grnd, String inngCnt, String tme_Dt,
                        CricketMatchState matchState, CricMchInnDetails innDetails, CricMchTeamScore btnTeam,
                        CricMchTeamScore blgnTeam) {

        this.type = type;
        this.mchDesc = mchDesc;
        this.nmch = nmch;
        this.grnd = grnd;
        this.inngCnt = inngCnt;
        this.tme_Dt = tme_Dt;
        this.matchState = matchState;
        this.innDetails = innDetails;
        this.btnTeam = btnTeam;
        this.blgnTeam = blgnTeam;
    }

    public CricketMatchState getMatchState() {
        return matchState;
    }

    public void setMatchState(CricketMatchState matchState) {
        this.matchState = matchState;
    }

    public CricMchInnDetails getInnDetails() {
        return innDetails;
    }

    public void setInnDetails(CricMchInnDetails innDetails) {
        this.innDetails = innDetails;
    }

    public CricMchTeamScore getBtnTeam() {
        return btnTeam;
    }

    public void setBtnTeam(CricMchTeamScore btnTeam) {
        this.btnTeam = btnTeam;
    }

    public CricMchTeamScore getBlgnTeam() {
        return blgnTeam;
    }

    public void setBlgnTeam(CricMchTeamScore blgnTeam) {
        this.blgnTeam = blgnTeam;
    }

    public String getMom() {
        return mom;
    }

    public void setMom(String mom) {
        this.mom = mom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrs() {
        return srs;
    }

    public void setSrs(String srs) {
        this.srs = srs;
    }

    public String getGrnd() {
        return grnd;
    }

    public void setGrnd(String grnd) {
        this.grnd = grnd;
    }

    public String getNmch() {
        return nmch;
    }

    public void setNmch(String nmch) {
        this.nmch = nmch;
    }

    public String getInngCnt() {
        return inngCnt;
    }

    public void setInngCnt(String inngCnt) {
        this.inngCnt = inngCnt;
    }

    public String getMchDesc() {
        return mchDesc;
    }

    public void setMchDesc(String mchDesc) {
        this.mchDesc = mchDesc;
    }

    public String getTme_Dt() {
        return tme_Dt;
    }

    public void setTme_Dt(String tme_Dt) {
        this.tme_Dt = tme_Dt;
    }

    public String getTme_stTme() {
        return tme_stTme;
    }

    public void setTme_stTme(String tme_stTme) {
        this.tme_stTme = tme_stTme;
    }

    public String getTme_endDt() {
        return tme_endDt;
    }

    public void setTme_endDt(String tme_endDt) {
        this.tme_endDt = tme_endDt;
    }

    public String getVcity() {
        return vcity;
    }

    public void setVcity(String vcity) {
        this.vcity = vcity;
    }

    public String getVcountry() {
        return vcountry;
    }

    public void setVcountry(String vcountry) {
        this.vcountry = vcountry;
    }
}
