package com.buggyarts.instafeedplus.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.buggyarts.instafeedplus.Models.CricMchInnDetails;
import com.buggyarts.instafeedplus.Models.CricMchTeamScore;
import com.buggyarts.instafeedplus.Models.CricketMatch;
import com.buggyarts.instafeedplus.Models.CricketMatchState;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.adapters.CricMchAdapter;
import com.buggyarts.instafeedplus.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mayank on 1/20/18
 */

public class SportsFeed extends Fragment {

    Context context;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<CricketMatch> matches;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CricMchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sports_feed, container, false);
//        recyclerView = view.findViewById(R.id.scores_recyclerView);
//        layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
//        recyclerView.setLayoutManager(layoutManager);
//
//        adapter = new CricMchAdapter(matches,context);
//        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference().child("News").child("CricketNews");
//        matches = new ArrayList<>();

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                matches.clear();
//                String sourceString = dataSnapshot.toString().replace("DataSnapshot","");
////                Log.d("JSON",sourceString);
//                extractSportsFeeds(sourceString);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void extractSportsFeeds(String jsonResponse) {
        try {
            JSONObject responseObject = new JSONObject(jsonResponse);
            JSONArray mchs = responseObject.getJSONObject("value").getJSONObject("mchdata").getJSONArray("match");
            int i = 0;
            while (i < mchs.length()) {
                CricketMatch cricketMatch = new CricketMatch();
                JSONObject match = mchs.getJSONObject(i);
                JSONObject state = match.getJSONObject("state");


                CricketMatchState matchState = new CricketMatchState();
                matchState.setState(state.getString("@mchState"));
                matchState.setStatus(state.getString("@status"));


                CricMchInnDetails mchInnDetails = new CricMchInnDetails();
                CricMchTeamScore btnTeam = new CricMchTeamScore();
                CricMchTeamScore blngTeam = new CricMchTeamScore();

                if (!(matchState.getState().equals("Result") || matchState.getState().equals("nextlive") || matchState.getState().equals("preview"))) {

                    matchState.setTossWon(state.getString("@TW"));
                    matchState.setDecisn(state.getString("@decisn"));

                    JSONObject mScore = match.getJSONObject("mscr");
                    JSONObject inngDetail = mScore.getJSONObject("inngsdetail");
                    JSONObject btnTeamScore = mScore.getJSONObject("btTm");
                    JSONObject blngTeamScore = mScore.getJSONObject("blgTm");

                    mchInnDetails.setNoOfOvers(inngDetail.getString("@noofovers"));
                    mchInnDetails.setRrr(inngDetail.getString("@rrr"));
                    mchInnDetails.setCrr(inngDetail.getString("@crr"));
                    mchInnDetails.setCprtshp(inngDetail.getString("@cprtshp"));

                    btnTeam.setId(btnTeamScore.getString("@id"));
                    btnTeam.setsName(btnTeamScore.getString("@sName"));

                    btnTeam.setRuns(btnTeamScore.getJSONObject("Inngs").getString("@r"));
                    btnTeam.setOvrs(btnTeamScore.getJSONObject("Inngs").getString("@ovrs"));
                    btnTeam.setWkts(btnTeamScore.getJSONObject("Inngs").getString("@wkts"));

                    blngTeam.setId(blngTeamScore.getString("@id"));
                    blngTeam.setsName(blngTeamScore.getString("@sName"));
                    try {
                        blngTeam.setRuns(blngTeamScore.getJSONObject("Inngs").getString("@r"));
                        blngTeam.setOvrs(blngTeamScore.getJSONObject("Inngs").getString("@ovrs"));
                        blngTeam.setWkts(blngTeamScore.getJSONObject("Inngs").getString("@wkts"));
                    } catch (JSONException e) {
//                        e.printStackTrace();
                        blngTeam.setRuns(" ");
                        blngTeam.setOvrs(" ");
                        blngTeam.setWkts(" ");
                    }

                } else if (matchState.getState().equals("Result")) {
                    cricketMatch.setMom(match.getJSONObject("manofthematch").getJSONObject("mom").getString("@Name"));
                    mchInnDetails = null;
                    btnTeam = null;
                    blngTeam = null;
                } else {
                    mchInnDetails = null;
                    btnTeam = null;
                    blngTeam = null;
                }

                cricketMatch.setType(match.getString("@type"));
                try {
                    cricketMatch.setMchDesc(match.getString("@mchDesc"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    cricketMatch.setMchDesc(" ");
                }
                try {
                    cricketMatch.setGrnd(match.getString("@grnd"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    cricketMatch.setGrnd(" ");
                }
                try {
                    cricketMatch.setNmch(match.getString("@nmch"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    try {
                        cricketMatch.setNmch(match.getString("@mnum"));
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                        cricketMatch.setNmch(" ");
                    }
                }
                try {
                    cricketMatch.setInngCnt(match.getString("@inngCnt"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    cricketMatch.setInngCnt(" ");
                }
                try {
                    cricketMatch.setTme_Dt(match.getJSONObject("Tme").getString("@Dt"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    cricketMatch.setTme_Dt(" ");
                }

                cricketMatch.setMatchState(matchState);
                cricketMatch.setInnDetails(mchInnDetails);
                cricketMatch.setBtnTeam(btnTeam);
                cricketMatch.setBlgnTeam(blngTeam);


                matches.add(cricketMatch);


                i++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
