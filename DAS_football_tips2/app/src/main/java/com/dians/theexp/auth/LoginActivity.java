package com.dians.theexp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jane.das_football_tips.MainActivity;
import com.example.jane.das_football_tips.R;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText inputUsername;
    EditText inputPassword;
    Button loginUserBtn;

    String user = null;
    String pass = null;

    String resp = null;
    String loginURL = "http://52.26.249.101/Login/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

            /*HashAuthImpl hashingImpl = new HashAuthImpl(pass);

            if (hashingImpl.checkPass(pass)) {
                pass = hashingImpl.getGeneratedSecuredPasswordHash();
            }*/

            performPOSTRequest(user, pass);
        }
    }

    public void performPOSTRequest(final String username, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String URL = loginURL;

        StringRequest req = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", "" + response);
                    if (response.contains("Ne postoi")) {
                        Toast.makeText(getApplicationContext(), "Wrong username or password!", Toast.LENGTH_SHORT).show();
                    } else if (response.contains("id")) {
                        sendToMain();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError verror) {
                VolleyLog.e("Error: ", verror.getStackTrace());
            }
        }) {
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
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}