package com.buggyarts.instafeedplus.Models;

/**
 * Created by mayank on 1/20/18
 */

public class CricMchInnDetails {

    String noOfOvers, rrr, crr, cprtshp;

    public CricMchInnDetails() {
    }

    public CricMchInnDetails(String noOfOvers, String req_rr, String cur_rr, String cprtshp) {
        this.noOfOvers = noOfOvers;
        this.rrr = req_rr;
        this.crr = cur_rr;
        this.cprtshp = cprtshp;
    }

    public String getNoOfOvers() {
        return noOfOvers;
    }

    public void setNoOfOvers(String noOfOvers) {
        this.noOfOvers = noOfOvers;
    }

    public String getRrr() {
        return rrr;
    }

    public void setRrr(String rrr) {
        this.rrr = rrr;
    }

    public String getCrr() {
        return crr;
    }

    public void setCrr(String crr) {
        this.crr = crr;
    }

    public String getCprtshp() {
        return cprtshp;
    }

    public void setCprtshp(String cprtshp) {
        this.cprtshp = cprtshp;
    }
}
