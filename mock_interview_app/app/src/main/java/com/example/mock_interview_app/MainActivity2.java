package com.example.mock_interview_app;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity2 extends Activity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    SharedPreferences sh;
    String url;
    ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;
    static final int DIALOG_DOWNLOAD_PROGRESS = 2;
    private TextView startTV, stopTV, statusTV,t1;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static String mFileName = null,filename=null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    byte[] filedt=null;
    byte[] filedt1=null;
    String result=" ";
    TextView b1,t2;

    String rid="",ss="";
    int index=0,mark=0;
    float mrk;
    public static ArrayList<String> qstn;

    public static ArrayList<String> ans,qid;
    TextToSpeech ttobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy polphotouploadicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(polphotouploadicy);
        }


        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        statusTV = findViewById(R.id.idTVstatus);
        startTV = findViewById(R.id.btnRecord);
        stopTV = findViewById(R.id.btnStop);



        t2 = findViewById(R.id.txthead);

        qstn=getIntent().getStringArrayListExtra("qstn");
        ans=getIntent().getStringArrayListExtra("ans");
        qid=getIntent().getStringArrayListExtra("qid");

        t2.setText(qstn.get(index));
        Toast.makeText(getApplicationContext(),qstn.get(index),Toast.LENGTH_LONG).show();

        ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                ttobj.setLanguage(Locale.UK);
                ttobj.setPitch((float) .6);
                ttobj.speak("The question is  "+qstn.get(index), TextToSpeech.QUEUE_FLUSH, null);
                startService(new Intent(getApplicationContext(),Serv.class));

            }
        });



        startTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRecording();
            }
        });
        stopTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseRecording();
                filedt1 = getbyteData(filename);
                String answer=ans.get(index);
                SharedPreferences.Editor ed=sh.edit();
                ed.putString("ans",answer);
                ed.putString("qid",qid.get(index));

                ed.commit();

                filedt = getbyteData(mFileName);
                Log.d("filedataaa", filedt + "");
                url = "http://"+sh.getString("ip","")+":5000/voice";
                runthread(mFileName,filename);


