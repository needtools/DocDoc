package com.example.docdoc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.docdoc.async.AsyncQueueRequest;
import com.example.docdoc.models.Contact;
import com.example.docdoc.models.Person;
import com.example.docdoc.network.NetworkMonitor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.example.docdoc.MainActivity.isConnected_isWIFI;
import static com.example.docdoc.MainActivity.myURL;
import static com.example.docdoc.MainActivity.phoneLoginTotal;

public class PeopleActivity extends AppCompatActivity {
    private String LOG_TAG = "peopleActivityLog";
    Map<String, String> params;
    ListView listPeople;
    String[] peopleInfo, peoplePhone;
    Map<String,String> mapPatientTemp;
    private NetworkMonitor mNetworkMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        Toolbar toolbar = findViewById(R.id.toolbarPeople);
        setSupportActionBar(toolbar);

        listPeople = (ListView)findViewById(R.id.listPeople);

        mapPatientTemp =  new   TreeMap<>();// PEOPLE

        mNetworkMonitor = new NetworkMonitor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkMonitor, intentFilter);

        if (isConnected_isWIFI) {// ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE
            doWorkPeopleActivity();
        }
        else {
            Toast.makeText(getApplicationContext(),getString(R.string.internet_no), Toast.LENGTH_LONG).show();
        }

    }

    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    // menu on the top of activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people, menu);
        return true;
    }
    // ++++++++++++++++++++++++++++++++++++++++++
    // what item of menu was clicked
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_person:
                go_activity_reg();
                break;
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
        }

        return super.onOptionsItemSelected(item);
    }
    // +++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mNetworkMonitor);

    }
    // ++++++++++++++++++++++++++++++++++++++++++
    private void doWorkPeopleActivity() {
        // for filling list of people who believes me as doctor
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest( com.android.volley.Request.Method.POST, myURL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        if(!response.equalsIgnoreCase("EMPTY")){
                            makeList( response);

                        }
                        else {// list of patients is empty

                            Toast.makeText(getApplicationContext(), getString(R.string.DASH), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), getString(R.string.UPS), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                params = new HashMap<>();
                //add your parameters here as key-value pairs to send to servlet
                params.put("action", "showpeople");
                params.put("myphone", phoneLoginTotal);
                return params;
            }
        };

        AsyncQueueRequest asyncPeople = new AsyncQueueRequest(this, queue, postRequest);
        asyncPeople.execute();
    }
    // ++++++++++++++++++++++++++++++++++++++++++
    private void makeList(String response) {
        // make list of peoples from response of server
        Gson gson = new Gson();
        Type personType = new TypeToken<ArrayList<Person>>(){}.getType();
        ArrayList<Person> al= gson.fromJson(response, personType);

        for(Person p : al){
            String tempGender="";
            if(p.getGender().equals(getString(R.string.male))){
                tempGender=getString(R.string.male);
            }
            else if (p.getGender().equals(getString(R.string.female))){
                tempGender=getString(R.string.female);
            }
            String tempValue="";
            if(!p.getName().equalsIgnoreCase(""))tempValue=tempValue+ "\n" + p.getName();
            if(!p.getEmail().equalsIgnoreCase(""))tempValue=tempValue+ "\n" + p.getEmail();
            if(!p.getBirthday().equalsIgnoreCase(""))tempValue=tempValue+ "\n" + getAge(p.getBirthday());
            if(!tempGender.equalsIgnoreCase(""))tempValue=tempValue+ "\n" +  tempGender;
            mapPatientTemp.put(p.getPhone(), "<"+p.getPhone()+">" +  tempValue);
        }

        fillListPeopleFromMapPatientTemp();

    }
    // ++++++++++++++++++++++++++++++++++++++++++
    // fill the list of peoples from  and give to items the listener
    private void fillListPeopleFromMapPatientTemp() {

        if (mapPatientTemp.size()>0) {
            peopleInfo = new String[this.mapPatientTemp.size()];
            peoplePhone = new String[this.mapPatientTemp.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : this.mapPatientTemp.entrySet()) {

                peopleInfo[i] = entry.getValue();
                peoplePhone[i] = entry.getKey();
                i++;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, peopleInfo);
            listPeople.setAdapter(adapter);

            // Register the ListView  for Context menu
            registerForContextMenu(listPeople);

            // each item of list is clickable
            listPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    go_activity_story(peoplePhone[position], peopleInfo[position]);
                }

            });
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.DASH), Toast.LENGTH_LONG).show();
        }
    }
    // ++++++++++++++++++++++++++++++++++++++++++
    // context menu for each item of list
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu_people, menu);
//        menu.setHeaderTitle("Select The Action");
    }
    // ++++++++++++++++++++++++++++
    // listener of context menu = yes-no
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId()==R.id.remove){
            deletePeople(peopleInfo[(int)info.id]);
//            Toast.makeText(getApplicationContext(),people[(int)info.id],Toast.LENGTH_LONG).show();
        }
        else if(item.getItemId()==R.id.cancel){
            return false;
        }
        else{
            return false;
        }
        return true;
    }
    // ++++++++++++++++++++++++++++++++++++++++++
    private void deletePeople(String zapis) {
// deleting people from map

        final Contact deleteContact = new Contact();

        if( mapPatientTemp.size()>0){
            for (Map.Entry<String, String> entry : mapPatientTemp.entrySet()) {
                if(entry.getValue().equals(zapis)){
                    deleteContact.setPhone( entry.getKey());// his phone
                    deleteContact.setZcontact(phoneLoginTotal);// my phone in his list

                }
            }
        }

        askDeletePeople(deleteContact);
    }
    // ++++++++++++++++++++++++++++++++++++++++++
    private void askDeletePeople(final Contact deleteContact) {
        // dialog yes-no
        final Context context = this;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deletePeopleToServer(deleteContact);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.xx_remove)+"?").setPositiveButton(getString(R.string.xx_remove), dialogClickListener)
                .setNegativeButton(getString(R.string.y_cancel), dialogClickListener).show();
    }
    // ++++++++++++++++++++++++++++++++++++++++++
    private void deletePeopleToServer(final Contact deleteContact) {
        // deleting people from server
        if (isConnected_isWIFI) {// ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE
            Gson gsonPeople = new GsonBuilder().serializeNulls().create();
            final String requestJsonPeople = gsonPeople.toJson(deleteContact);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Delete contact to local DB --> key-value
                            deletePeopleFromMapPatientTemp(deleteContact.getPhone());
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
                    params.put("action", "deletepeople");
                    params.put("people", requestJsonPeople);//requestJsonPeople = String for request in servlet
                    return params;
                }
            };
            queue.add(postRequest);
        }
        else {
            Toast.makeText(getApplicationContext(),getString(R.string.internet_no), Toast.LENGTH_LONG).show();
        }

    }

    // ++++++++++++++++++++++++++++++++++++++++++
    // if people was deleted from server it delites from local DB
    private void deletePeopleFromMapPatientTemp(String phonePeopleDelete) {
        mapPatientTemp.remove(phonePeopleDelete);
        listPeople.setAdapter(null);// clear listview
        fillListPeopleFromMapPatientTemp();
    }

    // ++++++++++++++++++++++++++++++++++++++++++
    // Get gender and change according translation
    private String getGender(String s) {
        if (s.equalsIgnoreCase(getString(R.string.male))){
            s = getString(R.string.male);
        }
        else if (s.equalsIgnoreCase(getString(R.string.female))){
            s = getString(R.string.female);
        }
        else {
            s ="";
        }
        return s;
    }

    // ++++++++++++++++++++++++++++++++++++++++++
    // count age from birthday
    private String getAge(String birthdayDate) {
        String[] yearMonthDayBirth = birthdayDate.split("/");
        int yearThen = Integer.parseInt(yearMonthDayBirth[2]);
        int monthThen = Integer.parseInt(yearMonthDayBirth[0]);
        int dayThen = Integer.parseInt(yearMonthDayBirth[1]);// Locale.US

        Calendar calBirthday = Calendar.getInstance();
        calBirthday.setTime(new Date());
        int yearNow = calBirthday.get(Calendar.YEAR);
        if ((yearThen >= 0 && yearThen <= yearNow - 2000)) {
            yearThen = yearThen + 2000;
        }
        if (yearThen > yearNow - 2000 && yearThen <= 99) {
            yearThen = yearThen + 1900;
        }

        Calendar birthCalendar = new GregorianCalendar();
        birthCalendar.set(yearThen, monthThen-1, dayThen);

        Calendar nowCalendar = new GregorianCalendar();
        nowCalendar.setTime(new Date());

        int diffYear = (nowCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR))-1;// full years of age

        return diffYear+"";
    }

    // ******************************
    // go to sertain activity
    public void go_activity_data() {
        startActivity(new Intent(this, DataActivity.class));
    }
    private void go_activity_med() {
        startActivity(new Intent(this, MedActivity.class));
    }
    public void go_activity_contacts() {
        startActivity(new Intent(this, ContactsActivity.class));
    }
    public void go_activity_store() {
        startActivity(new Intent(this, StoreActivity.class));
    }
    public void go_activity_reg() {
        startActivity(new Intent(this, PersonActivity.class));
    }

    public void go_activity_story(String patientPhone, String patientInfo) {
        String[] patientInfoHere = patientInfo.split("\n");

        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra("patientPhone", patientPhone);
        intent.putExtra("patientInfo",patientInfoHere[1]+", "+patientInfoHere[3]);
        startActivity(intent);
    }

}
