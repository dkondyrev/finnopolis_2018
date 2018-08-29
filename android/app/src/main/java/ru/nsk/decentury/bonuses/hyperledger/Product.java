package ru.nsk.decentury.bonuses.hyperledger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Product {
    private String id;
    private String name;
    private String vendorCode;
    private String producer;
    private String owner;
    private String productState;
    private JSONObject json;

    public Product(String jsonString) {
        try {
            json = new JSONObject(jsonString);
            parseJson(json);
        } catch (JSONException e) {}
    }

    public Product(JSONObject json) {
        try {
            this.json = json;
            parseJson(json);
        } catch (JSONException e) {}
    }

    private void parseJson(JSONObject json) throws JSONException {
        id = json.getString("id");
        name = json.getString("name");
        vendorCode = json.getString("vendorCode");
        producer = getIdFromString(json.getString("producer"));
        owner = getIdFromString(json.getString("owner"));
        productState = json.getString("productState");
    }

    private String getIdFromString(String str) {
        int position = str.indexOf('#');
        return str.substring(position + 1);
    }

    public static ArrayList<Product> parseProductsArray(String jsonString) {
        ArrayList<Product> products = new ArrayList<>();

        try {
            JSONArray jsonProducts = new JSONArray(jsonString);

            for (int i = 0; i < jsonProducts.length(); ++i) {
                Product product = new Product(jsonProducts.getJSONObject(i));
                products.add(product);
            }
        } catch (JSONException e) {}

        return products;
    }

    public String getOwner() {
        return owner;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public String getId() {
        return id;
    }
}
