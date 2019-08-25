package com.example.docdoc.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static com.example.docdoc.MainActivity.isConnected_isWIFI;

public class NetworkMonitor extends BroadcastReceiver {
    private String LOG_TAG = "networkMonitorLog";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        Log.d(LOG_TAG+" #1 ", action);

        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&  activeNetwork.isConnectedOrConnecting();
        Log.d(LOG_TAG+" #2 ",System.currentTimeMillis()+ " isConnected: "+isConnected);

        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        Log.d(LOG_TAG+" #3 ",System.currentTimeMillis()+ " isWiFi: "+isWiFi);

        if (isConnected && isWiFi) {
            isConnected_isWIFI = true;
            Log.d(LOG_TAG+" #5 ",System.currentTimeMillis()+ " isConnected_isWIFI: "+isConnected_isWIFI);
        }
        else{
            isConnected_isWIFI = false;
            Log.d(LOG_TAG+" #6 ",System.currentTimeMillis()+" isConnected_isWIFI: "+isConnected_isWIFI);
        }


//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
//        Log.d(LOG_TAG+" #4 ",connectionInfo.getSSID());
    }


}
