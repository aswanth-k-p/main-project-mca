package com.example.mock_interview_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
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

public class viewresult extends AppCompatActivity {
    SharedPreferences sp;
    String url;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewresult);
        imageView=findViewById(R.id.gallery_item_image);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        url ="http://"+sp.getString("ip", "") + ":5000/viewresult";

        RequestQueue queue = Volley.newRequestQueue(viewresult.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {


                    JSONObject jo = new JSONObject(response);
                    String status = jo.getString("task");




                    if(android.os.Build.VERSION.SDK_INT>9)
                    {
                        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    java.net.URL thumb_u;
                    try {

                        //thumb_u = new java.net.URL("http://192.168.43.57:5000/static/photo/flyer.jpg");

                        thumb_u = new java.net.URL("http://"+sp.getString("ip","")+":5000/static/pltgraph/"+status);
                        Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                        imageView.setImageDrawable(thumb_d);

                    }
                    catch (Exception e)
                    {
                        Log.d("errsssssssssssss",""+e);
                    }




                } catch (Exception e) {
                    Log.d("=========", e.toString());
                    Toast.makeText(viewresult.this, "err"+e, Toast.LENGTH_SHORT).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(viewresult.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("tid",sp.getString("tid",""));
                params.put("lid",sp.getString("lid",""));

                return params;
            }
        };
        queue.add(stringRequest);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }
    public void onBackPressed() {
        // TODO Auto-generated method stub

        startActivity(new Intent(getApplicationContext(),home.class));
    }

}