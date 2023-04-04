package com.example.mock_interview_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class register extends AppCompatActivity {
    EditText firstname,phone,email,uname,pwd,dob;

    Button b1;
    String fname,ph,email1,username,password,dd;
    String url="";
    String ip="";
    SharedPreferences sh;



    DatePickerDialog datepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstname=findViewById(R.id.fname);


        phone=findViewById(R.id.Phone);


        email=findViewById(R.id.email);
        uname=findViewById(R.id.uname);
        pwd=findViewById(R.id.pwd);

        dob=findViewById(R.id.dob);


        b1=findViewById(R.id.button6);


        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ip=sh.getString("ip","");


         url="http://"+ip+":5000/register";



        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datepicker = new DatePickerDialog(register.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, year, month, day);
                datepicker.show();
            }
        });






        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname=firstname.getText().toString();


                ph=phone.getText().toString();


                email1=email.getText().toString();
                username=uname.getText().toString();
                password=pwd.getText().toString();
                dd=dob.getText().toString();

                final Calendar cldr = Calendar.getInstance();
                int year = cldr.get(Calendar.YEAR);




                if (fname.equalsIgnoreCase("")) {
                    firstname.setError("Enter Your First Name");
                    firstname.requestFocus();
                } else if (!fname.matches("^[a-zA-Z ]*$")) {
                    firstname.setError("Only characters allowed");
                    firstname.requestFocus();
                }

                 else if (dd.equalsIgnoreCase("")) {
                    dob.setError("Enter Your Dob");
                    dob.requestFocus();
                }

                else if (ph.equalsIgnoreCase("")) {
                    phone.setError("Enter Your Phone No");
                    phone.requestFocus();
                } else if (ph.length()!=10) {
                    phone.setError("Minimum 10 nos required");
                    phone.requestFocus();

                }


                else if (email1.equalsIgnoreCase("")) {
                    email.setError("Enter Your Email");
                    email.requestFocus();
                } else if (username.equalsIgnoreCase("")) {
                    uname.setError("Enter Your username");
                    uname.requestFocus();
                }   else if(password.equalsIgnoreCase("")) {
                    pwd.setError("Enter Your Password");
                    pwd.requestFocus();
                }
                else {


                        String dates[] = dd.split("-");

                        if (Integer.parseInt(dates[2]) > year) {
                            Toast.makeText(getApplicationContext(), "invalid date", Toast.LENGTH_SHORT).show();
                        } else {

                            // Instantiate the RequestQueue.
                            RequestQueue queue = Volley.newRequestQueue(register.this);

                            // Request a string response from the provided URL.
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the response string.

                                    try {
                                        JSONObject jo = new JSONObject(response);
                                        String status = jo.getString("task");

                                        if (status.equalsIgnoreCase("success")) {
                                            Toast.makeText(getApplicationContext(), "Registered ", Toast.LENGTH_SHORT).show();
//
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));


                                        } else {
                                            Toast.makeText(register.this, "Already exist", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {

                                        Toast.makeText(register.this, "error" + e, Toast.LENGTH_SHORT).show();

                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "qwert "+error, Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("username", username);
                                    params.put("password", password);
                                    params.put("name", fname);


                                    params.put("phone", ph);
                                    params.put("dob", dd);


                                    params.put("email", email1);


                                    return params;
                                }
                            };
                            // Add the request to the RequestQueue.
                            queue.add(stringRequest);
                        }


                }

            }
        });


    }
    public Boolean checkDateFormat(String date){
        if (date == null || !date.matches("^(1[0-9]|0[1-9]|3[0-1]|2[1-9])-(0[1-9]|1[0-2])-[0-9]{4}$"))
            return false;
        SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy");
        try {
            format.parse(date);
            return true;
        }catch (ParseException e){
            return false;
        }
    }







}