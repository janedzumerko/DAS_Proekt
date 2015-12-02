package com.example.jane.das_football_tips.Tab2;

/**
 * Created by Jane on 10/12/2015.
 */
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jane.das_football_tips.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Tab2 extends Fragment {

    public boolean flagForNoMatches;
    public int day;
    public String dayOfTheWeek;
    public SimpleDateFormat simpleDF;
    public Date date;
    public String utreshenDatum;
    public String vcerashenDatum;

    public String deneshenDatumZaTest;

    public Calendar c;

    TextView dateTextView;
    String DeneshenDatum;

    String urlMatches = "http://52.26.249.101/RestGet/index?date=";

    private List<MatchTipInfo> matchesTips;

    // JSON Node names
    private static final String TAG_MATCHES = "matches";
    private static final String TAG_HOME_TEAM = "home_team";
    private static final String TAG_AWAY_TEAM = "away_team";
    private static final String TAG_FULL_TIME_BET = "full_time_bet";
    private static final String TAG_TOTAL_GOALS_BET = "total_goals_bet";

    // contacts JSONArray
    JSONArray contacts = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    private Button desno;
    private Button levo;

    RecyclerView rv;
    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2, container, false);

        dateTextView = (TextView) v.findViewById(R.id.date_text_view);

        c = Calendar.getInstance();
        rv = (RecyclerView) v.findViewById(R.id.rv);
        tv = (TextView) v.findViewById(R.id.empty_text_view);

        contactList = new ArrayList<HashMap<String, String>>();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        rv.setLayoutManager(llm);

        desno = (Button) v.findViewById(R.id.nadesno);
        desno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagForNoMatches) {
                    c.add(Calendar.DATE, 1);
                    date = c.getTime();
                    dayOfTheWeek = getDayOfTheWeek(c.get(Calendar.DAY_OF_WEEK));
                    utreshenDatum = simpleDF.format(date);
                    Log.i("JANE", utreshenDatum);
                    DeneshenDatum = utreshenDatum;
                    urlMatches = "http://52.26.249.101/RestGet/index?date=";
                    new DailyMatches().execute();
                }

            }
        });

        levo = (Button) v.findViewById(R.id.nalevo);
        levo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deneshenDatumZaTest != DeneshenDatum) {
                    c.add(Calendar.DATE, -1);
                    date = c.getTime();
                    dayOfTheWeek = getDayOfTheWeek(c.get(Calendar.DAY_OF_WEEK));
                    vcerashenDatum = simpleDF.format(date);
                    Log.i("JANE", vcerashenDatum);
                    DeneshenDatum = vcerashenDatum;
                    urlMatches = "http://52.26.249.101/RestGet/index?date=";
                    new DailyMatches().execute();
                }
            }
        });




        day = c.get(Calendar.DAY_OF_WEEK);
        dayOfTheWeek = getDayOfTheWeek(day);

        simpleDF = new SimpleDateFormat("dd-MM-yyyy");
        date = new Date();
        DeneshenDatum = simpleDF.format(date);
        Log.i("JANE", DeneshenDatum);
        deneshenDatumZaTest = DeneshenDatum;
        deneshenDatumZaTest = deneshenDatumZaTest.replace("-",".");
        c.setTime(date);

        new DailyMatches().execute();


        return v;
    }

    private void initializeData() {
        Log.i("NACIONALNO", "Sho se deshava ovde?");
        Log.i("NACIONALNO", "ContactList.isEmpty() " + contactList.isEmpty() + "");

        matchesTips = new ArrayList<>();
        for (HashMap<String, String> map : contactList) {
            String tmp_home_team = map.get(TAG_HOME_TEAM);
            String tmp_away_team = map.get(TAG_AWAY_TEAM);
            String tmp_goal_bet = map.get(TAG_TOTAL_GOALS_BET);
            String tmp_full_bet = map.get(TAG_FULL_TIME_BET);
            Log.i("PROBA", tmp_home_team + " : " + tmp_away_team + " - "
            + tmp_full_bet + " // " + tmp_goal_bet);
            Log.i("PROBA","-------------");
            matchesTips.add(new MatchTipInfo(tmp_home_team, tmp_away_team,
                    tmp_goal_bet, tmp_full_bet));

        }

    }

    private void initializeAdapter() {
        RVAdapter adapter = new RVAdapter(matchesTips);
        rv.setAdapter(adapter);
    }

    private String getDayOfTheWeek(int day) {
        String[] denoviArray = getResources().getStringArray(R.array.denovi_array);
        switch (day) {
            case Calendar.SUNDAY:
                return denoviArray[0];
            case Calendar.MONDAY:
                return denoviArray[1];
            case Calendar.TUESDAY:
                return denoviArray[2];
            case Calendar.WEDNESDAY:
                return denoviArray[3];
            case Calendar.THURSDAY:
                return denoviArray[4];
            case Calendar.FRIDAY:
                return denoviArray[5];
            case Calendar.SATURDAY:
                return denoviArray[6];

        }
        return "";
    }




    class DailyMatches extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("JANE", "It started..");
            DeneshenDatum = DeneshenDatum.replace("-",".");
            matchesTips = new ArrayList<>();
            urlMatches = urlMatches + DeneshenDatum;

            contactList = new ArrayList<HashMap<String, String>>();

            dateTextView.setText(DeneshenDatum + " / " + dayOfTheWeek);
            desno.setEnabled(false);
            levo.setEnabled(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                URL url = new URL
                        (urlMatches);

                Log.i("NACIONALNO", urlMatches);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    if(stringBuilder.toString() != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(stringBuilder.toString());

                            // Getting JSON Array node
                            contacts = jsonObj.getJSONArray(TAG_MATCHES);

                            // looping through All Matches
                            int dolzina = contacts.length();
                            for(int i=0; i< dolzina; i++) {

                                JSONObject c = contacts.getJSONObject(i);

                                String home_team = c.getString(TAG_HOME_TEAM);
                                String away_team = c.getString(TAG_AWAY_TEAM);
                                String bet = c.getString(TAG_FULL_TIME_BET);
                                String goals_bet = c.getString(TAG_TOTAL_GOALS_BET);


                                // tmp hashmap for single match
                                HashMap<String, String> contact = new HashMap<String, String>();

                                // adding each child node to HashMap key => value
                                contact.put(TAG_HOME_TEAM, home_team);
                                contact.put(TAG_AWAY_TEAM, away_team);
                                contact.put(TAG_FULL_TIME_BET, bet);
                                contact.put(TAG_TOTAL_GOALS_BET, goals_bet);

                                // adding contact to contact list
                                contactList.add(contact);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }


                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }

            } catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("JANE", response);
            Log.i("JANE", "JSON : " + response);

            initializeData();
            initializeAdapter();
            desno.setEnabled(true);
            levo.setEnabled(true);
            Log.i("NACIONALNO", matchesTips.isEmpty() + "");
            if(matchesTips.isEmpty()) {
                flagForNoMatches = true;
                rv.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
            } else {
                flagForNoMatches = false;
                rv.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
            }

            if(!flagForNoMatches) {
                desno.setEnabled(false);
            }

            Log.i("JANE", "Deneshen datum : " + deneshenDatumZaTest + " == " + DeneshenDatum);
            Log.i("JANE", "Dali vraka true :"  + deneshenDatumZaTest.equals(DeneshenDatum));
            if (deneshenDatumZaTest.equals(DeneshenDatum)) {
                levo.setEnabled(false);
            } else {
                levo.setEnabled(true);
            }

        }
    }
}