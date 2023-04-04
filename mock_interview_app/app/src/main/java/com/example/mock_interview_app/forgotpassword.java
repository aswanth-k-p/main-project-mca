package com.example.mock_interview_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class forgotpassword extends AppCompatActivity {
    EditText e1;
    SharedPreferences sp;
    String url,ip,email;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        b1=findViewById(R.id.button);
        e1=findViewById(R.id.editTextTextPersonName);


        url="http://"+sp.getString("ip","")+":5000/forgot";

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            email=e1.getText().toString();
            if(email.equalsIgnoreCase(""))
            {
                e1.setError("enter your email address");
                e1.requestFocus();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                e1.setError("enter   valid email");
                e1.requestFocus();

            }
else {


                RequestQueue queue = Volley.newRequestQueue(forgotpassword.this);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.

                        try {
                            JSONObject jo = new JSONObject(response);
                            String status = jo.getString("task");
                            Toast.makeText(forgotpassword.this, ""+status, Toast.LENGTH_SHORT).show();
Intent ik=new Intent(getApplicationContext(),MainActivity.class);
startActivity(ik);

                        } catch (Exception e) {

                            Toast.makeText(forgotpassword.this, ""+e, Toast.LENGTH_SHORT).show();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(forgotpassword.this, error+"errrrrrrr", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", email);


                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
            }
        });

    }
}