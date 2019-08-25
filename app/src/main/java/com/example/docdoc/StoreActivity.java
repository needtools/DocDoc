package com.example.docdoc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.docdoc.database.DBHelper;
import com.example.docdoc.locales.TimeLocales;

import java.util.ArrayList;
import java.util.List;

import static com.example.docdoc.MainActivity.localeTotal;

public class StoreActivity extends AppCompatActivity {
    DBHelper dbHelper;
    int start=0;
    private String LOG_TAG = "storeActivityLog";
    TextView temperature1, temperature2,date1,date2, high1, high2, low1, low2,
            pulse1,pulse2,sugar1,sugar2;
    SQLiteDatabase db;
    List<Integer> dbIdList;
    ImageButton butRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        dbHelper = new DBHelper(this);

        temperature1 = (TextView)findViewById(R.id.temperature1);
        temperature2 = (TextView)findViewById(R.id.temperature2);
        date1 = (TextView)findViewById(R.id.date1);
        date2 = (TextView)findViewById(R.id.date2);
        high1 = (TextView)findViewById(R.id.high1);
        high2 = (TextView)findViewById(R.id.high2);
        low1 = (TextView)findViewById(R.id.low1);
        low2 = (TextView)findViewById(R.id.low2);
        pulse1 = (TextView)findViewById(R.id.pulse1);
        pulse2 = (TextView)findViewById(R.id.pulse2);
        sugar1 = (TextView)findViewById(R.id.sugar1);
        sugar2 = (TextView)findViewById(R.id.sugar2);
        butRight = (ImageButton)findViewById(R.id.butRight);

        dbIdList = new ArrayList<>();

        deleteOldLocalDBMessages();

    }
    // +++++++++++++++++++++++++++
    private void deleteOldLocalDBMessages() {

        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("data", null, null, null, null, null,  "id DESC");

       if(c.moveToFirst()){
            int idColIndex = c.getColumnIndex("id");
            int timeColIndex = c.getColumnIndex("time");
            do {
//                        Log.d(LOG_TAG, "All data with ID = " + idColIndex + " " +timeColIndex);
                if (System.currentTimeMillis()-2678400000L>Long.parseLong(c.getString(timeColIndex))){
                    int delCount = db.delete("data", "time = " + c.getString(timeColIndex), null);
                }
                else {
                    dbIdList.add(c.getInt(idColIndex));
                }
            } while (c.moveToNext());
                   doWorkStoreActivity(start);//0
        }
       else {
            Log.d(LOG_TAG, getString(R.string.EMPTY));
        }
        c.close();
        dbHelper.close();

    }
//    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 // fill  tableLayout with data
    /*
    keysListIndex = 0 => value " + keysList.get(keysListIndex) = 5
    keysListIndex = 1 => value " + keysList.get(keysListIndex) = 4
     */
