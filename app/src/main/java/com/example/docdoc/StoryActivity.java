package com.example.docdoc;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
//import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.docdoc.async.AsyncQueueRequest;
import com.example.docdoc.locales.TimeLocales;
import com.example.docdoc.models.Data;
import com.example.docdoc.network.NetworkMonitor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.example.docdoc.MainActivity.isConnected_isWIFI;
import static com.example.docdoc.MainActivity.localeTotal;
import static com.example.docdoc.MainActivity.myURL;

public class StoryActivity extends AppCompatActivity {
    private String LOG_TAG = "storyActivityLog";
    TableLayout tableStoryImages, tableStoryData;
    LinearLayout linearLayoutBottom;
    TableRow trTemp;
    private NetworkMonitor mNetworkMonitor;
    String fPhone;
    Map<String, String> params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_story);
        Toolbar toolbar = findViewById(R.id.toolbarStory);
        setSupportActionBar(toolbar);

        tableStoryImages = (TableLayout) findViewById(R.id.tableImages);
        tableStoryImages.setStretchAllColumns(true);

        linearLayoutBottom = (LinearLayout) findViewById(R.id.linearLayoutBottom);

        tableStoryData = (TableLayout) findViewById(R.id.tableData);

        FloatingActionButton fab = findViewById(R.id.fab_story);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_activity_people();
            }
        });

        Intent intent = getIntent();
        fPhone = intent.getStringExtra("patientPhone");
        String lInfo = intent.getStringExtra("patientInfo");

        setTitle(lInfo);

//        Log.d(LOG_TAG + " #12 ", System.currentTimeMillis() +" ~ "+ fPhone+" > "+ lInfo);

        mNetworkMonitor = new NetworkMonitor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkMonitor, intentFilter);

        if (isConnected_isWIFI == true) {// ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE
            doWorkStortActivity(fPhone);
        }
        else {
            Toast.makeText(getApplicationContext(),getString(R.string.internet_no), Toast.LENGTH_LONG).show();
        }

    }
    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mNetworkMonitor);

    }
    // ++++++++++++++++++++++++++++++++++++++++++
    private void doWorkStortActivity(final String fPhone) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.d(LOG_TAG + " #122 ", System.currentTimeMillis() +" "+  response);
                            fillPatientData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), getString(R.string.UPS), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                params = new HashMap<String, String>();
                //add your parameters here as key-value pairs to send to servlet
                params.put("action", "peopledata");
                params.put("patientphone", fPhone);
                return params;
            }
        };

        AsyncQueueRequest asyncStory = new AsyncQueueRequest(this, queue, postRequest);
        asyncStory.execute();
    }
    // ++++++++++++++++++++++++++++++++++++++++++
    private void fillPatientData(String response) {
        // make list of peoples from response of server
        Gson gson = new Gson();
        Type personType = new TypeToken<ArrayList<Data>>(){}.getType();
        ArrayList<Data> al= gson.fromJson(response, personType);
        Collections.reverse(al);
        for(Data d : al){
//            Log.d(LOG_TAG,"phone = " + d.getPhone() + ", " +
//                    "time = " + d.getTime() + ", " +
//                    "temperature = " + d.getTemperature()+ ", " +
//                    "systolic = " + d.getSystolic() + ", " +
//                    "diastolic = " +  d.getDiastolic() + ", " +
//                    "pulse = " + d.getPulse() + ", " +
//                    "sugar = " + d.getSugar()
//            );
            trTemp = new TableRow(this);
            trTemp.setPadding(1,5,1, 5);

            TextView patientTime = new TextView(this);
            patientTime.setTextSize(10);
            patientTime.setTextColor(Color.BLACK);
            patientTime.setGravity(Gravity.LEFT);
            patientTime.setMinHeight(60);
            patientTime.setTypeface(null, Typeface.BOLD);

            TextView patientSystolic= new TextView(this);
            patientSystolic.setTextSize(16);
            patientSystolic.setTextColor(Color.BLACK);
            patientSystolic.setGravity(Gravity.LEFT);

            TextView patientDiastolic= new TextView(this);
            patientDiastolic.setTextSize(16);
            patientDiastolic.setTextColor(Color.BLACK);
            patientDiastolic.setGravity(Gravity.LEFT);

            TextView patientTemperature= new TextView(this);
            patientTemperature.setTextSize(16);
            patientTemperature.setTextColor(Color.BLACK);
            patientTemperature.setGravity(Gravity.LEFT);

            TextView patientSugar= new TextView(this);
            patientSugar.setTextSize(16);
            patientSugar.setTextColor(Color.BLACK);
            patientSugar.setGravity(Gravity.LEFT);

            TextView patientPulse= new TextView(this);
            patientPulse.setTextSize(16);
            patientPulse.setTextColor(Color.BLACK);
            patientPulse.setGravity(Gravity.LEFT);

            patientTime.setText((CharSequence) getDataFromTime(d.getTime()));
            patientTemperature.setText(d.getTemperature());
            patientSystolic.setText(d.getSystolic());
            patientDiastolic.setText(d.getDiastolic());
            patientPulse.setText(d.getPulse());
            patientSugar.setText(d.getSugar());

            trTemp.addView(patientTime);
            trTemp.addView(patientTemperature);
            trTemp.addView(patientSystolic);
            trTemp.addView(patientDiastolic);
            trTemp.addView(patientPulse);
            trTemp.addView(patientSugar);

            tableStoryData.addView(trTemp);
        }

    }

    // ++++++++++++++++++++++++++++++++++++++++++
    private void go_activity_people() {
        startActivity(new Intent(this, PeopleActivity.class));
    }
    // ++++++++++++++++++++++++++++++++++++++++++

    private String getDataFromTime(String substring) {
        Long dateData = Long.parseLong(substring);

        TimeLocales tl = new TimeLocales(localeTotal.toString(), dateData);// ru_RU
        String formatTimeLocale = tl.timeformated;// 30.05.76

        return formatTimeLocale;
    }

}
