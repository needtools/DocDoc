package com.example.docdoc;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.docdoc.async.AsyncQueueRequest;
import com.example.docdoc.database.DBHelper;
import com.example.docdoc.models.Data;
import com.example.docdoc.network.NetworkMonitor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.example.docdoc.MainActivity.isConnected_isWIFI;
import static com.example.docdoc.MainActivity.myURL;
import static com.example.docdoc.MainActivity.personDataFilledFlag;
import static com.example.docdoc.MainActivity.phoneLoginTotal;


public class DataActivity extends AppCompatActivity {
    private NetworkMonitor mNetworkMonitor;
    EditText textTemperature, textHighPreasure, textLowPreasure,  textPulse, textSugar;
    Map<String, String> params;
    String  temperatureHere, systolicHere,diastolicHere,pulseHere, sugarHere;
    Long moment;
    private String LOG_TAG = "dataActivityLog";

    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Toolbar toolbar = findViewById(R.id.toolbarData);
        setSupportActionBar(toolbar);


        mNetworkMonitor = new NetworkMonitor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkMonitor,intentFilter);

        temperatureHere="";
        systolicHere="";
        diastolicHere="";
        pulseHere="";
        sugarHere="";
        personDataFilledFlag=1;

        textTemperature = (EditText) findViewById(R.id.textTemperature);
        textHighPreasure = (EditText) findViewById(R.id.textHighPreasure);
        textLowPreasure = (EditText) findViewById(R.id.textLowPreasure);
        textPulse = (EditText) findViewById(R.id.textPulse);
        textSugar = (EditText) findViewById(R.id.textSugar);

        FloatingActionButton fab = findViewById(R.id.fab_data);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected_isWIFI) {// // ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE
                            doWorkDataActivity();
                }
               else {
                    Toast.makeText(getApplicationContext(),getString(R.string.internet_no), Toast.LENGTH_LONG).show();
                }
            }
        });



    }
    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mNetworkMonitor);

    }
    // +++++++++++++++++++++++++++++++
    public void doWorkDataActivity(){

        // Save data to server and if SUCCSESSFUL save in SQLite database

        temperatureHere=(!(textTemperature.getText().toString().equals("")))?textTemperature.getText().toString():"-";
        systolicHere=(!(textHighPreasure.getText().toString().equals("")))?textHighPreasure.getText().toString():"-";
        diastolicHere=(!(textLowPreasure.getText().toString().equals("")))?textLowPreasure.getText().toString():"-";
        pulseHere=(!(textPulse.getText().toString().equals("")))?textPulse.getText().toString():"-";
        sugarHere=(!(textSugar.getText().toString().equals("")))?textSugar.getText().toString():"-";

        // if even one parameter is not empty
        if((!(temperatureHere.equalsIgnoreCase("-"))  ||  (!(systolicHere.equalsIgnoreCase("-")))  ||
                (!(diastolicHere.equalsIgnoreCase("-")))  ||  (!(pulseHere.equalsIgnoreCase("-")))  ||
                (!(sugarHere.equalsIgnoreCase("-"))))) {


            Data data = new Data();
            moment = System.currentTimeMillis();

            data.setPhone(phoneLoginTotal);
            data.setTime(moment + "");
            data.setTemperature(temperatureHere);
            data.setSystolic(systolicHere);
            data.setDiastolic(diastolicHere);
            data.setPulse(pulseHere);
            data.setSugar(sugarHere);

//                        Log.d(LOG_TAG,"phone = " + data.getPhone() + ", " +
//                                        "time = " + data.getTime() + ", " +
//                                "temperature = " + data.getTemperature()+ ", " +
//                                "systolic = " + data.getSystolic() + ", " +
//                                "diastolic = " +  data.getDiastolic() + ", " +
//                                "pulse = " + data.getPulse() + ", " +
//                                "sugar = " + data.getSugar()
//            );

                Gson gsonData = new GsonBuilder().serializeNulls().create();
                final String requestJsonData = gsonData.toJson(data);

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                saveDataToLocalDB();

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
                        params = new HashMap<>();
                        //add your parameters here as key-value pairs to send to servlet
                        params.put("action", "condition");
                        params.put("data", requestJsonData);//requestJsonData = String for request in servlet
                        return params;
                    }
                };
            AsyncQueueRequest asyncData = new AsyncQueueRequest(this, queue, postRequest);
            asyncData.execute();
            }

    }
    // ++++++++++++++++++++++++++++++++++++++++++
    private void saveDataToLocalDB() {

       DBHelper dbHelper = new DBHelper(this);
        ContentValues cv = new ContentValues();

        cv.put("time", moment);
        cv.put("temperature", temperatureHere);
        cv.put("systolic", systolicHere);
        cv.put("diastolic", diastolicHere);
        cv.put("pulse", pulseHere);
        cv.put("sugar", sugarHere);

        db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            long rowID = db.insert("data", null, cv);
            db.setTransactionSuccessful();
//            Log.d(LOG_TAG, "New data with ID = " + rowID);
        } finally {
            db.endTransaction();
        }

        dbHelper.close();
        go_activity_store();
    }
    // +++++++++++++++++++++++++
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // ++++++++++++++++++++++++++++++++
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_person:
                go_activity_reg();
                break;
            case R.id.menu_store:
                go_activity_store();
                break;
            case R.id.menu_med:
                go_activity_med();
                break;
            case R.id.menu_contacts:
                go_activity_contacts();
                break;
            case R.id.menu_people:
                go_activity_people();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    // ******************************
    public void go_activity_contacts() {
        startActivity(new Intent(this, ContactsActivity.class));
    }
    public void go_activity_people() {
        startActivity(new Intent(this, PeopleActivity.class));
    }
    private void go_activity_med() {
        startActivity(new Intent(this, MedActivity.class));
    }
    public void go_activity_store() {
        startActivity(new Intent(this, StoreActivity.class));
    }
    public void go_activity_reg() {

        startActivity(new Intent(this, PersonActivity.class));
    }

}
