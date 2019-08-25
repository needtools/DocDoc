package com.example.docdoc;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.docdoc.models.Person;
import com.example.docdoc.network.NetworkMonitor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.docdoc.MainActivity.MainActivityPREFERENCES;
import static com.example.docdoc.MainActivity.isConnected_isWIFI;
import static com.example.docdoc.MainActivity.myURL;
import static com.example.docdoc.MainActivity.personDataFilledFlag;
import static com.example.docdoc.MainActivity.phoneLoginTotal;
import static com.example.docdoc.MainActivity.sPrefMainRead;


public class PersonActivity extends AppCompatActivity {
    private NetworkMonitor mNetworkMonitor;
    EditText textNameSurname, textEmail;
    TextView labelGender, labelNameSurname, nameSurnamePerson, labelEmail;
    RadioButton radioMale, radioFemale;
    RadioGroup radioGroupGender;
    String checkedGender, birthdayDate;
    DatePicker datePicker;
    Person person;
    SharedPreferences.Editor ed1, ed2;
    Map<String, String> params;
    private String LOG_TAG = "personActivityLog";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Toolbar toolbar = findViewById(R.id.toolbarPerson);
        setSupportActionBar(toolbar);

        mNetworkMonitor = new NetworkMonitor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkMonitor,intentFilter);

        checkedGender="";

        labelEmail = (TextView)findViewById(R.id.labelEmail);
        textEmail = (EditText) findViewById(R.id.textEmail);
        textNameSurname = (EditText) findViewById(R.id.textNameSurname);
        labelGender = (TextView)findViewById(R.id.labelGender);
        labelNameSurname = (TextView)findViewById(R.id.labelNameSurname);
        nameSurnamePerson = (TextView)findViewById(R.id.nameSurnamePerson);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_person);

        labelGender.setTextColor(Color.BLACK);
        labelNameSurname.setTextColor(Color.BLACK);
        labelEmail.setTextColor(Color.BLACK);
        // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
        /*
        get data from inner DB. If app starts not for the first time the "form" of MainActivity is filling with data
         */

        ed1 = sPrefMainRead.edit();
        // phone/login of user

        nameSurnamePerson.setText(phoneLoginTotal);

        // GET data from local DB
        String nameSurnameHere = (!sPrefMainRead.getString("name", "").equals("")) ?
                sPrefMainRead.getString("name", "") : "";
        textNameSurname.setText(nameSurnameHere);

        String emailTakeHere = (!sPrefMainRead.getString("email", "").equals("")) ?
                sPrefMainRead.getString("email", "") : "";
        textEmail.setText(emailTakeHere);

        String yearMonthDay = (!sPrefMainRead.getString("birthday", "").equals("")) ?
                sPrefMainRead.getString("birthday", "") : "";
        if (!yearMonthDay.equalsIgnoreCase("")) {
            String[] yearMonthDayBirth = yearMonthDay.split("/");
            int yearThen = Integer.parseInt(yearMonthDayBirth[2]);
            int monthThen = Integer.parseInt(yearMonthDayBirth[0]) - 1;
            int dayThen = Integer.parseInt(yearMonthDayBirth[1]);// Locale.US

            Calendar cal = Calendar.getInstance();// datepicker gives year like 57 or 13 so in local DB it is 57 or 13. I need 1957 or 2013
            cal.setTime(new Date());
            int yearNow = cal.get(Calendar.YEAR);
            if ((yearThen >= 0 && yearThen <= yearNow - 2000)) {
                yearThen = yearThen + 2000;
            }
            if (yearThen > yearNow - 2000 && yearThen <= 99) {
                yearThen = yearThen + 1900;
            }

            datePicker.updateDate(yearThen, monthThen, dayThen);// changed date for datepicker -> year = 1957 or 2013
        }


        if (sPrefMainRead.getString("gender", "").equals("male")) {
            radioMale.setChecked(true);
            checkedGender = "male";
        } else if (sPrefMainRead.getString("gender", "").equals("female")) {
            radioFemale.setChecked(true);
            checkedGender = "female";
        } else {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
        }

        ed1.apply();
        if (personDataFilledFlag == 0 && (!nameSurnameHere.equalsIgnoreCase(""))) {// comes after MainActivity
            go_activity_data();// all personData was filled before
        }


