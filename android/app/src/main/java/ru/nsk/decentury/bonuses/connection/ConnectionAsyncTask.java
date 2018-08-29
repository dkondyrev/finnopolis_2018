package ru.nsk.decentury.bonuses.connection;

import android.content.Context;
import android.os.AsyncTask;

import ru.nsk.decentury.bonuses.ResponseProcessor;


public class ConnectionAsyncTask extends AsyncTask<String, String, String> {
    private HyperledgerConnection connection;
    private String url;
    private ResponseProcessor processor;


    public ConnectionAsyncTask (Context c, String requestUrl, ResponseProcessor p) {
        connection = new HyperledgerConnection(c);
        url = requestUrl;
        processor = p;
    }


    @Override
    protected String doInBackground(String... strings) {
        return connection.getRequest(url);
    }

    @Override
    protected void onPostExecute(String result)
    {
        processor.processResult(result);
    }
}
