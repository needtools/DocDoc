package com.example.docdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.docdoc.database.DBHelper;
import com.example.docdoc.models.Data;
import com.example.docdoc.models.Person;
import com.example.docdoc.network.NetworkMonitor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = "mainActivityLog";
    private NetworkMonitor mNetworkMonitor;
    public static boolean isConnected_isWIFI;
    public static String phoneLoginTotal;
    EditText textLoginPhone, textPassword;
    TextView labelLoginPhone, labelPassword;
    ImageButton imageButtonForget;
    String passwordHere, registration;
    public static Locale localeTotal;
    public static int personDataFilledFlag;
    Map<String, String> params;
    public static SharedPreferences sPrefMainRead, sPrefMedRead;
    Person person;
    FloatingActionButton fab;

    public static final String myURL = "https://docdoc-222504.appspot.com/hello";
    public static final String MainActivityPREFERENCES = "MainActivityStorage";
    public static final String MedActivityPREFERENCES = "MedActivityStorage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        mNetworkMonitor = new NetworkMonitor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkMonitor, intentFilter);
//                 Log.d(LOG_TAG + " #1 ", System.currentTimeMillis() + " isConnected_isWIFI: "+ isChecked );

        phoneLoginTotal = "";// my phone can use in all activities
        personDataFilledFlag = 0;
        localeTotal = getResources().getConfiguration().locale;

        imageButtonForget = (ImageButton) findViewById(R.id.imageButtonForget);


        textPassword = (EditText) findViewById(R.id.textPassword);
        textLoginPhone = (EditText) findViewById(R.id.textLoginPhone);

        labelLoginPhone = (TextView) findViewById(R.id.labelLoginPhone);
        labelPassword = (TextView) findViewById(R.id.labelPassword);

        labelLoginPhone.setTextColor(Color.BLACK);
        labelPassword.setTextColor(Color.BLACK);

        fab = findViewById(R.id.fab_main);

        // Local DBs global
        sPrefMainRead = getSharedPreferences(MainActivityPREFERENCES, Context.MODE_PRIVATE);
        sPrefMedRead = getSharedPreferences(MedActivityPREFERENCES, Context.MODE_PRIVATE);
 /*
        get data from inner DB. If app starts not for the first time the "form" of MainActivity is filling with data
         */
        SharedPreferences.Editor ed1 = sPrefMainRead.edit();
        if (!sPrefMainRead.getString("phone", "").equals("")) {//! - not the absolutly first start of programm
            phoneLoginTotal = sPrefMainRead.getString("phone", "");

            Toast.makeText(getApplicationContext(), getString(R.string.HELLO) + phoneLoginTotal + getString(R.string.WELCOME_BACK), Toast.LENGTH_SHORT).show();// registration for the first time
            go_activity_person();
        } else {

            Toast.makeText(getApplicationContext(), getString(R.string.registration) , Toast.LENGTH_LONG).show();// registration for the first time
        }
        ed1.apply();
        // *******************************************************************
        imageButtonForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected_isWIFI) {// ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE
                    sendRegistrationDataByEmail();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.internet_no), Toast.LENGTH_LONG).show();
                }
            }
        });

