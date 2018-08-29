package ru.nsk.decentury.bonuses.connection;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import ru.nsk.decentury.bonuses.ResponseProcessor;


public class ConnectionPostTask extends AsyncTask<String, String, String> {
    private HyperledgerConnection connection;
    private String url;
    private JSONObject body;
    private ResponseProcessor processor;


    public ConnectionPostTask (Context c, String requestUrl, JSONObject b, ResponseProcessor p) {
        connection = new HyperledgerConnection(c);
        url = requestUrl;
        body = b;
        processor = p;
    }


    @Override
    protected String doInBackground(String... strings) {
        return connection.postRequest(url, body);
    }

    @Override
    protected void onPostExecute(String result)
    {
        processor.processResult(result);
    }
}
