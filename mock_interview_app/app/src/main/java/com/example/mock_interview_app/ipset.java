package com.example.mock_interview_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ipset extends AppCompatActivity {
    EditText ip;
    Button submit;
    String ip1;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipset);

        ip=findViewById(R.id.uname);
        submit=findViewById(R.id.button6);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ip1=ip.getText().toString();
                if(ip1.equalsIgnoreCase(""))
                {
                    ip.setError("Enter your IP address");
                    ip.requestFocus();
                }
                else {

                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("ip", ip1);
                    ed.commit();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });
    }
    public Boolean checkDateFormat(String date){
        if (date == null || !date.matches("^(1[0-9]|0[1-9]|3[0-1]|2[1-9])-(0[1-9]|1[0-2])-[0-9]{4}$"))
            return false;
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
        try {
            format.parse(date);
            return true;
        }catch (ParseException e){
            return false;
        }
    }

}