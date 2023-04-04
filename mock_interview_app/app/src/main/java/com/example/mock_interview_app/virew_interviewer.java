
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

public class virew_interviewer extends AppCompatActivity  implements AdapterView.OnItemClickListener {
    SharedPreferences sp;
    String url;
    ArrayList<String> name,email,about,lid;
    ListView l4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virew_interviewer);

        l4=findViewById(R.id.list2);

        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        url ="http://"+sp.getString("ip", "") + ":5000/and_view_interviewers";

        RequestQueue queue = Volley.newRequestQueue(virew_interviewer.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);

                    name= new ArrayList<>();
                    email= new ArrayList<>();

                    about=new ArrayList<>();
                    lid=new ArrayList<>();



                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);

                        name.add(jo.getString("name"));

                        email.add(jo.getString("email")+jo.getString("phone"));
                        about.add(jo.getString("about"));
                        lid.add(jo.getString("lid"));



                    }



                    l4.setAdapter(new custom2(virew_interviewer.this,name,about));

l4.setOnItemClickListener(virew_interviewer.this);

                } catch (Exception e) {
                    Log.d("=========", e.toString());
                    Toast.makeText(virew_interviewer.this, "err"+e, Toast.LENGTH_SHORT).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(virew_interviewer.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();



                return params;
            }
        };
        queue.add(stringRequest);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        SharedPreferences.Editor ed=sp.edit();
        ed.putString("inid",lid.get(i));
        ed.commit();
        startActivity(new Intent(getApplicationContext(),viewtest.class));


    }
}