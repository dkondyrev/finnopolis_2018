package ru.nsk.decentury.bonuses.hyperledger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buyer {
    private String id;
    private String phoneNumber;
    private int balance;
    private Map<String, Integer> bonusBalancesMap;
    private List<BonusBalance> bonusBalances;
    private JSONObject json;

    public class BonusBalance {
        public String store;
        public int tokenBalance;

        public BonusBalance(String s, int balance) {
            store = s;
            tokenBalance = balance;
        }
    }

    public Buyer(JSONObject json) {
        bonusBalancesMap = new HashMap<>();
        bonusBalances = new ArrayList<>();

        if (json == null) {
            return;
        }
        try {
            parseJson(json);
        } catch (JSONException e) {}
    }

    public Buyer(String jsonString) {
        bonusBalancesMap = new HashMap<>();
        bonusBalances = new ArrayList<>();

        if (jsonString == null) {
            return;
        }

        try {
            json = new JSONObject(jsonString);
            parseJson(json);
        } catch (JSONException e) {}
    }

    private void parseJson(JSONObject json) throws JSONException {
        id = json.getString("id");
        balance = (int) json.getLong("balance");
        phoneNumber = json.getString("phoneNumber");

        JSONArray jsonWallet = json.getJSONArray("wallet");


        for (int i = 0; i < jsonWallet.length(); ++i) {
            JSONObject walletEntry = jsonWallet.getJSONObject(i);
            String store = getIdFromString(walletEntry.getString("store"));
            int tokenBalance = (int) walletEntry.getLong("tokenBalance");
            bonusBalancesMap.put(store, tokenBalance);
            bonusBalances.add(new BonusBalance(store, tokenBalance));
        }
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getBalance() {
        return balance;
    }

    public List<BonusBalance> getBonusBalances() {
        return bonusBalances;
    }

    public int getBonusBalance(String store) {
        Integer balance = bonusBalancesMap.get(store);
        if (balance == null) {
            return 0;
        }
        return balance;
    }


    private String getIdFromString(String str) {
        int position = str.indexOf('#');
        return str.substring(position + 1);
    }

    public static ArrayList<Buyer> parseBuyersArray(String jsonString) {
        ArrayList<Buyer> buyers = new ArrayList<>();

        if (jsonString == null) {
            return buyers;
        }

        try {
            JSONArray jsonBuyers = new JSONArray(jsonString);

            for (int i = 0; i < jsonBuyers.length(); ++i) {
                Buyer buyer = new Buyer(jsonBuyers.getJSONObject(i));
                buyers.add(buyer);
            }
        } catch (JSONException e) {}

        return buyers;
    }
}