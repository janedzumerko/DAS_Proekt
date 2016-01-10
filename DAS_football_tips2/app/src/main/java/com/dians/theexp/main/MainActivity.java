package com.dians.theexp.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dians.theexp.auth.LoginActivity;
import com.dians.theexp.auth.Singleton;
import com.dians.theexp.sqlite.helper.Match;
import com.dians.theexp.sqlite.helper.Ticket;
import com.dians.theexp.sqlite.model.MySQLiteHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    ArrayList<String> list;


    CharSequence Titles[];
    int Numboftabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        list = new ArrayList<String>();

        list.add(getString(R.string.tabela));
        list.add(getString(R.string.predlogtiket));
        list.add(getString(R.string.dosegahnitiketi));

        Titles = list.toArray(new CharSequence[list.size()]);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });


        adapter.notifyDataSetChanged();
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout) {
            // remove user data

            Singleton.getInstance().userId = -1;
            Singleton.getInstance().username = "";

            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("username").commit();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("userId").commit();

            MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
            db.clearTables();
            db.closeDB();

            // workaround because the application keeps restarting the MainActivity if exited
            // through finish();
            android.os.Process.killProcess(android.os.Process.myPid());

        }

        if (id == R.id.action_favorite) {
            tryRetrieveTickets();
        }

        return super.onOptionsItemSelected(item);
    }

    public void tryRetrieveTickets() {
        String s = "http://52.26.249.101/Login/getTicket?userid=" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("userId", -1);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest req = new StringRequest(Request.Method.GET, s, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);

                addToDB(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error", error.toString());

            }
        }) {
            // key value pairs
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<String, String>();
                params.put("userid", String.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("userId", -1)));
                return params;
            }
        };

        // add the request object to the queue to be executed
        requestQueue.add(req);
    }

    public void addToDB(String response) {
        JsonArray obj = new JsonParser().parse(response).getAsJsonArray();

        Log.d("obj", obj.toString());

        MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());


        for (int i = 0; i < obj.size(); i++) {
            JsonElement jobj = obj.get(i);
            Log.d("element", jobj.toString());
            JsonObject jsonobj = jobj.getAsJsonObject();

            String tDate = jsonobj.get("datetime").toString();
            Log.d("element", "" + jsonobj.get("datetime"));

            long ticket1id = db.createTicket(new Ticket(tDate));

            for (int j = 0; ; j++) {
                try {
                    //JsonElement jelem = jsonobj.get(String.valueOf(j));
                    JsonObject match = (JsonObject) jsonobj.get(String.valueOf(j));

                    String hometeam = match.get("HOMETEAM").toString();
                    String awayteam = match.get("AWAYTEAM").toString();
                    Integer prediction = match.get("PREDICTION").getAsInt();

                    Match m = new Match(hometeam, awayteam, prediction);

                    db.createMatch(m, ticket1id);

                } catch (NullPointerException npe) {
                    Log.e("tolku", "");
                    break;
                }

            }



        }
    }
}
