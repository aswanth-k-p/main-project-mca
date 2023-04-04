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

public class viewtest extends AppCompatActivity implements AdapterView.OnItemClickListener {
    SharedPreferences sp;
    String url;
    ArrayList<String> name,test,description,tid,quid,question,answer;
    ListView l4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtest);


        l4=findViewById(R.id.list2);

        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        url ="http://"+sp.getString("ip", "") + ":5000/and_view_tests";

        RequestQueue queue = Volley.newRequestQueue(viewtest.this);

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


                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);

                        name.add(jo.getString("name"));

                        test.add(jo.getString("test_name"));
                        description.add(jo.getString("description"));
                        tid.add(jo.getString("test_id"));



                    }



                    l4.setAdapter(new custom3(viewtest.this,name,test,description));
                    l4.setOnItemClickListener(viewtest.this);


                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(viewtest.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("int_id",sp.getString("inid",""));
                return params;
            }
        };
        queue.add(stringRequest);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SharedPreferences.Editor ed=sp.edit();
        ed.putString("tid",tid.get(i));
        ed.commit();
        viewscore();

        question();
//        startActivity(new Intent(getApplicationContext(),viewquestion.class));

    }

    private void viewscore() {
        url ="http://"+sp.getString("ip", "") + ":5000/view_score";


        RequestQueue queue = Volley.newRequestQueue(viewtest.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.

                try {
                    JSONObject jo = new JSONObject(response);
                        SharedPreferences.Editor edt = sp.edit();
                        edt.putString("scrid", jo.getString("task"));
                        edt.commit();

                } catch (Exception e) {

                    Toast.makeText(viewtest.this, ""+e, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(viewtest.this, error+"errrrrrrr", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("lid", sp.getString("lid",""));
                params.put("tid", sp.getString("tid",""));

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void question() {
        RequestQueue queue = Volley.newRequestQueue(viewtest.this);
        url ="http://"+sp.getString("ip", "") + ":5000/and_view_questions";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);

                    quid= new ArrayList<>();
                    question= new ArrayList<>();

                    answer=new ArrayList<>();




                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);

                        quid.add(jo.getString("qn_id"));

                        question.add(jo.getString("question"));
                        answer.add(jo.getString("answer"));




                    }



                    if (question.size()!=0) {
                        Intent i=new Intent(getApplicationContext(),MainActivity2.class);
                        i.putStringArrayListExtra("qstn", question);
                        i.putStringArrayListExtra("ans", answer);
                        i.putStringArrayListExtra("qid", quid);
                        startActivity(i);



                    }



                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(viewtest.this, "err"+error, Toast.LENGTH_SHORT).show();
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
}