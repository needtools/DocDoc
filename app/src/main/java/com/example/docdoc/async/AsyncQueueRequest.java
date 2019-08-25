package com.example.docdoc.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.docdoc.R;

public class AsyncQueueRequest extends AsyncTask<Void, Void, Void> {
    private Activity act;
    private ProgressDialog progressDialog;
    private RequestQueue queue;
    private StringRequest postRequest;

    public AsyncQueueRequest(Activity act, RequestQueue queue, StringRequest postRequest) {
        this.act=act;
        this.queue=queue;
        this.postRequest=postRequest;
    }


    @Override
    protected Void doInBackground(Void... voids) {
//        for (int i=0; i <= 3; i++) {
//                try {
//                    Thread.sleep(1000);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        queue.add(postRequest);
        return (null);
    }

    @Override
    protected void onPostExecute(Void unused) {
        progressDialog.dismiss();

    }

    protected void onPreExecute(){
        progressDialog = ProgressDialog.show(act, null, null, true, false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_layout);
    }

}
