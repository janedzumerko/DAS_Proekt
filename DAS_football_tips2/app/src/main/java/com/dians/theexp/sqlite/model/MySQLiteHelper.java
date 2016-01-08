/*
HOW TO USE:


*/

package com.dians.theexp.sqlite.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dians.theexp.sqlite.helper.Match;
import com.dians.theexp.sqlite.helper.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by k1ko on 11/24/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "MySQLiteHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "userTickets";

    // Table Names
    private static final String TABLE_TICKET = "tickets";
    private static final String TABLE_MATCH = "matches";
    private static final String TABLE_TICKET_MATCH = "ticket_matches";

    // TICKETS Table - column names
    private static final String KEY_TICKET_ID = "ticket_id";
    private static final String KEY_DATETIME = "datetime";

    // MATCHES Table - column names
    private static final String KEY_MATCH_ID = "match_id";
    private static final String KEY_HOME_TEAM = "home_team";
    private static final String KEY_AWAY_TEAM = "away_team";
    private static final String KEY_PREDICTION = "prediction";

    // REL_TICKET_MATCH Table - column names
    private static final String KEY_REL_ID = "rel_id";
    private static final String KEY_REL_TICKET_ID = "rel_ticket_id";
    private static final String KEY_REL_MATCH_ID = "rel_match_id";

    // Ticket Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_TICKET = "CREATE TABLE "
            + TABLE_TICKET + "(" + KEY_TICKET_ID + " INTEGER PRIMARY KEY," + KEY_DATETIME + " TEXT" + ")";

    // Match table create statement
    private static final String CREATE_TABLE_MATCH = "CREATE TABLE " + TABLE_MATCH
            + "(" + KEY_MATCH_ID + " INTEGER PRIMARY KEY," + KEY_HOME_TEAM
            + " TEXT," + KEY_AWAY_TEAM + " TEXT," + KEY_PREDICTION + " INTEGER" + ")";

    // Relation table create statement
    private static final String CREATE_TABLE_TICKET_MATCH = "CREATE TABLE "
            + TABLE_TICKET_MATCH + "(" + KEY_REL_ID + " INTEGER PRIMARY KEY,"
            + KEY_REL_TICKET_ID + " INTEGER," + KEY_REL_MATCH_ID + " INTEGER" + ")";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_TICKET);
        db.execSQL(CREATE_TABLE_MATCH);
        db.execSQL(CREATE_TABLE_TICKET_MATCH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET_MATCH);

        // create new tables
        onCreate(db);
    }

    public void createMatch(Match match, long ticket_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // values.put(KEY_MATCH_ID, match.getId());
        values.put(KEY_HOME_TEAM, match.getHomeTeam());
        values.put(KEY_AWAY_TEAM, match.getAwayTeam());
        values.put(KEY_PREDICTION, match.getPrediction());

        // insert row
        long match_id = db.insert(TABLE_MATCH, null, values);

        // assigning tickets to matches
        createMatchTicket(match_id, ticket_id);

    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // get single match
    public Match getMatch(long match_id) throws NullPointerException {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_MATCH + " WHERE " + KEY_MATCH_ID + " = " + match_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        Match match = new Match();
        try {
            match.setId(c.getInt(c.getColumnIndex(KEY_MATCH_ID)));
            match.setHomeTeam(c.getString(c.getColumnIndex(KEY_HOME_TEAM)));
            match.setAwayTeam(c.getString(c.getColumnIndex(KEY_AWAY_TEAM)));
            match.setPrediction(c.getInt(c.getColumnIndex(KEY_PREDICTION)));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        c.close();

        return match;
    }

    // getting all matches
    public List<Match> getAllMatches() {
        List<Match> matches = new ArrayList<Match>();
        String selectQuery = "SELECT  * FROM " + TABLE_MATCH;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Match match = new Match();
                match.setId(c.getInt(c.getColumnIndex(KEY_MATCH_ID)));
                match.setHomeTeam(c.getString(c.getColumnIndex(KEY_HOME_TEAM)));
                match.setAwayTeam(c.getString(c.getColumnIndex(KEY_AWAY_TEAM)));
                match.setPrediction(c.getInt(c.getColumnIndex(KEY_PREDICTION)));

                // adding to list
                matches.add(match);
            } while (c.moveToNext());
        }
        c.close();
        return matches;
    }

    // getting all matches under single ticket
    public List<Match> getAllMatchesInTicket(Long ticket_id) {
        List<Match> matches = new ArrayList<Match>();

        String selectQuery = "SELECT * FROM "
                + TABLE_MATCH + " tm, "
                + TABLE_TICKET + " tt, "
                + TABLE_TICKET_MATCH + " ttm "
                + "WHERE tt." + KEY_TICKET_ID + " = '" + ticket_id + "'"
                + " AND tt." + KEY_TICKET_ID + " = " + "ttm." + KEY_REL_TICKET_ID
                + " AND tm." + KEY_MATCH_ID + " = " + "ttm." + KEY_REL_MATCH_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Match match = new Match();
                match.setId(c.getInt(c.getColumnIndex(KEY_MATCH_ID)));
                match.setHomeTeam(c.getString(c.getColumnIndex(KEY_HOME_TEAM)));
                match.setAwayTeam(c.getString(c.getColumnIndex(KEY_AWAY_TEAM)));
                match.setPrediction(c.getInt(c.getColumnIndex(KEY_PREDICTION)));

                // adding to todo list
                matches.add(match);
            } while (c.moveToNext());
        }
        c.close();

        return matches;
    }

    // updating a match
    public int updateMatch(Match match) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_MATCH_ID, match.getId());
        values.put(KEY_HOME_TEAM, match.getHomeTeam());
        values.put(KEY_AWAY_TEAM, match.getAwayTeam());
        values.put(KEY_PREDICTION, match.getPrediction());

        // updating row
        return db.update(TABLE_MATCH, values, KEY_MATCH_ID + " = ?",
                new String[]{String.valueOf(match.getId())});
    }

    // Deleting a match
    public void deleteMatch(long match_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MATCH, KEY_MATCH_ID + " = ?", new String[]{String.valueOf(match_id)});
    }

    // get match row count
    public int getMatchCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_MATCH, null);

        return c.getCount();
    }

    // Creating ticket
    public long createTicket(Ticket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_TICKET_ID, ticket.getId());
        values.put(KEY_DATETIME, ticket.getDatetime());

        // insert row
        long t_id = db.insert(TABLE_TICKET, null, values);
        return t_id;
    }

    // get ticket count
    public int getTicketCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TICKET, null);

        return c.getCount();
    }

    // All tickets
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<Ticket>();
        String selectQuery = "SELECT  * FROM " + TABLE_TICKET;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Ticket t = new Ticket();
                t.setId(c.getLong((c.getColumnIndex(KEY_TICKET_ID))));
                t.setDatetime(c.getString(c.getColumnIndex(KEY_DATETIME)));

                // adding to tickets list
                tickets.add(t);
            } while (c.moveToNext());
        }
        c.close();
        return tickets;
    }

    // Updating a ticket
    public int updateTicket(Ticket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_TICKET_ID, ticket.getId());
        values.put(KEY_DATETIME, ticket.getDatetime());

        // updating row
        return db.update(TABLE_TICKET, values, KEY_TICKET_ID + " = ?",
                new String[]{String.valueOf(ticket.getId())});
    }

    // Deleting a ticket and all matches in it
    public void deleteTicket(long ticket_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // get all matches in this ticket
        List<Match> allMatchesInTicket = getAllMatchesInTicket(ticket_id);

        // delete all matches
        for (Match match : allMatchesInTicket) {
            deleteMatch(match.getId());
        }

        // now delete the ticket
        db.delete(TABLE_TICKET, KEY_TICKET_ID + " = ?",
                new String[]{String.valueOf(ticket_id)});

    }


    // @TODO
    public Long createMatchTicket(long match_id, long ticket_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REL_MATCH_ID, match_id);
        values.put(KEY_REL_TICKET_ID, ticket_id);

        long id = db.insert(TABLE_TICKET_MATCH, null, values);

        return id;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