// ==========================================================

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected_isWIFI) {// // ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE

                    doWorkMainActivity();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.internet_no), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkMonitor);

    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++
    private void sendRegistrationDataByEmail() {
        final Context context = this;
        final Activity activity = (Activity) context;

        LayoutInflater li = LayoutInflater.from(context);
        View emailView = li.inflate(R.layout.add_email, null);// window

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

        mDialogBuilder.setView(emailView);
        final EditText emailInput = (EditText) emailView.findViewById(R.id.input_email);


        mDialogBuilder
                .setCancelable(true)
                .setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                final String emailSendHere = (!(emailInput.getText().toString().equalsIgnoreCase(""))) ? emailInput.getText().toString() : "";

                                if (!emailSendHere.equalsIgnoreCase("")) {

                                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                    StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    Toast.makeText(getApplicationContext(), response + " " + emailSendHere, Toast.LENGTH_LONG).show();
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    // error
                                                    Toast.makeText(getApplicationContext(), getString(R.string.UPS), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    ) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            params = new HashMap<>();
                                            //add your parameters here as key-value pairs to send to servlet
                                            params.put("action", "senddatatouseremail");
                                            params.put("useremail", emailSendHere);//requestJsonPerson = String for request in servlet
                                            params.put("locale", localeTotal.toString());
                                            return params;
                                        }
                                    };

                                    AsyncQueueRequest asyncMainEmail = new AsyncQueueRequest(activity, queue, postRequest);
                                    asyncMainEmail.execute();

                                }

                            }
                        })
                .setNegativeButton(getString(R.string.y_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private void doWorkMainActivity() {

        phoneLoginTotal = textLoginPhone.getText().toString();

        if (phoneLoginTotal.equals("")) {
            labelLoginPhone.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), getString(R.string.PHONE_LOGIN) + getString(R.string.UPS), Toast.LENGTH_LONG).show();
        } else if (phoneLoginTotal.contains(",")) {
            labelLoginPhone.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), getString(R.string.PHONE_LOGIN)+ getString(R.string.CONTAINS)+ "\",\" !", Toast.LENGTH_LONG).show();
        } else if (phoneLoginTotal.contains("~")) {
            labelLoginPhone.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), getString(R.string.PHONE_LOGIN)+ getString(R.string.CONTAINS)+"\"~\" !", Toast.LENGTH_LONG).show();
        }

        passwordHere = textPassword.getText().toString();
        if (passwordHere.equals("")) {
            labelPassword.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), getString(R.string.PASSWORD) + getString(R.string.UPS), Toast.LENGTH_LONG).show();
        }

        if (!phoneLoginTotal.equals("") && !passwordHere.equals("") && !phoneLoginTotal.contains(",")
                && !phoneLoginTotal.contains("~")) {

            person = new Person();

            List<String> contacts = new ArrayList<>();
            contacts.add(null);// doesn't need now

//        person.setCryptId(cryptIdTotal);
            person.setPhone(phoneLoginTotal);
            person.setPassword(passwordHere);
//            person.setEmail(emailHere);
//            person.setName("");
//            person.setBirthday("");
//            person.setGender("");
            person.setZcontacts(contacts);// does not need now - empty

            // Save data to server and if SUCCSESSFUL

            Gson gsonPerson = new GsonBuilder().serializeNulls().create();
            final String requestJsonPerson = gsonPerson.toJson(person);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("OK")) {
                                savePhoneToLocalDB();
                            } else if (response.equals("PHONE")) {
                                labelLoginPhone.setTextColor(Color.RED);
                                Toast.makeText(getApplicationContext(), getString(R.string.PHONE_LOGIN) + getString(R.string.UPS), Toast.LENGTH_LONG).show();
                            } else {
                                restoreOldPersonDataToLocalDB(response);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), getString(R.string.ERROR) + getString(R.string.UPS), Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    params = new HashMap<>();
                    //add your parameters here as key-value pairs to send to servlet
                    params.put("action", "registration");
                    params.put("person", requestJsonPerson);//requestJsonPerson = String for request in servlet
                    params.put("locale", localeTotal.toString());
                    return params;
                }
            };

            AsyncQueueRequest asyncMain = new AsyncQueueRequest(this, queue, postRequest);
            asyncMain.execute();

        }

    }

    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    private void  savePhoneToLocalDB(){
        // SAVE person to local DB --> key-value can be changed but not cryptId
        SharedPreferences.Editor  ed2 = sPrefMainRead.edit();

        ed2.putString("phone", phoneLoginTotal);
        ed2.apply();

        go_activity_person();
    }
    // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

    private void go_activity_person() {
        startActivity(new Intent(this, PersonActivity.class));
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++
    private void restoreOldPersonDataToLocalDB(String response) {
// restoration of person local DB
        Gson gson = new Gson();
        Person person = gson.fromJson(response, Person.class);

        SharedPreferences.Editor ed3 = sPrefMainRead.edit();
        phoneLoginTotal =  person.getPhone();
        ed3.putString("phone", phoneLoginTotal);

        if (!person.getEmail().equals(""))
            ed3.putString("email", person.getEmail());
        if (!person.getName().equals(""))
            ed3.putString("name", person.getName());
        if (!person.getGender().equals(""))
            ed3.putString("gender", person.getGender());
        if (!person.getBirthday().equals(""))
             ed3.putString("birthday", person.getBirthday());

             ed3.apply();

            textLoginPhone.setText(phoneLoginTotal);

        getOldHelthDataFromServer();

    }
// +++++++++++++++++++++++++++++++++++++++++++++++++
    private void getOldHelthDataFromServer() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       fillOldPersonHeathDataToLocalSQLite(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), getString(R.string.ERROR)+getString(R.string.UPS), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                params = new HashMap<>();
                //add your parameters here as key-value pairs to send to servlet
                params.put("action", "peopledata");
                params.put("patientphone", phoneLoginTotal);
                return params;
            }
        };
        AsyncQueueRequest asyncOldData = new AsyncQueueRequest(this, queue, postRequest);
        asyncOldData.execute();

    }
// ++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void fillOldPersonHeathDataToLocalSQLite(String response) {
        Gson gson = new Gson();
        Type personType = new TypeToken<ArrayList<Data>>(){}.getType();
        ArrayList<Data> al= gson.fromJson(response, personType);

        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(this);
        ContentValues cv = new ContentValues();

        for(Data d : al){
//            Log.d(LOG_TAG,"phone = " + d.getPhone() + ", " +
//                    "time = " + d.getTime() + ", " +
//                    "temperature = " + d.getTemperature()+ ", " +
//                    "systolic = " + d.getSystolic() + ", " +
//                    "diastolic = " +  d.getDiastolic() + ", " +
//                    "pulse = " + d.getPulse() + ", " +
//                    "sugar = " + d.getSugar()
//            );

        cv.put("time", d.getTime());
        cv.put("temperature", d.getTemperature());
        cv.put("systolic", d.getSystolic());
        cv.put("diastolic", d.getDiastolic());
        cv.put("pulse", d.getPulse());
        cv.put("sugar", d.getSugar());

        db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            long rowID = db.insert("data", null, cv);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        }
        dbHelper.close();
        go_activity_person();
    }

}
// +++++++++++++++++++++++++++++++++++++++++++++++++
//    private String replaceSymbols(String stringHere) {
//        stringHere = stringHere.replace(",", "&cedil;");
//        stringHere = stringHere.replace("\"", "&quot;");
//        stringHere = stringHere.replace("~", "&uml;");
//        return stringHere;
//
//}
//    // +++++++++++++++++++++++++++++++++++++++++++++++++
//    private String  returnSymbols(String stringHere) {
//        stringHere = stringHere.replace("&cedil;", ",");
//        stringHere = stringHere.replace("&uml;", "~");
//        return stringHere;
//    }
