package com.dians.theexp.main.Tab3;

/**
 * Created by Jane on 10/12/2015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

        // sets the options for the listview
        // will show a vertical list
        lView = (RecyclerView) v.findViewById(R.id.ticket_list_view);
        lView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lView.setLayoutManager(llm);

        tView = (TextView) v.findViewById(R.id.msg_tab3);

        updateView();

        // if no tickets, it will show a message that the user has no tickets
        if (tickets.size() == 0) {
            tView.setVisibility(View.VISIBLE);
            return v;
        }
        tView.setVisibility(View.INVISIBLE);

        return v;
    }

    /*
     * Pulls all tickets from the local SQLite database
     * and adds them to the adapter for viewing
     */
    public void updateView() {
        db = new MySQLiteHelper(this.getContext());

        tickets = db.getAllTickets();
        listTickets = new ArrayList<TicketInfo>();

        StringBuilder sb;
        int i = 0;
        for (Ticket t : tickets) {
            sb = new StringBuilder();

            List<Match> matches = db.getAllMatchesInTicket(t.getId());
            for (Match m:
                    matches) {
                sb.append(m.getHomeTeam() + " vs " + m.getAwayTeam() + "\nPrediction: " + m.getPrediction() + "\n");
            }

            TicketInfo ti = new TicketInfo();
            ti.dateTime = t.getDatetime();
            ti.matches = sb.toString();
            listTickets.add(i++, ti);
        }
        db.closeDB();
        ticketAdapter = new TicketAdapter(listTickets);
        lView.setAdapter(ticketAdapter);

        ticketAdapter.notifyDataSetChanged();
    }

    // used for detecting if we are in the third tab
    // if true, it updates the adapter so the newest added tickets will show
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...

            updateView();

            if (!isVisibleToUser) {

                // TODO stop audio playback
            }
        }
    }

}