package com.example.jane.das_football_tips.Tab3;

/**
 * Created by Jane on 10/12/2015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.com.dians.theexp.sqlite.helper.Ticket;
import com.com.dians.theexp.sqlite.model.MySQLiteHelper;
import com.example.jane.das_football_tips.R;

import java.util.ArrayList;
import java.util.List;


public class Tab3 extends Fragment {

    MySQLiteHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3, container, false);

        final ListView lView = (ListView) v.findViewById(R.id.lvTiketi);

        db = new MySQLiteHelper(this.getContext());

        final List<Ticket> tickets = db.getAllTickets();

        if (tickets.size() == 0) {
            return v;
        }

        final List<String> listOfDates = new ArrayList<String>();
        for (Ticket t : tickets) {
            listOfDates.add(t.getDatetime());
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, listOfDates);
        lView.setAdapter(adapter);

        return v;
    }
}