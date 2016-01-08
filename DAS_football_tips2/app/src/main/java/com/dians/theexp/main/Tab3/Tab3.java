package com.dians.theexp.main.Tab3;

/**
 * Created by Jane on 10/12/2015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dians.theexp.sqlite.helper.Match;
import com.dians.theexp.sqlite.helper.Ticket;
import com.dians.theexp.sqlite.model.MySQLiteHelper;
import com.dians.theexp.main.R;

import java.util.ArrayList;
import java.util.List;


public class Tab3 extends Fragment {

    MySQLiteHelper db;
    List<Ticket> tickets;
    List<TicketInfo> listTickets;
    TicketAdapter ticketAdapter;
    RecyclerView lView;
    TextView tView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3, container, false);

        lView = (RecyclerView) v.findViewById(R.id.ticket_list_view);
        lView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lView.setLayoutManager(llm);
        lView.setAdapter(ticketAdapter);


        tView = (TextView) v.findViewById(R.id.msg_tab3);

        db = new MySQLiteHelper(this.getContext());

        tickets = db.getAllTickets();
        listTickets = new ArrayList<TicketInfo>();


        if (tickets.size() == 0) {
            tView.setVisibility(View.VISIBLE);
            return v;
        }
        tView.setVisibility(View.INVISIBLE);

        StringBuilder sb;
        int i = 0;
        for (Ticket t : tickets) {
            sb = new StringBuilder();

            List<Match> matches = db.getAllMatchesInTicket(t.getId());
            for (Match m:
                 matches) {
                sb.append(m.getHomeTeam() + " vs " + m.getAwayTeam() + "\t" + m.getPrediction() + "\n\n");
            }

            TicketInfo ti = new TicketInfo();
            ti.dateTime = t.getDatetime();
            ti.matches = sb.toString();
            listTickets.add(i++, ti);
        }
        db.closeDB();
        ticketAdapter = new TicketAdapter(listTickets);
        ticketAdapter.notifyDataSetChanged();


        return v;
    }
}