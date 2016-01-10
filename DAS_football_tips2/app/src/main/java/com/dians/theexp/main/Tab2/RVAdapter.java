package com.dians.theexp.main.Tab2;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dians.theexp.main.R;

import java.util.List;

/**
 * Created by Jane on 10/20/2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MatchTipInfoViewHolder>{

    public static class MatchTipInfoViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView hometeam;
        TextView awayteam;
        TextView goal_bet;

        CheckBox cbKec;
        CheckBox cbIks;
        CheckBox cbDvojka;

        MatchTipInfoViewHolder (View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            hometeam = (TextView)itemView.findViewById(R.id.home_team);
            awayteam = (TextView)itemView.findViewById(R.id.away_team);
            goal_bet = (TextView)itemView.findViewById(R.id.goal_bet);

            cbKec = (CheckBox) itemView.findViewById(R.id.checkbox_kec);
            cbIks = (CheckBox) itemView.findViewById(R.id.checkbox_iks);
            cbDvojka = (CheckBox) itemView.findViewById(R.id.checkbox_dvojka);

            cbKec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cbIks.setChecked(false);
                    cbDvojka.setChecked(false);
                }
            });

            cbIks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cbKec.setChecked(false);
                    cbDvojka.setChecked(false);
                }
            });

            cbDvojka.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cbKec.setChecked(false);
                    cbIks.setChecked(false);
                }
            });
        }
    }

    List<MatchTipInfo> persons;

    RVAdapter(List<MatchTipInfo> persons){
        this.persons = persons;
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    @Override
    public MatchTipInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        MatchTipInfoViewHolder pvh = new MatchTipInfoViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MatchTipInfoViewHolder matchTipInfoViewHolder, int i) {
        matchTipInfoViewHolder.hometeam.setText(persons.get(i).home_team);
        matchTipInfoViewHolder.awayteam.setText(persons.get(i).away_team);

        matchTipInfoViewHolder.goal_bet.setText(persons.get(i).goal_bet);

        if(persons.get(i).getFull_time_bet().equals("1")) {
            matchTipInfoViewHolder.cbKec.setChecked(true);
        } else if(persons.get(i).getFull_time_bet().equals("X")) {
            matchTipInfoViewHolder.cbIks.setChecked(true);
        } else if(persons.get(i).getFull_time_bet().equals("2")) {
            matchTipInfoViewHolder.cbDvojka.setChecked(true);
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public List<MatchTipInfo> getItems() {
        return persons;
    }
}