//                uploadFile();
            }
        });
    }

    private void runthread(String mFileName, String filename) {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog pd;

                        pd=new ProgressDialog(MainActivity2.this);
       pd.setMessage("Uploading....");
     pd.show();


                                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response1) {
//                        Toast.makeText(MainActivity2.this, "/////////"+response1.data + "rsessssssp", Toast.LENGTH_LONG).show();



                            try {




                                JSONObject obj = new JSONObject(new String(response1.data));
                                result = obj.getString("task");
                                if (!result.equals(""))
                                {
                                    pd.dismiss();
                                    mrk += new Float(result);
                                    SharedPreferences.Editor ed = sh.edit();
                                    ed.putString("mark", mrk + "");
                                    ed.commit();
                                    Toast.makeText(MainActivity2.this, result + "resulttt", Toast.LENGTH_LONG).show();
                                    index = index + 1;
                                    if ((qstn.size() + "").equalsIgnoreCase(index + "")) {
                                        new Insert1().execute();
                                    } else {
                                        qstn = getIntent().getStringArrayListExtra("qstn");
                                        ttobj.speak("The question is  " + qstn.get(index), TextToSpeech.QUEUE_FLUSH, null);
                                        t2.setText(qstn.get(index));
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "errr"+error, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mp = new HashMap<>();

                mp.put("qid",sh.getString("qid",""));
                mp.put("tid",sh.getString("tid",""));

                mp.put("lid",sh.getString("lid",""));

                mp.put("scid",sh.getString("scrid",""));



                return mp;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<String, DataPart>();
                long imagename = System.currentTimeMillis();
                params.put("file1", new DataPart(filename, filedt1 ));
                params.put("file", new DataPart(mFileName, filedt ));
                return params;
            }
        };
                        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                        queue.add(stringRequest);

                Volley.newRequestQueue(getApplicationContext()).add(volleyMultipartRequest);



                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading File...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
        }
        return null;
    }

    private void startRecording() {
        // check permission method is used to check
        // that the user has granted permission
        // to record nd store the audio.
        if (CheckPermissions()) {

            // setbackgroundcolor method will change
            // the background color of text view.
//            stopTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
            startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//            playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//            stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));

            // we are here initializing our filename variable
            // with the path of the recorded audio file.
             filename=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+sh.getString("fl","");


            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/AudioRecording.3gp";

            // below method is used to initialize
            // the media recorder clss
            mRecorder = new MediaRecorder();

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            Intent intent
//                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
//                    Locale.getDefault());
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
//
//            try {
//                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//            }
//            catch (Exception e) {
//                Toast
//                        .makeText(MainActivity.this, " " + e.getMessage(),
//                                Toast.LENGTH_SHORT)
//                        .show();
//            }
            // below method is used to set
            // the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            // below method is used to set the
            // audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder.setOutputFile(mFileName);

            try {
                // below mwthod will prepare
                // our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start
            // the audio recording.
            mRecorder.start();
            statusTV.setText("Recording Started");
        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(MainActivity2.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


    public void playAudio() {
        stopTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));

        // for playing our recorded audio
        // we are using media player class.
        mPlayer = new MediaPlayer();
        try {
            // below method is used to set the
            // data source which will be our file name
            mPlayer.setDataSource(mFileName);

            // below method will prepare our media player
            mPlayer.prepare();

            // below method will start our media player.
            mPlayer.start();
            statusTV.setText("Recording Started Playing");
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    public void pauseRecording() {
        stopTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));

        // below method will stop
        // the audio recording.
        statusTV.setText("Recording Stoped");

        mRecorder.stop();

        // below me
        // thod will release
        // the media recorder class.
        mRecorder.release();
        mRecorder = null;





    }
    ProgressDialog pd;

//    private void uploadFile(String mFileName, String filepath) {
//        pd=new ProgressDialog(MainActivity2.this);
//        pd.setMessage("Uploading....");
//        pd.show();
//        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
//                new Response.Listener<NetworkResponse>() {
//                    @Override
//                    public void onResponse(NetworkResponse response1) {
//                        Toast.makeText(MainActivity2.this, "/////////"+response1.data + "rsessssssp", Toast.LENGTH_LONG).show();
//
//                        if(!response1.data.equals("")) {
//                            pd.dismiss();
//
//
//                            String x = new String(response1.data);
//                            Toast.makeText(MainActivity2.this, response1.data + "", Toast.LENGTH_LONG).show();
//
//
//                            try {
//                                JSONObject obj = new JSONObject(new String(response1.data));
//                                result = obj.getString("task");
//                                mrk+= new Float(result);
//                                SharedPreferences.Editor ed= sh.edit();
//                                ed.putString("mark",mrk+"");
//                                ed.commit();
//                                Toast.makeText(MainActivity2.this, result+"resulttt", Toast.LENGTH_LONG).show();
//                                index = index + 1;
//                                if ((qstn.size() + "").equalsIgnoreCase(index + "")) {
//                                    new Insert1().execute();
//                                } else {
//                                    qstn = getIntent().getStringArrayListExtra("qstn");
//                                    ttobj.speak("The question is  " + qstn.get(index), TextToSpeech.QUEUE_FLUSH, null);
//                                    t2.setText(qstn.get(index));
//                                }
//                            } catch (Exception e) {
//                                Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> mp = new HashMap<>();
//
//                mp.put("qid",sh.getString("qid",""));
//                mp.put("tid",sh.getString("tid",""));
//
//                mp.put("lid",sh.getString("lid",""));
//
//                mp.put("scid",sh.getString("scrid",""));
//
//
//
//                return mp;
//            }
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<String, DataPart>();
//                long imagename = System.currentTimeMillis();
//                params.put("file1", new DataPart(filename, filedt1 ));
//                params.put("file", new DataPart(mFileName, filedt ));
//                return params;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(volleyMultipartRequest);
//
//    }

    private byte[] getbyteData(String pathHolder) {
        Log.d("path", pathHolder);
        File fil = new File(pathHolder);
        int fln = (int) fil.length();
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(fil);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[fln];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byteArray = bos.toByteArray();
            inputStream.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e+"", Toast.LENGTH_LONG).show();
        }
        return byteArray;




    }

    public void pausePlaying() {
        // this method will release the media player
        // class and pause the playing of our recorded audio.
        mPlayer.release();
        mPlayer = null;
        stopTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        statusTV.setText("Recording Play Stopped");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                statusTV.setText(
                        Objects.requireNonNull(result).get(0));
            }
        }
    }


    class Insert1 extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args)
        {
            RequestQueue queue = Volley.newRequestQueue(MainActivity2.this);
          String  ur="http://"+sh.getString("ip","")+":5000/mark";
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ur,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the response string.
                    Log.d("+++++++++++++++++",response);

                    try {
                        JSONObject json=new JSONObject(response);
                        ss=json.getString("task");

                        if(ss.equalsIgnoreCase("invalid"))
                        {
                            Toast.makeText(getApplicationContext(),"invalid",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
//
//                            publishProgress(ss);
                            Intent i=new Intent(getApplicationContext(),viewresult.class);
                            startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    Toast.makeText(getApplicationContext(),"invalid",Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();

                    params.put("scid",sh.getString("scrid",""));
                    params.put("mark",sh.getString("mark",""));

                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

            return ss;

        }

        @Override
        protected void onProgressUpdate(String... values) {
//        	e.setText(values[0]);
            // TODO Auto-generated method stub, text, duration)
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            // pDialog.dismiss();



            Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_LONG).show();


        }

    }

}