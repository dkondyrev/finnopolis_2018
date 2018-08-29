package ru.nsk.decentury.bonuses.hyperledger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Store {
    private String id;
    private String companyName;
    private double maxBonuses;
    private double bonusCoefficient;
    private int balance;
    private Map<String, Integer> priceList;
    private JSONObject json;

    public Store(String jsonString) {
        try {
            json = new JSONObject(jsonString);
            parseJson(json);
        } catch (JSONException e) {}
    }

    public Store(JSONObject json) {
        try {
            this.json = json;
            parseJson(json);
        } catch (JSONException e) {}
    }


    private void parseJson(JSONObject json) throws JSONException {
        id = json.getString("id");
        companyName = json.getString("companyName");
        maxBonuses = json.getDouble("maxBonuses");
        bonusCoefficient = json.getDouble("bonusCoefficient");
        balance = (int)json.getLong("balance");

        JSONArray jsonPrices = json.getJSONArray("priceList");
        priceList = new HashMap<>();

        for (int i = 0; i < jsonPrices.length(); ++i) {
            JSONObject priceEntry = jsonPrices.getJSONObject(i);

            String vendorCode = priceEntry.getString("vendorCode");
            int price = (int) priceEntry.getLong("price");
            priceList.put(vendorCode, price);
        }
    }

    public static ArrayList<Store> parseStoresArray(String jsonString) {
        ArrayList<Store> stores = new ArrayList<>();

        if (jsonString == null) {
            return stores;
        }

        try {
            JSONArray jsonStores = new JSONArray(jsonString);

            for (int i = 0; i < jsonStores.length(); ++i) {
                Store store = new Store(jsonStores.getJSONObject(i));
                stores.add(store);
            }
        } catch (JSONException e) {}

        return stores;
    }

    public String getId() {
        return id;
    }

    public int getPrice(String vendorCode) {
        Integer price = priceList.get(vendorCode);
        if (price == null) {
            return -1;
        }
        return price;
    }

    public int getBalance() {
        return balance;
    }

    public double getBonusCoefficient() {
        return bonusCoefficient;
    }

    public double getMaxBonuses() {
        return maxBonuses;
    }

    public String getCompanyName() {
        return companyName;
    }
}
