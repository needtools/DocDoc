package com.example.docdoc;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.docdoc.models.Contact;
import com.example.docdoc.network.NetworkMonitor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.docdoc.MainActivity.isConnected_isWIFI;
import static com.example.docdoc.MainActivity.myURL;
import static com.example.docdoc.MainActivity.phoneLoginTotal;

public class ContactsActivity extends AppCompatActivity {
    ListView listContacts;
    Map<String, String> params;
    TextView labelPhoneContact;
    String[] contacts;
    String nameHere, phoneHere;
    private NetworkMonitor mNetworkMonitor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarContacts);
        setSupportActionBar(toolbar);

        nameHere="";
        phoneHere="";

        listContacts = (ListView)findViewById(R.id.listContacts);
        labelPhoneContact = (TextView)findViewById(R.id.labelPhoneContact);

        mNetworkMonitor = new NetworkMonitor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkMonitor, intentFilter);

        // +++++++++++++++++++++++++++++++++++++++++++
        if (isConnected_isWIFI) {// ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE
            // download contacts from server
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            ArrayList<String> al= gson.fromJson(response, ArrayList.class);
                            if (al.size()>0) {

                                fillListContacts(al);
                            }

                            else {
                                Toast.makeText(getApplicationContext(), getString(R.string.EMPTY), Toast.LENGTH_LONG).show();// no phone number
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), getString(R.string.ERROR), Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    params = new HashMap<>();
                    //add your parameters here as key-value pairs to send to servlet
                    params.put("action", "showcontacts");
                    params.put("personphone", phoneLoginTotal);
                    return params;
                }
            };
            AsyncQueueRequest asyncContacts = new AsyncQueueRequest(this, queue, postRequest);
            asyncContacts.execute();
        }
        else {
            Toast.makeText(getApplicationContext(),getString(R.string.internet_no), Toast.LENGTH_LONG).show();
        }


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // button listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_contacts);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doWorkContactsActivity();

            }
        });
    }
    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mNetworkMonitor);

    }
    // ++++++++++++++++++++++++++++
    // context menu for list items

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu_contacts, menu);
//        menu.setHeaderTitle("Select The Action");
    }
    // ++++++++++++++++++++++++++++++++++++++
    // alert dialog yes-no
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId()==R.id.remove){
            deleteContactToServer(contacts[(int)info.id]);
        }
        else if(item.getItemId()==R.id.cancel){
            return false;
        }
        else{
            return false;
        }
        return true;
    }
    // +++++++++++++++++++
    // fill the list of contacts from local DB
    public void fillListContacts(ArrayList contactsList){
        // take data to Map and list
        contacts = new String[contactsList.size()];

        for (int i=0; i<contactsList.size(); i++){
            String t = (String) contactsList.get(i);

            String[]tt=t.split("~");
//            tt[0] = returnSymbols(tt[0]);
            tt[1] = returnSymbols(tt[1]);

            contacts[i]=tt[1]+"\n<"+tt[0]+">";
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);
        listContacts.setAdapter(adapter);

        // Register the ListView  for Context menu
        registerForContextMenu(listContacts);


    }
    //+++++++++++++++++++++++++++++++++
    // Alert dialog for adding contact - two EditTexts and two buttons = yes-no

    public  void   doWorkContactsActivity(){// from button listener

        if (isConnected_isWIFI) {// ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE
            final Context context = this;
            final Activity activity = (Activity) context;

            LayoutInflater li = LayoutInflater.from(context);
            View contactsView = li.inflate(R.layout.add_contacts, null);// window

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

            mDialogBuilder.setView(contactsView);

            final  EditText userInput = (EditText) contactsView.findViewById(R.id.input_nameSurname);
            final EditText phoneInput = (EditText) contactsView.findViewById(R.id.input_phone);
            // write to local db
            mDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    nameHere=(!userInput.getText().toString().equals(""))?userInput.getText().toString():"-";
                                    if (!nameHere.equals("-")){
                                        nameHere=  replaceSymbols( nameHere);
                                    }
                                    phoneHere=(!(phoneInput.getText().toString().equals("")))? phoneInput.getText().toString():"";

                                    if (phoneHere.equals("")) {
                                        labelPhoneContact.setTextColor(Color.RED);
                                        Toast.makeText(getApplicationContext(),  getString(R.string.CONTACT)+getString(R.string.EMPTY), Toast.LENGTH_LONG).show();
                                    }
                                    else if(phoneHere.contains(",")){
                                        labelPhoneContact.setTextColor(Color.RED);
                                        Toast.makeText(getApplicationContext(), getString(R.string.CONTACT)+getString(R.string.CONTAINS)+"\",\" !", Toast.LENGTH_LONG).show();
                                        }
                                    else if(phoneHere.contains("~")){
                                        labelPhoneContact.setTextColor(Color.RED);
                                        Toast.makeText(getApplicationContext(), getString(R.string.CONTACT)+getString(R.string.CONTAINS)+"\"~\" !", Toast.LENGTH_LONG).show();
                                    }
                                    else {

                                        //Save contacts in server
                                        Contact contact = new Contact();

                                        contact.setPhone(phoneLoginTotal);
                                        contact.setZcontact( phoneHere+"~"+nameHere);

                                        Gson gsonContact = new GsonBuilder().serializeNulls().create();
                                        final String requestJsonContact = gsonContact.toJson(contact);

                                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Gson gson = new Gson();
                                                        ArrayList<String> al= gson.fromJson(response, ArrayList.class);

                                                        fillListContacts(al);
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
                                                params.put("action", "addcontact");
                                                params.put("contact", requestJsonContact);//requestJsonPerson = String for request in servlet
                                                return params;
                                            }
                                        };
                                        AsyncQueueRequest asyncNewContact = new AsyncQueueRequest(activity, queue, postRequest);
                                        asyncNewContact.execute();

                                    }

                                }
                            })
                    .setNegativeButton(getString(R.string.y_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = mDialogBuilder.create();

            alertDialog.show();

        }
        else {
            Toast.makeText(getApplicationContext(),getString(R.string.internet_no), Toast.LENGTH_LONG).show();
        }

    }
    // +++++++++++++++++++++++++++++++++++++++++++++
    private void deleteContactToServer( String contactDeleting)  {
        // if I want to delete contact I have delete it to server
        if (isConnected_isWIFI) {// ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE ONLINE
            String[] temp = contactDeleting.split("\n");
            Contact contact = new Contact();

            contact.setPhone(phoneLoginTotal);
            contact.setZcontact(temp[0]+"~"+temp[1]);// look saveContactToLocalDB => editor.putString(timeNow+"_namecontact", nameHere+" - "+phoneHere);


            Gson gsonPerson = new GsonBuilder().serializeNulls().create();
            final String requestJsonPerson = gsonPerson.toJson(contact);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, myURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            ArrayList<String> al= gson.fromJson(response, ArrayList.class);
                            if (al.size()>0) {
                                fillListContacts(al);
                            }

                            else {
                                listContacts.setAdapter(null);// clear listview
                                Toast.makeText(getApplicationContext(), getString(R.string.DASH), Toast.LENGTH_LONG).show();// no phone number
                            }

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
                    params.put("action", "deletecontact");
                    params.put("contact", requestJsonPerson);//requestJsonPerson = String for request in servlet
                    return params;
                }
            };
            AsyncQueueRequest asyncContact = new AsyncQueueRequest(this, queue, postRequest);
            asyncContact.execute();

        }
        else {
            Toast.makeText(getApplicationContext(),getString(R.string.internet_no), Toast.LENGTH_LONG).show();
        }

    }
    // +++++++++++++++++++++++++++++++++++++++++++++++
    // menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // menu listener
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_person:
                go_activity_reg();
                break;
            case R.id.menu_data:
                go_activity_data();
                break;
            case R.id.menu_med:
                go_activity_med();
                break;
            case R.id.menu_store:
                go_activity_store();
                break;
            case R.id.menu_people:
                go_activity_people();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    // ******************************
    // navigation between activities
    public void go_activity_data() {
        startActivity(new Intent(this, DataActivity.class));
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


    // +++++++++++++++++++++++++++++++++++++++++++++++++
    private String replaceSymbols(String stringHere) {
        stringHere = stringHere.replace(",", "&cedil;");
        stringHere = stringHere.replace("~", "&uml;");
        return stringHere;
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++
    private String  returnSymbols(String stringHere) {
        stringHere = stringHere.replace("&cedil;", ",");
        stringHere = stringHere.replace("&uml;", "~");
        return stringHere;
    }
}
