package ru.nsk.decentury.bonuses.connection;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HyperledgerConnection {
    private RequestQueue queue;
    private String hyperledgerUrl = "http://37.193.252.60:3000/api/";
    private Map<String, String> getHeaders;
    private Map<String, String> postHeaders;

    public HyperledgerConnection (Context context) {
        queue = Volley.newRequestQueue(context);

        getHeaders = new HashMap<>();
        getHeaders.put("Accept", "application/json");

        postHeaders = new HashMap<>();
        postHeaders.put("Accept", "application/json");
        postHeaders.put("Content-Type", "application/json");
    }

    public String getRequest(String url) {
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.GET, hyperledgerUrl + url, future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeaders;
            }
        };
        queue.add(request);

        String response = null;
        try {
            response = future.get(45, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Connection error");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Connection error");
            e.printStackTrace();
        }

        return response;
    }

    public String postRequest(String url, JSONObject body) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, hyperledgerUrl + url, body, future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return postHeaders;
            }
        };
        queue.add(request);

        String response = null;
        try {
            response = future.get(15, TimeUnit.SECONDS).toString();
        } catch (InterruptedException e) {
            System.err.println("Connection error");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Connection error");
            e.printStackTrace();
        }

        return response;
    }
}
