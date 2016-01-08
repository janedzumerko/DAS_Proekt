package com.dians.theexp.main.Tab3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dians.theexp.main.R;

import java.util.List;

/**
 * Created by k1ko on 1/4/16.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<TicketInfo> ticketList;

    public TicketAdapter(List<TicketInfo> ticketList) {
        this.ticketList = ticketList;
    }


    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    @Override
    public void onBindViewHolder(TicketViewHolder ticketViewHolder, int i) {
        TicketInfo t = ticketList.get(i);
        ticketViewHolder.date.setText(t.dateTime);
        ticketViewHolder.matches.setText(t.matches);

    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.tab3cardview, viewGroup, false);

        return new TicketViewHolder(itemView);
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {

        protected TextView date;
        protected TextView matches;

        public TicketViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.title);
            matches = (TextView) v.findViewById(R.id.matches);
        }
    }
}
