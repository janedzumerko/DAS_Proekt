package com.dians.theexp.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dians.theexp.main.MainActivity;
import com.dians.theexp.main.R;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText inputUsername;
    EditText inputPassword;
    Button loginUserBtn;

    String user = null;
    String pass = null;
    Integer userId = -1;

    ProgressDialog progressDialog;

    String resp = null;
    String loginURL = "http://52.26.249.101/Login/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        /*
         *   Check whether the user is logging in for the first time.
         *   If true, it sends him directly to the main view.
         */
        String checkLoggedInUserName = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).getString("username",
                        Singleton.getInstance().username);
        if (checkLoggedInUserName != null) {
            sendToMain();
        }

        inputUsername = (EditText) findViewById(R.id.input_username);
        inputPassword = (EditText) findViewById(R.id.input_password);
        loginUserBtn = (Button) findViewById(R.id.btn_login);

        loginUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login() throws Exception {
        if (inputUsername.getText().toString().trim().length() < 4) {
            Toast.makeText(getApplicationContext(), "Username too short!", Toast.LENGTH_SHORT).show();
        } else if (inputPassword.getText().toString().trim().length() < 4) {
            Toast.makeText(getApplicationContext(), "Password too short!", Toast.LENGTH_SHORT).show();
        } else {
            user = inputUsername.getText().toString();
            pass = inputPassword.getText().toString();

            // initialize progress dialog
            progressDialog = new ProgressDialog(LoginActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Signing in...");
            progressDialog.show();

            performPOSTRequest(user, pass);
        }
    }


    /*
     * Passes the entered username and password to the server using key-value pairs
     * and waits for the response. If the response is valid, it will return the
     * id, username and email of the user and then redirect him to the main view.
     */
    public void performPOSTRequest(final String username, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String URL = loginURL;

        StringRequest req = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    Log.d("response", "" + response);
                    if (response.contains("Ne postoi")) {

                        Toast.makeText(getApplicationContext(), "Wrong username or password!", Toast.LENGTH_SHORT).show();
                    } else if (response.contains("id")) {
                        userId = Integer.parseInt(response.split("\"")[3]);
                        Toast.makeText(getApplicationContext(), "Sign in successful!", Toast.LENGTH_SHORT).show();
                        commitUsernamePrefs();
                        sendToMain();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.e("Error: ", verror.getStackTrace());
                progressDialog.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                }

                // Network errors validation
                if (error instanceof TimeoutError) {
                    Log.e("Volley", "TimeoutError");
                    Toast.makeText(getApplicationContext(), "Timeout network error!", Toast.LENGTH_SHORT).show();
                }else if(error instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), "No connection!", Toast.LENGTH_SHORT).show();
                    Log.e("Volley", "NoConnectionError");
                } else if (error instanceof ServerError) {
                    Log.e("Volley", "ServerError");
                    Toast.makeText(getApplicationContext(), "Server error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Log.e("Volley", "NetworkError");
                    Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT).show();
                }


            }
        }) {
            // key value pairs
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                Log.d("username", "" + username);
                Log.d("password", "" + password);

                return params;
            }
        };

        // add the request object to the queue to be executed
        requestQueue.add(req);


    }


    public void sendToSignUpForm(View v) {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendToMain() {
        // Start the Main activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }

    public void commitUsernamePrefs() {
        // save username and id
        Singleton.getInstance().username = user;
        Singleton.getInstance().userId = userId;
        Log.d("userid", "" + userId + " " + Singleton.getInstance().userId);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putString("username", Singleton.getInstance().username)
                .commit();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putInt("userId", Singleton.getInstance().userId)
                .commit();
    }
}