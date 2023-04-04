package com.example.mock_interview_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class viewquestion extends AppCompatActivity implements AdapterView.OnItemClickListener {
    SharedPreferences sp;
    String url;
    ArrayList<String> qus,qid;
    ListView l4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewquestion);

        l4=findViewById(R.id.list2);

        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        url ="http://"+sp.getString("ip", "") + ":5000/and_view_questions";

        RequestQueue queue = Volley.newRequestQueue(viewquestion.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);

                    qus= new ArrayList<>();
                    qid= new ArrayList<>();





                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);

                        qus.add(jo.getString("question"));
                        qid.add(jo.getString("qn_id"));



                    }



                    l4.setAdapter(new custom2(viewquestion.this,qus,qid));
                    l4.setOnItemClickListener(viewquestion.this);



                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(viewquestion.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tid",sp.getString("tid",""));



                return params;
            }
        };
        queue.add(stringRequest);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//
//        SharedPreferences.Editor ed=sp.edit();
//        ed.putString("qid",qid.get(i));
//        ed.commit();
//        startActivity(new Intent(getApplicationContext(),MainActivity2.class));

    }
}