// +++++++++++++++++++++++++++++++++++++++++++++++++

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        if (isConnected_isWIFI) {// // ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE

                    doWorkPersonActivity();
                }
                else {
                    Toast.makeText(getApplicationContext(),getString(R.string.internet_no), Toast.LENGTH_LONG).show();
                }
            }
        });
        // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
        // what button was clicked
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioMale:
                        checkedGender = "male";
                        break;
                    case R.id.radioFemale:
                        checkedGender = "female";
                        break;
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
    // +++++++++++++++++++++++++++++++++++
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person, menu);
        return true;
    }
    // +++++++++++++++++++++++++++++++++++
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_data:
                go_activity_data();
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

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void doWorkPersonActivity() {
        birthdayDate = (datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear());// SEE LOCALE

        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.US);// Local US

        Date date = null;
        try {
            date = sdf.parse(birthdayDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // filling the "form" of this activity
        final String  birthDayHere = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).format(date);


        String nameHere = textNameSurname.getText().toString();
        if (nameHere.equals("")) {
            labelNameSurname.setTextColor(Color.RED);

        }
        else if (nameHere.contains(",") ){
            labelNameSurname.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), getString(R.string.NAME_SURNAME)+getString(R.string.CONTAINS)+"\",\" !", Toast.LENGTH_LONG).show();
        }
        else if (nameHere.contains("~") ){
            labelNameSurname.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), getString(R.string.NAME_SURNAME)+getString(R.string.CONTAINS)+"\"~\" !", Toast.LENGTH_LONG).show();
        }

        final String  emailHere = textEmail.getText().toString();
        if (emailHere.equals("")) {
            labelEmail.setTextColor(Color.RED);
        }

        final String  genderHere = (!checkedGender.equals("")) ? checkedGender : "";
        if (genderHere.equals("")) {
            labelGender.setTextColor(Color.RED);

        }

        if (!nameHere.contains("~") && !nameHere.contains(",")){

            person = new Person();
            person.setPhone(phoneLoginTotal);
            person.setName(nameHere);
            person.setEmail(emailHere);
            person.setBirthday(birthDayHere);
            person.setGender(genderHere);

            // Save data to server and if SUCCSESSFUL
            Gson gsonPerson = new GsonBuilder().serializeNulls().create();
            final String requestJsonPersonData = gsonPerson.toJson(person);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final String finalNameHere = nameHere;

            StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            savePersonToLocalDB(finalNameHere, emailHere, birthDayHere, genderHere);
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
                    params.put("action", "personality");
                    params.put("persondata", requestJsonPersonData);//requestJsonPerson = String for request in servlet
                    return params;
                }
            };
            AsyncQueueRequest asyncPerson = new AsyncQueueRequest(this, queue, postRequest);
            asyncPerson.execute();
        }
    }


// ++++++++++++++++++++++++++++++++++++++++++++++
    // Menu items actions
    private void go_activity_data() {
        startActivity(new Intent(this, DataActivity.class));
    }
    private void go_activity_store() {
        startActivity(new Intent(this, StoreActivity.class));
    }
    private void go_activity_med() {
        startActivity(new Intent(this, MedActivity.class));
    }
    private void go_activity_contacts() {
        startActivity(new Intent(this, ContactsActivity.class));
    }
    private void go_activity_people() {
        startActivity(new Intent(this, PeopleActivity.class));
    }
    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    private void savePersonToLocalDB( String nameHere, String emailHere, String birthDayHere, String genderHere){

        // SAVE person to local DB --> key-value can be changed but not cryptId

        sPrefMainRead = getSharedPreferences(MainActivityPREFERENCES, Context.MODE_PRIVATE);
        ed2 = sPrefMainRead.edit();

        ed2.putString("name", nameHere);
        ed2.putString("email", emailHere);
        ed2.putString("birthday", birthDayHere);
        ed2.putString("gender", genderHere);
        ed2.apply();

        go_activity_data();
    }
}
