package ru.nsk.decentury.bonuses.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import ru.nsk.decentury.bonuses.R;
import ru.nsk.decentury.bonuses.ResponseProcessor;
import ru.nsk.decentury.bonuses.connection.ConnectionAsyncTask;
import ru.nsk.decentury.bonuses.hyperledger.Purchase;

public class StoreMainActivity extends AppCompatActivity implements ResponseProcessor {
    private String storeId;

    private BottomNavigationView navigation;

    private TextView storeNameTextView;
    private TextView productsTextView;
    private TextView bonusesTextView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    openScanActivity();
                    return true;
                case R.id.navigation_main:
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
        setContentView(R.layout.activity_store_main);

        Bundle b = getIntent().getExtras();
        storeId = b.getString("storeId");

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_main);

        storeNameTextView = (TextView) findViewById(R.id.store_name);
        storeNameTextView.setText(storeId);

        productsTextView = (TextView) findViewById(R.id.products_number);
        bonusesTextView = (TextView) findViewById(R.id.bonuses_number);

        getPurchases();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_main);
    }

    @Override
    public void processResult(String result) {
        processPurchases(result);
    }

    private void getPurchases() {
        new ConnectionAsyncTask(this, "buyingGoodsInStore", this).execute();
    }

    private void processPurchases(String result) {
        List<Purchase> purchases = Purchase.parsePurchasesArray(result);

        int totalProducts = 0;
        int totalBonuses = 0;

        for (int i = 0; i < purchases.size(); ++i) {
            Purchase purchase = purchases.get(i);
            if (purchase.getStore().equals(storeId)) {
                List<String> products = purchase.getProducts();

                totalProducts += products.size();
                totalBonuses += purchase.getBonuses();
            }
        }

        productsTextView.setText(String.valueOf(totalProducts));
        bonusesTextView.setText(String.valueOf(totalBonuses));
    }


    private void openProfileActivity(){
        Intent intent = new Intent(this, StoreProfileActivity.class);
        Bundle b = new Bundle();
        b.putString("storeId", storeId);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openScanActivity(){
        Intent intent = new Intent(this, StoreScanActivity.class);
        Bundle b = new Bundle();
        b.putString("storeId", storeId);
        intent.putExtras(b);
        startActivity(intent);
    }

}
