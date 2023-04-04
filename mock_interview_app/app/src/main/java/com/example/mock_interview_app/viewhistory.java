package com.example.mock_interview_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class viewhistory extends AppCompatActivity implements AdapterView.OnItemClickListener {
    SharedPreferences sp;
    String url;
    ArrayList<String> name,test,description,tid,ttid,question,answer;
    ListView l4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewhistory);

        l4=findViewById(R.id.list);

        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        url ="http://"+sp.getString("ip", "") + ":5000/and_view_tests1";

        RequestQueue queue = Volley.newRequestQueue(viewhistory.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);

                    name= new ArrayList<>();
                    test= new ArrayList<>();

                    description=new ArrayList<>();

                    tid=new ArrayList<>();

                    ttid=new ArrayList<>();

                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);



                        test.add(jo.getString("test_name"));
                        description.add(jo.getString("date"));
                        tid.add(jo.getString("name"));

                        ttid.add(jo.getString("test_id"));


                    }



                    l4.setAdapter(new custom3(viewhistory.this,tid,test,description));
                    l4.setOnItemClickListener(viewhistory.this);


                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(viewhistory.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("lid",sp.getString("lid",""));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {




        AlertDialog.Builder ald=new AlertDialog.Builder(viewhistory.this);
        ald.setTitle("Details")
                .setPositiveButton(" View Result ", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try
                        {


                            SharedPreferences.Editor ed=sp.edit();
                            ed.putString("tid",ttid.get(i));
                            ed.putString("dt",description.get(i));

                            ed.commit();
                            startActivity(new Intent(getApplicationContext(),viewresult1.class));



                        }
                        catch(Exception e)
                        {
                            Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setNegativeButton(" View Details ", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        SharedPreferences.Editor ed=sp.edit();
                        ed.putString("tid",ttid.get(i));
                        ed.putString("dt",description.get(i));

                        ed.commit();
                        startActivity(new Intent(getApplicationContext(),view_details.class));


                    }
                });

        AlertDialog al=ald.create();
        al.show();







    }
}