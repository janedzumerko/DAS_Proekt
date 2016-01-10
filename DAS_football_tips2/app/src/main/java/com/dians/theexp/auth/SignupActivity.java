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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dians.theexp.main.MainActivity;
import com.dians.theexp.main.R;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText inputUsername;
    EditText inputPassword;
    EditText inputEmail;
    Button registerUserBtn;

    ProgressDialog progressDialog;

    String user = null;
    String pass = null;
    String email = null;

    String registerURL = "http://52.26.249.101/Login/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inputUsername = (EditText) findViewById(R.id.s_input_name);
        inputPassword = (EditText) findViewById(R.id.s_input_password);
        inputEmail = (EditText) findViewById(R.id.s_input_email);
        registerUserBtn = (Button) findViewById(R.id.btn_signup);

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    register();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void register() {
        if (inputUsername.getText().toString().trim().length() < 4) {
            Toast.makeText(getApplicationContext(), "Username too short!", Toast.LENGTH_SHORT).show();
        } else if (inputPassword.getText().toString().trim().length() < 4) {
            Toast.makeText(getApplicationContext(), "Password too short!", Toast.LENGTH_SHORT).show();
        } else {
            user = inputUsername.getText().toString();
            pass = inputPassword.getText().toString();
            email = inputEmail.getText().toString();

            // initialize progress dialog
            progressDialog = new ProgressDialog(SignupActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Signing up...");
            progressDialog.show();

            performPOSTRequest(user, pass, email);
        }
    }

    /*
     * Sends the user input to the server and saves them if valid. If the response is successful,
     * the user is redirected to the login activity. If not, an error is displayed.
     *
     */
    public void performPOSTRequest(final String username, final String password, final String email) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String URL = registerURL;

        StringRequest req = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    Log.d("response", "" + response);
                    if (response.contains("\"status\":\"Success\"")) {
                        Toast.makeText(getApplicationContext(), "Account created successfully! You can log in now.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (response.contains("\"status\":\"Failed\"")) {
                        Toast.makeText(getApplicationContext(), "Account already exists!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                }

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

                //VolleyLog.e("Error: ", verror.getStackTrace());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", pass);
                params.put("email", email);

                return params;
            }
        };

        // add the request object to the queue to be executed
        requestQueue.add(req);


    }

    public void sendToLoginForm(View v) {
        // Start the Login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
