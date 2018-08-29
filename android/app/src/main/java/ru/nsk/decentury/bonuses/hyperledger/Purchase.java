package ru.nsk.decentury.bonuses.hyperledger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Purchase {
    private String store;
    private String buyer;
    private List<String> products;
    private int bonus;
    private String transactionId;
    private String timestamp;

    private JSONObject json;



    public Purchase(String jsonString) {
        try {
            json = new JSONObject(jsonString);
            parseJson(json);
        } catch (JSONException e) {}
    }

    public Purchase(JSONObject json) {
        try {
            this.json = json;
            parseJson(json);
        } catch (JSONException e) {}
    }

    private void parseJson(JSONObject json) throws JSONException {
        store = getIdFromString(json.getString("store"));
        buyer = getIdFromString(json.getString("buyer"));
        products = parseProductsArray(json.getJSONArray("products"));
        bonus = (int) json.getLong("bonus");
        transactionId = json.getString("transactionId");
        timestamp = json.getString("timestamp");
    }

    private String getIdFromString(String str) {
        int position = str.indexOf('#');
        return str.substring(position + 1);
    }

    private List<String> parseProductsArray(JSONArray productsArray) throws JSONException {
        ArrayList<String> productsList = new ArrayList<>();

        for (int i = 0; i < productsArray.length(); ++i) {
            String product = getIdFromString(productsArray.getString(i));
            productsList.add(product);
        }
        return productsList;
    }

    public static ArrayList<Purchase> parsePurchasesArray(String jsonString) {
        ArrayList<Purchase> purchases = new ArrayList<>();

        if (jsonString == null) {
            return purchases;
        }

        try {
            JSONArray jsonPurchases = new JSONArray(jsonString);

            for (int i = 0; i < jsonPurchases.length(); ++i) {
                Purchase purchase = new Purchase(jsonPurchases.getJSONObject(i));
                purchases.add(purchase);
            }
        } catch (JSONException e) {}

        return purchases;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getStore() {
        return store;
    }

    public List<String> getProducts() {
        return products;
    }

    public int getBonuses() {
        return bonus;
    }
}


