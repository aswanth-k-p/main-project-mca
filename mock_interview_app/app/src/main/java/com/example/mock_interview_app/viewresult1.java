package com.example.mock_interview_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class viewresult1 extends AppCompatActivity {
    SharedPreferences sp;
    String url;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView imageView;
    ArrayList<String> imge;
    ListView l1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewresult1);
        imageView=findViewById(R.id.gallery_item_image);
        l1=findViewById(R.id.list2);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        url ="http://"+sp.getString("ip", "") + ":5000/viewresult1";

        RequestQueue queue = Volley.newRequestQueue(viewresult1.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);

                    imge= new ArrayList<>();



                    for(int i=0;i<ar.length();i++) {
                        JSONObject jo = ar.getJSONObject(i);


                        imge.add(jo.getString("image"));
                    }

                    l1.setAdapter(new customimg(viewresult1.this,imge));




                } catch (Exception e) {
                    Log.d("=========", e.toString());
                    Toast.makeText(viewresult1.this, "err"+e, Toast.LENGTH_SHORT).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(viewresult1.this, "err"+error, Toast.LENGTH_SHORT).show();
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



    public void onBackPressed() {
        // TODO Auto-generated method stub

        startActivity(new Intent(getApplicationContext(),home.class));
    }

}