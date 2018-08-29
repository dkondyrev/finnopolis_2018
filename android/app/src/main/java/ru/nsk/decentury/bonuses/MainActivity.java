package ru.nsk.decentury.bonuses;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.nsk.decentury.bonuses.connection.ConnectionAsyncTask;
import ru.nsk.decentury.bonuses.hyperledger.Buyer;
import ru.nsk.decentury.bonuses.hyperledger.Product;
import ru.nsk.decentury.bonuses.hyperledger.ProductInfo;
import ru.nsk.decentury.bonuses.hyperledger.Purchase;

public class MainActivity extends AppCompatActivity implements ResponseProcessor {
    private Buyer buyer;

    private BottomNavigationView navigation;
    private TextView nameTextView;

    private RecyclerView bonusesRecyclerView;
    private RecyclerView.Adapter bonusesAdapter;
    private RecyclerView.LayoutManager bonusesLayoutManager;

    private RecyclerView productsRecyclerView;
    private RecyclerView.Adapter productsAdapter;
    private RecyclerView.LayoutManager productsLayoutManager;

    enum RequestType {USER_INFO, USER_PRODUCTS}
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
                    openExchangeActivity();
                    return true;
                case R.id.navigation_main:
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
        setContentView(R.layout.activity_main);

        Bundle b = getIntent().getExtras();
        String buyerId = b.getString("buyerId");

        nameTextView = (TextView) findViewById(R.id.main_user_name);

        bonusesRecyclerView = (RecyclerView) findViewById(R.id.main_binuses_list);
        bonusesRecyclerView.setHasFixedSize(true);
        bonusesLayoutManager = new LinearLayoutManager(this);
        bonusesRecyclerView.setLayoutManager(bonusesLayoutManager);

        productsRecyclerView = (RecyclerView) findViewById(R.id.main_products_list);
        productsRecyclerView.setHasFixedSize(true);
        productsLayoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(productsLayoutManager);


        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_main);

        getUserInfo(buyerId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_main);
    }

    @Override
    public void processResult(String result) {
        switch (currentRequest) {
            case USER_INFO:
                processUserInfo(result);
                break;
            case USER_PRODUCTS:
                processUserProducts(result);
                break;
        }
    }

    private void getUserProducts() {
        new ConnectionAsyncTask(this, "buyingGoodsInStore", this).execute();
        currentRequest = RequestType.USER_PRODUCTS;
    }

    private void getUserInfo(String id) {
        new ConnectionAsyncTask(this, "Buyer/" + id, this).execute();
        currentRequest = RequestType.USER_INFO;
    }

    private void processUserInfo(String result) {
        buyer = new Buyer(result);

        nameTextView.setText(buyer.getId());

        bonusesAdapter = new BonusesListAdapter(buyer.getBonusBalances());
        bonusesRecyclerView.setAdapter(bonusesAdapter);
        getUserProducts();
    }

    private void processUserProducts(String result) {
        List<Purchase> purchases = Purchase.parsePurchasesArray(result);

        List<ProductInfo> userProducts = new ArrayList<>();

        for (int i = 0; i < purchases.size(); ++i) {
            Purchase purchase = purchases.get(i);
            if (purchase.getBuyer().equals(buyer.getId())) {
                String store = purchase.getStore();
                List<String> products = purchase.getProducts();
                for (int j = 0; j < products.size(); ++j) {
                    userProducts.add(new ProductInfo(products.get(j), store));
                }
            }
        }

        productsAdapter = new ProductsListAdapter(userProducts);
        productsRecyclerView.setAdapter(productsAdapter);
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

    private void openExchangeActivity(){
        Intent intent = new Intent(this, ExchangeActivity.class);
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
