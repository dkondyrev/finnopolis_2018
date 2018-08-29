package ru.nsk.decentury.bonuses;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.nsk.decentury.bonuses.connection.ConnectionAsyncTask;
import ru.nsk.decentury.bonuses.connection.ConnectionPostTask;
import ru.nsk.decentury.bonuses.hyperledger.Buyer;
import ru.nsk.decentury.bonuses.hyperledger.Store;

public class ExchangeActivity extends AppCompatActivity implements ResponseProcessor {
    private Buyer buyer;
    private int fromStoreIndex = 0;
    private int toStoreIndex = 0;
    private List<Store> stores;
    private String[] storesNames;

    private BottomNavigationView navigation;

    private EditText bonusesEditText;
    private Spinner storesSpinner;
    private Spinner storesSpinner2;

    private ArrayAdapter<String> storesSpinnerAdapter;
    private ArrayAdapter<String> storesSpinner2Adapter;

    private Button changeButton;

    enum RequestType {USER_INFO, STORES, EXCHANGE_TRANSACTION}
    private RequestType currentRequest;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_stores:
                    openStoresActivity();
                    return true;
                case R.id.navigation_exchange:
                    return true;
                case R.id.navigation_main:
                    openMainActivity();
                    return true;
                case R.id.navigation_card:
                    openCardActivity();
                    return true;
                case R.id.navigation_profile:
                    openProfileActivity();
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        String buyerId = b.getString("buyerId");

        setContentView(R.layout.activity_exchange);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_exchange);

        bonusesEditText = (EditText) findViewById(R.id.bonuses_edit_text);

        storesSpinner = (Spinner) findViewById(R.id.store_spinner_1);
        storesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                fromStoreIndex = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        storesSpinner2 = (Spinner) findViewById(R.id.store_spinner_2);
        storesSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                toStoreIndex = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        changeButton = (Button) findViewById(R.id.change_button);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bonusesChangeTransaction();
            }
        });

        getUserInfo(buyerId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_exchange);
    }


    @Override
    public void processResult(String result) {
        switch (currentRequest) {
            case USER_INFO:
                processUserInfo(result);
                break;
            case STORES:
                processStores(result);
                break;
            case EXCHANGE_TRANSACTION:

                break;
        }
    }

    private void getUserInfo(String userId) {
        new ConnectionAsyncTask(this, "Buyer/" + userId, this).execute();
        currentRequest = RequestType.USER_INFO;
    }

    private void processUserInfo(String result) {
        buyer = new Buyer(result);
        getStores();
    }

    private void getStores() {
        new ConnectionAsyncTask(this, "Store", this).execute();
        currentRequest = RequestType.STORES;
    }

    private void processStores(String result) {
        stores = Store.parseStoresArray(result);
        storesNames = new String[stores.size()];

        for (int i = 0; i < stores.size(); ++i) {
            storesNames[i] = stores.get(i).getId();
        }

        storesSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storesNames);
        storesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storesSpinner.setAdapter(storesSpinnerAdapter);

        storesSpinner2Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storesNames);
        storesSpinner2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storesSpinner2.setAdapter(storesSpinner2Adapter);
    }

    private void bonusesChangeTransaction() {
        int bonuses = Integer.decode(bonusesEditText.getText().toString());
        if (bonuses <= 0 || (fromStoreIndex == toStoreIndex)) {
            return;
        }
        String fromStore = "ru.nsk.decentury.Store#" + storesNames[fromStoreIndex];
        String toStore = "ru.nsk.decentury.Store#" + storesNames[toStoreIndex];
        String buyerName = "ru.nsk.decentury.Buyer#" + buyer.getId();

        Map<String, Object> map = new HashMap<>();
        map.put("$class", "ru.nsk.decentury.BonusesExchange");
        map.put("buyer", buyerName);
        map.put("fromStore", fromStore);
        map.put("toStore", toStore);
        map.put("bonusesAmount", bonuses);

        JSONObject request = new JSONObject(map);
        new ConnectionPostTask(this, "BonusesExchange", request, this).execute();
        currentRequest = RequestType.EXCHANGE_TRANSACTION;
    }

    private void openStoresActivity(){
        Intent intent = new Intent(this, StoresActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyer.getId());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyer.getId());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyer.getId());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openCardActivity(){
        Intent intent = new Intent(this, CardActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyer.getId());
        intent.putExtras(b);
        startActivity(intent);
    }

}
