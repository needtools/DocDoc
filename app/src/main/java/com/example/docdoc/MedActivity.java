package com.example.docdoc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;
import java.util.TreeMap;

import static com.example.docdoc.MainActivity.sPrefMedRead;

public class MedActivity extends AppCompatActivity {
    private String LOG_TAG = "medActivityLog";
    Map<String,String> mapMedTemp;
    ListView listMed;
    String[] mapMedKeys, mapMedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMed);
        setSupportActionBar(toolbar);

        listMed = (ListView)findViewById(R.id.listMed);

        fillMapMedTemp();


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_med);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doWorkMedActivity();
            }
        });

    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void fillMapMedTemp() {
        // take data from local DB to Map and list
        SharedPreferences.Editor  ed = sPrefMedRead.edit();
        mapMedTemp =  new TreeMap<>();// MED

        Map<String, ?> allEntries = sPrefMedRead.getAll();
        if(allEntries.size()>0){

            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                mapMedTemp.put(entry.getKey(), (String) entry.getValue());
//                Log.d(LOG_TAG,  System.currentTimeMillis()+" allEntries.size = "+allEntries.size()+" key = " + entry.getKey() + ", " + "value = " + entry.getValue()  );
            }

            fillListMedFromMapMedTemp();
        }
        else {
            listMed.setAdapter(null);// clear listview
            Toast.makeText(getApplicationContext(), getString(R.string.EMPTY), Toast.LENGTH_LONG).show();
        }
        ed.apply();
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void doWorkMedActivity() {
        final Context context = this;

        LayoutInflater li = LayoutInflater.from(context);
        View medView = li.inflate(R.layout.add_med, null);// window

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

        mDialogBuilder.setView(medView);

        final EditText medInput = (EditText) medView.findViewById(R.id.input_med);

        mDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String medHere=(!(medInput.getText().toString().equalsIgnoreCase("")))? medInput.getText().toString():"";

                                if (!medHere.equals("")) {
                                    saveMedHere(medHere);

                                }
                                else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.DASH), Toast.LENGTH_LONG).show();
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
    // +++++++++++++++++++++++++++++++++++++++++++++++
    private void saveMedHere(String medHere) {
        Long moment = System.currentTimeMillis();
        SharedPreferences.Editor  ed = sPrefMedRead.edit();
        ed.putString(moment+"", medHere);
        ed.apply();
        fillMapMedTemp();
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++
    private void fillListMedFromMapMedTemp() {
        mapMedKeys = new String[this.mapMedTemp.size()];
        mapMedValues = new String[this.mapMedTemp.size()];
        int i = mapMedKeys.length-1;
        for (Map.Entry<String, String> entry : this.mapMedTemp.entrySet()) {

            mapMedValues[i] = entry.getValue();
            mapMedKeys[i] = entry.getKey();
            i--;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mapMedValues);
        listMed.setAdapter(adapter);

        // Register the ListView  for Context menu
        registerForContextMenu(listMed);
    }
    // ++++++++++++++++
    // menu created
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_med, menu);
        return true;
    }
    // ++++++++++++++++++++++++++++
    // context menu for list items

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu_med, menu);
//        menu.setHeaderTitle("Select The Action");
    }
    // ++++++++++++++++++++++++++++++++++++++
    // alert dialog yes-no
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId()==R.id.remove){
            askDeleteMed(mapMedKeys[(int)info.id]);
        }
        else if(item.getItemId()==R.id.edit){
            editMed(mapMedKeys[(int)info.id]);
        }
        else if(item.getItemId()==R.id.cancel){
            return false;
        }
        else{
            return false;
        }
        return true;
    }
    // +++++++++++++++++++++++++++++++++++++++++
    private void askDeleteMed(final String mapMedKey) {
        final Context context = this;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteMed(mapMedKey);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(getString(R.string.xx_remove)+"?")
                .setTitle("");
        builder.setPositiveButton(getString(R.string.xx_remove), dialogClickListener);
        builder.setNegativeButton(getString(R.string.y_cancel), dialogClickListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // ++++++++++++++++++++++++++++++++++++++
    private void deleteMed(String mapMedKey) {
        SharedPreferences.Editor  ed = sPrefMedRead.edit();
        ed.remove(mapMedKey);
        ed.apply();
        fillMapMedTemp();
    }
    // ++++++++++++++++++++++++++++++++++++++
    private void editMed(final String mapMedKey) {
        final Context context = this;

        LayoutInflater li = LayoutInflater.from(context);
        View medView = li.inflate(R.layout.add_med, null);// window

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

        mDialogBuilder.setView(medView);

        final EditText medInput = (EditText) medView.findViewById(R.id.input_med);
        medInput.setText(mapMedTemp.get(mapMedKey));

        mDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String medHere=(!(medInput.getText().toString().equalsIgnoreCase("")))? medInput.getText().toString():"";

                                if (!medHere.equalsIgnoreCase("")) {
                                    deleteMed(mapMedKey);
                                    saveMedHere(medHere);

                                }
                                else {

                                    Toast.makeText(getApplicationContext(), getString(R.string.DASH), Toast.LENGTH_LONG).show();
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

    // +++++++++++++++++++++++++++++++++
// what menu items was clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_person:
                go_activity_reg();
                break;
            case R.id.menu_data:
                go_activity_data();
                break;
            case R.id.menu_store:
                go_activity_store();
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
    // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
    // Menu items actions
    private void go_activity_data() {
        startActivity(new Intent(this, DataActivity.class));
    }
    private void go_activity_store() {
        startActivity(new Intent(this, StoreActivity.class));
    }
    public void go_activity_reg() {
        startActivity(new Intent(this, PersonActivity.class));
    }
    private void go_activity_contacts() {
        startActivity(new Intent(this, ContactsActivity.class));
    }
    private void go_activity_people() {
        startActivity(new Intent(this, PeopleActivity.class));
    }


}
