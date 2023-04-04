package com.example.mock_interview_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class view_details extends AppCompatActivity {
    SharedPreferences sp;
    String url;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView imageView;
    ArrayList<String> question,answer,emotion,gramer;
    ListView l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        l1=findViewById(R.id.lv1);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        url ="http://"+sp.getString("ip", "") + ":5000/viewresult2";

        RequestQueue queue = Volley.newRequestQueue(view_details.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);

                    question = new ArrayList<>();
                    answer = new ArrayList<>();
                    emotion = new ArrayList<>();
                    gramer= new ArrayList<>();



                    for(int i=0;i<ar.length();i++) {
                        JSONObject jo = ar.getJSONObject(i);


                        question.add(jo.getString("question"));
                        answer.add(jo.getString("ans"));
                        emotion.add(jo.getString("emotion"));
                        gramer.add(jo.getString("details"));
                    }

                    l1.setAdapter(new custom_details(view_details.this,question,answer,emotion,gramer));




                } catch (Exception e) {
                    Log.d("=========", e.toString());
                    Toast.makeText(view_details.this, "err"+e, Toast.LENGTH_SHORT).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(view_details.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("tid",sp.getString("tid",""));
                params.put("lid",sp.getString("lid",""));
                params.put("dt",sp.getString("dt",""));

                return params;
            }
        };
        queue.add(stringRequest);





    }
}