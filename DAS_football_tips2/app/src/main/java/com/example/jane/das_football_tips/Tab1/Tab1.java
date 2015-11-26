package com.example.jane.das_football_tips.Tab1;

/**
 * Created by Jane on 10/12/2015.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.jane.das_football_tips.R;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class Tab1 extends Fragment {

    // JSON Node names
    private static final String TAG_TEAMS = "teams";
    private static final String TAG_ID = "stand_id";
    private static final String TAG_SEASON = "stand_season";
    private static final String TAG_ROUND = "stand_round";
    private static final String TAG_NAME = "stand_team_name";
    private static final String TAG_STAGE_ID = "stand_stage_id";
    private static final String TAG_COUNTRY = "stand_country";
    private static final String TAG_TEAM_ID = "stand_team_id";
    private static final String TAG_RECENT = "stand_recent_form";
    private static final String TAG_POSITION = "stand_position";
    private static final String TAG_GOAL_DIFFERENCE = "stand_gd";
    private static final String TAG_POINTS = "stand_points";
    //private static String urlRequest = "http://football-api.com/api/?Action=standings&APIKey=" + "8a5032b6-36a1-af35-9f577a444de1&comp_id=1204";
    private static String urlRequest = "http://52.26.249.101/RestTable/index";
    public CircularProgressView progresSearch;
    Button button1;
    // contacts JSONArray
    JSONArray contacts = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    ListView lv;
    LinearLayout layoutForInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);

        contactList = new ArrayList<HashMap<String, String>>();
        progresSearch = (CircularProgressView) v.findViewById(R.id.progress_bar_loading);
        lv = (ListView) v.findViewById(R.id.list);
        layoutForInfo = (LinearLayout) v.findViewById(R.id.info_za_tabela);
        setVisibilityToView();
        new RetrieveFeedTask().execute();



        return v;
    }

    public void setVisibilityToView() {
        if(contactList.isEmpty()) {
                layoutForInfo.setVisibility(View.GONE);
                lv.setVisibility(View.GONE);
                progresSearch.setVisibility(View.VISIBLE);

        } else {
            layoutForInfo.setVisibility(View.VISIBLE);
            lv.setVisibility(View.VISIBLE);
            progresSearch.setVisibility(View.GONE);
        }
    }


    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("JANE", "It started..");
            setVisibilityToView();
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                URL url = new URL
                   (urlRequest);
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
                            contacts = jsonObj.getJSONArray(TAG_TEAMS);

                            // looping through All Contacts
                            for(int i=0; i< contacts.length(); i++) {
                                JSONObject c = contacts.getJSONObject(i);

                                String id = c.getString(TAG_ID);
                                String season = c.getString(TAG_SEASON);
                                String round = c.getString(TAG_ROUND);
                                String name = c.getString(TAG_NAME);
                                String stage_id = c.getString(TAG_STAGE_ID);
                                String country = c.getString(TAG_COUNTRY);
                                String team_id = c.getString(TAG_TEAM_ID);
                                String recen_score = c.getString(TAG_RECENT);
                                String position = c.getString(TAG_POSITION);
                                if(position.length() == 1) {
                                    position = " " + position;
                                }
                                String goals = c.getString(TAG_GOAL_DIFFERENCE);
                                String points = c.getString(TAG_POINTS);


                                // tmp hashmap for single contact
                                HashMap<String, String> contact = new HashMap<String, String>();

                                // adding each child node to HashMap key => value
                                contact.put(TAG_ID, id);
                                contact.put(TAG_SEASON, season);
                                contact.put(TAG_ROUND, round);
                                contact.put(TAG_NAME, name);
                                contact.put(TAG_STAGE_ID, stage_id);
                                contact.put(TAG_COUNTRY, country);
                                contact.put(TAG_TEAM_ID, team_id);
                                contact.put(TAG_RECENT, recen_score);
                                contact.put(TAG_POSITION, position);
                                contact.put(TAG_GOAL_DIFFERENCE, goals);
                                contact.put(TAG_POINTS, points);

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
            setVisibilityToView();
        }

        @Override
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("JANE", response);

            setVisibilityToView();
            ListAdapter adapter = new SimpleAdapter(
                    getActivity().getApplicationContext() , contactList,
                    R.layout.list_item, new String[] { TAG_POSITION, TAG_NAME,
                    TAG_ROUND, TAG_GOAL_DIFFERENCE, TAG_POINTS },
                    new int[] { R.id.team_position,
                            R.id.team_name,
                            R.id.matches_till_now,
                            R.id.team_goals,
                            R.id.team_points});

            lv.setAdapter(adapter);
        }
    }


}