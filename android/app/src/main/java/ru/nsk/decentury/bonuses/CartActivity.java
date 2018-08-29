package ru.nsk.decentury.bonuses;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.nsk.decentury.bonuses.connection.ConnectionAsyncTask;
import ru.nsk.decentury.bonuses.connection.ConnectionPostTask;
import ru.nsk.decentury.bonuses.hyperledger.Buyer;

public class CartActivity extends AppCompatActivity implements ResponseProcessor {
    private Buyer buyer;
    private String storeId;
    private int bonusBalance = 0;
    private ArrayList<String> products;

    private TextView totalPriceTextView;
    private TextView bonusesBalanceTextView;
    private EditText bonusesAmountText;
    private Button buyButton;

    private BottomNavigationView navigation;

    private RecyclerView productsRecyclerView;
    private RecyclerView.Adapter productsAdapter;
    private RecyclerView.LayoutManager productsLayoutManager;

    enum RequestType {USER_INFO, BUY_TRANSACTION}
    private RequestType currentRequest;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_stores:
                    return true;
                case R.id.navigation_exchange:
                    openExchangeActivity();
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
        storeId = b.getString("storeId");

        products = b.getStringArrayList("products");
        ArrayList<Integer> prices = b.getIntegerArrayList("prices");


        setContentView(R.layout.activity_cart);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_stores);

        productsRecyclerView = (RecyclerView) findViewById(R.id.products_list);
        productsRecyclerView.setHasFixedSize(true);
        productsLayoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(productsLayoutManager);
        productsAdapter = new CartProductsAdapter(products, prices);
        productsRecyclerView.setAdapter(productsAdapter);

        totalPriceTextView = (TextView) findViewById(R.id.total_price);

        int totalPrice = 0;
        for (int i = 0; i < prices.size(); ++i){
            totalPrice += prices.get(i);
        }
        totalPriceTextView.setText(String.valueOf(totalPrice));


        bonusesBalanceTextView = (TextView) findViewById(R.id.bonuses_balance);
        bonusesAmountText = (EditText) findViewById(R.id.bonuses_amount);

        buyButton = (Button) findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBuyTransaction();
            }
        });

        getUserInfo(buyerId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_stores);
    }

    @Override
    public void processResult(String result) {
        switch (currentRequest) {
            case USER_INFO:
                processUserInfo(result);
                break;
            case BUY_TRANSACTION:
                System.out.println(result);
                break;
        }
    }

    private void getUserInfo(String userId) {
        new ConnectionAsyncTask(this, "Buyer/" + userId, this).execute();
        currentRequest = RequestType.USER_INFO;
    }

    private void processUserInfo(String result) {
        buyer = new Buyer(result);

        bonusBalance = buyer.getBonusBalance(storeId);
        bonusesBalanceTextView.setText(String.valueOf(bonusBalance));
    }

    private void sendBuyTransaction() {
        ArrayList<String> productsList = new ArrayList<>();

        for (int i = 0; i < products.size(); ++i) {
            productsList.add("ru.nsk.decentury.Product#" + products.get(i));
        }

        JSONArray productsArray = new JSONArray(productsList);

        Map<String, Object> map = new HashMap<>();
        map.put("$class", "ru.nsk.decentury.buyingGoodsInStore");
        map.put("store", "ru.nsk.decentury.Store#" + storeId);
        map.put("buyer", "ru.nsk.decentury.Buyer#" + buyer.getId());
        map.put("products", productsArray);

        int bonus = Integer.decode(bonusesAmountText.getText().toString());
        map.put("bonus", bonus);

        JSONObject request = new JSONObject(map);
        new ConnectionPostTask(this, "buyingGoodsInStore", request, this).execute();
        currentRequest = RequestType.BUY_TRANSACTION;

    }

    private void openProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyer.getId());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openExchangeActivity(){
        Intent intent = new Intent(this, ExchangeActivity.class);
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
