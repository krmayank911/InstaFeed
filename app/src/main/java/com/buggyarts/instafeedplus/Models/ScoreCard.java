package com.buggyarts.instafeedplus.Models;

import java.util.ArrayList;

/**
 * Created by mayank on 1/23/18
 */

public class ScoreCard {

    private ArrayList<CricketMatch> score_cards;

    public ScoreCard(ArrayList<CricketMatch> score_cards) {
        this.score_cards = score_cards;
    }

    public ArrayList<CricketMatch> getScore_cards() {
        return score_cards;
    }

    public void setScore_cards(ArrayList<CricketMatch> score_cards) {
        this.score_cards = score_cards;
    }
}
