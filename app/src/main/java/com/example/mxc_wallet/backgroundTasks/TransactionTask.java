package com.example.mxc_wallet.backgroundTasks;

import android.os.AsyncTask;

import java.util.List;

public class TransactionTask extends AsyncTask<Void, Void, List> {
    public TransactionTask() {
        super();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List list) {
        super.onPostExecute(list);
    }

    @Override
    protected void onCancelled(List list) {
        super.onCancelled(list);
    }

    @Override
    protected List doInBackground(Void... voids) {
        return null;
    }
}