public  void doWorkStoreActivity(int start) {//

//    Log.d(LOG_TAG,"cursorId = " + start + " size = " +dbIdList.size() + " value " + dbIdList.get(start));

    db = dbHelper.getWritableDatabase();
    String selection = " id LIKE ? ";

    if(dbIdList.size()>0) {// if next exists
        String[] selectionArgs = new String[]{dbIdList.get(start) + ""};
        Cursor c1 = db.query("data", null, selection, selectionArgs, null, null, null);

        if (c1 != null) {
            c1.moveToFirst();

//            int   idColIndex = c1.getColumnIndex("id");
            int timeColIndex = c1.getColumnIndex("time");
            int temperatureColIndex = c1.getColumnIndex("temperature");
            int systolicColIndex = c1.getColumnIndex("systolic");
            int diastolicColIndex = c1.getColumnIndex("diastolic");
            int pulseColIndex = c1.getColumnIndex("pulse");
            int sugarColIndex = c1.getColumnIndex("sugar");

//                    Log.d(LOG_TAG,
//                    "id = " + c1.getString(idColIndex) + ", " +
//                            "time = " + c1.getString(timeColIndex) + ", " +
//                            "temperature = " + c1.getString(temperatureColIndex)+ ", " +
//                            "systolic = " + c1.getString(systolicColIndex) + ", " +
//                            "diastolic = " + c1.getString(diastolicColIndex) + ", " +
//                            "pulse = " + c1.getString(pulseColIndex) + ", " +
//                            "sugar = " + c1.getString(sugarColIndex)
//                    );

            String moment = getMoment(Long.valueOf(c1.getString(timeColIndex)));
            date1.setText(moment);
            temperature1.setText(c1.getString(temperatureColIndex));
            high1.setText(c1.getString(systolicColIndex));
            low1.setText(c1.getString(diastolicColIndex));
            pulse1.setText(c1.getString(pulseColIndex));
            sugar1.setText(c1.getString(sugarColIndex));

            c1.close();
        }
    }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.EMPTY) + getString(R.string.UPS), Toast.LENGTH_LONG).show();
        go_activity_data();
        }

    if(dbIdList.size()>1) {// if next exists

        String[] selectionArgs2 = new String[]{dbIdList.get(start + 1) + ""};

        Cursor c2 = db.query("data", null, selection, selectionArgs2, null, null, "id DESC");

        if (c2 != null) {
            c2.moveToFirst();

//            int   idColIndex2 = c2.getColumnIndex("id");
            int timeColIndex2 = c2.getColumnIndex("time");
            int temperatureColIndex2 = c2.getColumnIndex("temperature");
            int systolicColIndex2 = c2.getColumnIndex("systolic");
            int diastolicColIndex2 = c2.getColumnIndex("diastolic");
            int pulseColIndex2 = c2.getColumnIndex("pulse");
            int sugarColIndex2 = c2.getColumnIndex("sugar");


//            Log.d(LOG_TAG,"id2 = " + c2.getString(idColIndex2) + ", " +
//                    "time2 = " + c2.getString(timeColIndex2) + ", " +
//                            "temperature2 = " + c2.getString(temperatureColIndex2)+ ", " +
//                            "systolic2 = " + c2.getString(systolicColIndex2) + ", " +
//                            "diastolic2 = " + c2.getString(diastolicColIndex2) + ", " +
//                            "pulse2 = " + c2.getString(pulseColIndex2) + ", " +
//                            "sugar2 = " + c2.getString(sugarColIndex2)
//            );


            String moment2 = getMoment(Long.valueOf(c2.getString(timeColIndex2)));
            date2.setText(moment2);
            temperature2.setText(c2.getString(temperatureColIndex2));
            high2.setText(c2.getString(systolicColIndex2));
            low2.setText(c2.getString(diastolicColIndex2));
            pulse2.setText(c2.getString(pulseColIndex2));
            sugar2.setText(c2.getString(sugarColIndex2));

            c2.close();

                butRight.setVisibility(View.VISIBLE);

        }
    }


    dbHelper.close();

}
    //    // +++++++++++++++++++++++++++++++++++++++++++++++++++++v
    private String getMoment(long timeHere){

        TimeLocales tl = new TimeLocales(localeTotal.toString(), timeHere);// ru_RU
        String formatTimeLocale = tl.timeformated;// 30.5.1976

        return formatTimeLocale;
    }
// +++++++++++++++++++++++++++++++++++++++++++
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // ++++++++++++++++++++++++++++++++
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
    public void go_activity_data() {
        startActivity(new Intent(this, DataActivity.class));
    }
    public void go_activity_reg() {

        startActivity(new Intent(this, PersonActivity.class));
    }
// ++++++++++++++++++++++++++++++++++++++++++++
    public void butLeftEnd(View view) {
    start=0;
        doWorkStoreActivity(start);
    }
// ++++++++++++++++++++++++++++++++++++++++
    public void butLeft(View view) {
        start=start-1;
        if(start<0)
            start=0;
        doWorkStoreActivity(start);
    }
// +++++++++++++++++++++++++++++++
    public void butRight(View view) {
        start=start+1;
        if(start>dbIdList.size()-2)
            start=dbIdList.size()-2;
        doWorkStoreActivity(start);
    }

}
