package com.dians.theexp.main.Tab2;

/**
 * Created by Jane on 10/12/2015.
 */
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.dians.theexp.sqlite.helper.Match;
import com.dians.theexp.sqlite.helper.Ticket;
import com.dians.theexp.sqlite.model.MySQLiteHelper;
import com.dians.theexp.main.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;

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

    private Button btnCreateTicket;

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

        btnCreateTicket = (Button) v.findViewById(R.id.create_ticket);
        btnCreateTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (matchesTips != null) {
                    MySQLiteHelper db = new MySQLiteHelper(getContext());
                    String timeOfTicketCreation = getDateTime();
                    long ticket_id = db.createTicket(new Ticket(timeOfTicketCreation));

                    JSONObject innerJSON = new JSONObject();
                    JSONArray matchesJSON = new JSONArray();
                    JSONObject matchJSON;

                    StringBuilder sb = new StringBuilder();
                    sb.append("{\n");
                    sb.append("\t\"usertickets\": {\n");
                    sb.append("\t\t\"username\": \"" + PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", "") + "\",\n");
                    sb.append("\t\t\"user_id\": " + PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("userId", -1) + ",\n");
                    sb.append("\t\t\"ticket_datetime\": \"" + timeOfTicketCreation + "\",\n");
                    sb.append("\t\t\"matches\": [\n");



                    for (MatchTipInfo mti : matchesTips) {
                        Log.d("addticket-mti", mti.toString());
                        db.createMatch(new Match(mti.getHome_team(), mti.getAway_team(), Integer.parseInt(mti.getFull_time_bet())), ticket_id);
                        try {
                            matchJSON = new JSONObject();
                            matchJSON.put("hometeam", mti.getHome_team());
                            matchJSON.put("awayteam", mti.getAway_team());
                            matchJSON.put("prediction", mti.getFull_time_bet());
                            matchesJSON.put(matchJSON);

                        }catch (JSONException jsone ) {

                        }

                    }

                    for (int i = 0; i < matchesTips.size(); ++i) {
                        sb.append("\t\t\t{\n");

                        sb.append("\t\t\t\t\"hometeam\": \"" + matchesTips.get(i).getHome_team() + "\",\n");
                        sb.append("\t\t\t\t\"awayteam\": \"" + matchesTips.get(i).getAway_team() + "\",\n");
                        sb.append("\t\t\t\t\"prediction\": " + matchesTips.get(i).getFull_time_bet() + "\n");

                        if (i == matchesTips.size() - 1) {
                            sb.append("\t\t\t}\n");
                        } else {
                            sb.append("\t\t\t},\n");
                        }
                    }

                    sb.append("\t\t]\n");
                    sb.append("\t}\n");
                    sb.append("}\n");

                    try {
                        innerJSON.put("matches", matchesJSON);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONObject finalJSON = new JSONObject();

                    try {
                        innerJSON.put("ticket_datetime", timeOfTicketCreation);
                        innerJSON.put("user_id", PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("userId", -1));
                        innerJSON.put("username", PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", ""));
                    } catch (JSONException jsone) {
                        Log.d("json_ex", "" + jsone.getStackTrace());
                    }

                    try {
                        finalJSON.put("usertickets", innerJSON);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    Log.d("finalJSON", "" + finalJSON.toString());
                    db.closeDB();

                    try {
                        //saveTicketToServer(finalJSON);
                        saveTicketToServer(sb.toString(), finalJSON);
                    } catch (Exception e) {
                        Log.d("saveticketerror", "" + e.getStackTrace());
                    }

                    Toast.makeText(getContext(), "Your ticket has been created.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No matches selected.", Toast.LENGTH_SHORT).show();
                    Log.d("matchesTips", "" + matchesTips.toString());
                }
            }
        });

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

    ProgressDialog progressDialog;

    public void saveTicketToServer(String userticketsJSON, final JSONObject userTicketJSON) {




        // initialize progress dialog
        progressDialog = new ProgressDialog(getContext(), ProgressDialog.STYLE_SPINNER);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving ticket...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //Req
        String ticketsURL = "http://52.26.249.101/Login/saveTicket";

        Log.d("json", userticketsJSON);

        String test = "usertickets[username]=k1ko&usertickets[user_id]=2&usertickets[ticket_datetime]=ajsega1&usertickets[matches][0][hometeam]=mutaaaad&usertickets[matches][0][awayteam]=chelsea&usertickets[matches][0][prediction]=0&usertickets[matches][1][hometeam]=everton&usertickets[matches][1][awayteam]=tottenham&usertickets[matches][1][prediction]=2";


        $.ajax(new AjaxOptions().url(ticketsURL)
                .type("POST")
                .data(test)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .context(getContext())
                .success(new Function() {
                    @Override
                    public void invoke($ droidQuery, Object... params) {
                        progressDialog.dismiss();
                        //Log.d("Ajax", "" + (String) params[0]);
                        for (int i = 0; i < params.length; i++) {
                            Log.d("sssssAjax", "" + params.toString());
                        }
                    }
                }).error(new Function() {
                    @Override
                    public void invoke($ droidQuery, Object... params) {
                        progressDialog.dismiss();
                        int statusCode = (Integer) params[1];
                        String error = (String) params[2];
                        Log.d("errAjax", statusCode + " " + error);
                    }
                }));

        /*JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ticketsURL, userticketsJSON, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    Log.d("response", "" + response.toString());
                } catch (Exception e) {
                    Log.d("response", "" + response.toString());
                    Log.d("error", "" + "" + e.getStackTrace());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.e("Error: ", verror.getStackTrace());

                progressDialog.dismiss();

                Log.d("Volley", "" + error.getStackTrace());

                if (error instanceof TimeoutError) {
                    Log.d("Volley", "TimeoutError");
                    Toast.makeText(getContext(), "Timeout network error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), "No connection!", Toast.LENGTH_SHORT).show();
                    Log.d("Volley", "NoConnectionError");
                } else if (error instanceof ServerError) {
                    Log.d("Volley", "ServerError");
                    Toast.makeText(getContext(), "Server error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Log.d("Volley", "NetworkError");
                    Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
                return params;
            }
        };

        // add the request object to the queue to be executed
        requestQueue.add(req);*/

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


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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