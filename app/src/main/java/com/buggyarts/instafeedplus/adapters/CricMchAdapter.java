package com.buggyarts.instafeedplus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.Models.CricketMatch;
import com.buggyarts.instafeedplus.R;

import java.util.ArrayList;

/**
 * Created by mayank on 1/20/18
 */

public class CricMchAdapter extends RecyclerView.Adapter<CricMchAdapter.VH> {

    ArrayList<CricketMatch> matches;
    Context context;
    boolean extra_visible = false;

    public CricMchAdapter(ArrayList<CricketMatch> matches, Context context) {
        this.matches = matches;
        this.context = context;
    }

    @Override
    public CricMchAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View match_card = LayoutInflater.from(context).inflate(R.layout.cric_mch_mini_model, parent, false);
        return new VH(match_card);
    }

    @Override
    public void onBindViewHolder(final CricMchAdapter.VH holder, int position) {
        CricketMatch match = matches.get(position);

        String heading = match.getTme_Dt() + " - " + match.getNmch() + " " + match.getMchDesc();
        String description = match.getMatchState().getStatus();
        String status = match.getMatchState().getState();
        String score_btn_team = "", score_blng_team = "", rrr = "", crr = "", partnership = "";

        if (!((status.equals("Result") || status.equals("nextlive")) || status.equals("preview"))) {
            score_btn_team = match.getBtnTeam().getsName() + " " + match.getBtnTeam().getRuns()
                    + "/" + match.getBtnTeam().getWkts() + " (" + match.getBtnTeam().getOvrs() + ")";

            String inngCnt = match.getInngCnt();
            if (inngCnt.equals("1")) {
                score_blng_team = match.getBlgnTeam().getsName() + "  Yet to bat";
                rrr = " ";
            } else {
                score_blng_team = match.getBlgnTeam().getsName() + " " + match.getBlgnTeam().getRuns()
                        + "/" + match.getBlgnTeam().getWkts() + " (" + match.getBlgnTeam().getOvrs() + ")";
                rrr = "R. run rate: " + match.getInnDetails().getRrr();
            }

            crr = "C. run rate: " + match.getInnDetails().getCrr();
            partnership = "Partnership: " + match.getInnDetails().getCprtshp();

            holder.score_team_batting.setText(score_btn_team);
            holder.score_team_bowling.setText(score_blng_team);
            holder.rrr.setText(rrr);
            holder.crr.setText(crr);
            holder.c_partnership.setText(partnership);

        } else if (status.equals("Result")) {
//            if(match.getMom().length() > 0){
//                holder.mom.setText("Man Of Match : " + match.getMom());
//            }
//            holder.scoreCard.setVisibility(View.GONE);
            holder.score_team_batting.setText("Man Of Match : " + match.getMom());
            holder.score_team_bowling.setVisibility(View.GONE);
            holder.rrr.setVisibility(View.GONE);
            holder.crr.setVisibility(View.GONE);
            holder.c_partnership.setVisibility(View.GONE);
        } else if ((status.equals("nextlive")) || status.equals("preview")) {

//            holder.scoreCard.setVisibility(View.GONE);
//            holder.momContainer.setVisibility(View.GONE);
//            holder.scoreContainer.setVisibility(View.GONE);

            if(status.equals("nextlive")){
                holder.score_team_batting.setText("Upcoming Match");
            }else if(status.equals("preview")){
                holder.score_team_batting.setText("Match is about to start");
            }

//            holder.score_team_batting.setText(score_btn_team);
//            holder.score_team_bowling.setText(score_blng_team);
//            holder.rrr.setText(rrr);
//            holder.crr.setText(crr);
//            holder.c_partnership.setText(partnership);

        }

        holder.heading.setText(heading);
//        holder.status.setText(status);
        holder.description.setText(description);
        holder.toggle_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!extra_visible) {
                    holder.extra.setVisibility(View.VISIBLE);
                    extra_visible = true;
                    holder.toggle_extra.setImageResource(R.drawable.ic_expand_less_black_24dp);
                } else {
                    holder.extra.setVisibility(View.GONE);
                    extra_visible = false;
                    holder.toggle_extra.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class VH extends RecyclerView.ViewHolder {

        TextView heading, status, score_team_batting, score_team_bowling, crr, rrr, c_partnership, description, mom;
        RelativeLayout extra, scoreContainer, momContainer, scoreCard;
        ImageView toggle_extra;

        public VH(View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.left_heading);
            scoreCard = itemView.findViewById(R.id.score_card);
            scoreContainer = itemView.findViewById(R.id.match_scores);
            momContainer = itemView.findViewById(R.id.mom_container);
            mom = itemView.findViewById(R.id.mom);
//            status = itemView.findViewById(R.id.right_heading);
            extra = itemView.findViewById(R.id.match_details);
            toggle_extra = itemView.findViewById(R.id.toggle_extra);
            score_team_batting = itemView.findViewById(R.id.team_batting);
            score_team_bowling = itemView.findViewById(R.id.team_bowling);
            crr = itemView.findViewById(R.id.current_rr);
            rrr = itemView.findViewById(R.id.required_rr);
            c_partnership = itemView.findViewById(R.id.partnership);
            description = itemView.findViewById(R.id.match_description);
        }
    }
}
