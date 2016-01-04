package com.com.dians.theexp.sqlite;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.com.dians.theexp.sqlite.helper.Match;
import com.com.dians.theexp.sqlite.helper.Ticket;
import com.com.dians.theexp.sqlite.model.MySQLiteHelper;
import com.example.jane.das_football_tips.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestActivity extends Activity {

    MySQLiteHelper db;

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_test);

        db = new MySQLiteHelper(getApplicationContext());


        // Inserting Tickets in db
        long ticket1id = db.createTicket(new Ticket(getDateTime()));
        long ticket2id = db.createTicket(new Ticket(getDateTime()));
        long ticket3id = db.createTicket(new Ticket(getDateTime()));
        long ticket4id = db.createTicket(new Ticket(getDateTime()));

        Log.d("_", "---------------------------------");
        Log.d("Ticket Count", "" + db.getTicketCount());
        Log.d("_", "---------------------------------");

        // Creating Matches
        Match match1 = new Match("Man UTD", "Chelsea", 0);
        Match match2 = new Match("Vardar", "Barcelona", 1);
        Match match3 = new Match("Bashkimi", "Real Madrid", 0);
        Match match4 = new Match("Hello World", "Goodbye World", 1);
        Match match5 = new Match("Milano", "Milan", 0);
        Match match6 = new Match("Spider-man", "Hulk", 1);
        Match match7 = new Match("Richard Stallman", "Dennis Ritchie", 2);
        Match match8 = new Match("tiger", "saber-toothed cat", 1);
        Match match9 = new Match("homer", "randy", 2);
        Match match10 = new Match("black", "white", 0);
        Match match11 = new Match("aldo", "conor", 2);

        // Inserting Matchs in db
        // Inserting Matches under Ticket
        db.createMatch(match1, ticket1id);
        db.createMatch(match2, ticket1id);
        db.createMatch(match3, ticket2id);
        db.createMatch(match4, ticket2id);
        db.createMatch(match5, ticket2id);
        db.createMatch(match6, ticket2id);
        db.createMatch(match7, ticket2id);
        db.createMatch(match8, ticket3id);
        db.createMatch(match9, ticket4id);
        db.createMatch(match10, ticket4id);
        db.createMatch(match11, ticket4id);

        Log.d("_", "---------------------------------");
        Log.e("Match Count", "" + db.getMatchCount());
        Log.d("_", "---------------------------------");


        // Getting all Ticket names
        Log.d("_", "---------------------------------");
        Log.d("Get Tickets", "");

        List<Ticket> allTickets = db.getAllTickets();
        for (Ticket ticket : allTickets) {
            Log.d("Ticket id", String.format("%d", ticket.getId()));
            Log.d("Ticket date", ticket.getDatetime());
        }
        Log.d("_", "---------------------------------");

        // Getting all Matches
        Log.d("_", "---------------------------------");
        Log.d("Get Matches", "");

        List<Match> allMatches = db.getAllMatches();
        for (Match match : allMatches) {
            Log.d("Match", match.getId() + " " + match.toString());
        }
        Log.d("_", "---------------------------------");

        // Getting Matchs under "Watchlist" Ticket name
        Log.d("_", "---------------------------------");
        Log.d("Match", "Get Matches under single Ticket name");

        List<Match> TicketsWatchList = db.getAllMatchesInTicket(ticket2id);
        for (Match match : TicketsWatchList) {
            Log.d("ticket2id", match.toString());
        }
        Log.d("_", "---------------------------------");

        /*
        // Deleting a Match
        Log.d("_", "---------------------------------");
        Log.d("Delete Match", "");
        Log.d("Match Count", " Before Deleting: " + db.getMatchCount());

        db.deleteMatch(match7.getId());

        Log.d("Match Count", " After Deleting: " + db.getMatchCount());
        Log.d("_", "---------------------------------");
        */

        // Deleting all Matches under Ticket
        Log.d("_", "---------------------------------");
        Log.d("Match Count",
                "in ticket2 Before Deleting Matches: "
                        + db.getAllMatchesInTicket(ticket2id).size());
        Log.d("Ticket count before", "" + db.getTicketCount());

        db.deleteTicket(ticket2id);

        Log.d("Match Count",
                "InTicket2 After Deleting Matches: "
                        + db.getAllMatchesInTicket(ticket2id).size());
        Log.d("Ticket count after", "" + db.getTicketCount());
        Log.d("_", "---------------------------------");

        Log.d("_", "---------------------------------");
        Log.d("Match count", "" + db.getMatchCount());
        Log.d("_", "---------------------------------");

        // Don't forget to close database connection
        db.closeDB();
    }

}
