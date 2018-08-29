package ru.nsk.decentury.bonuses.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ru.nsk.decentury.bonuses.R;
import ru.nsk.decentury.bonuses.ResponseProcessor;
import ru.nsk.decentury.bonuses.connection.ConnectionAsyncTask;
import ru.nsk.decentury.bonuses.hyperledger.Purchase;
import ru.nsk.decentury.bonuses.hyperledger.Store;

public class StoreProfileActivity extends AppCompatActivity implements ResponseProcessor {
    private String storeId;
    private Store store;

    private BottomNavigationView navigation;

    private EditText companyNameText;
    private EditText storeNameText;
    private TextView balanceTextView;
    private EditText bonusCoefficientText;
    private EditText maxCoefficientText;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    openScanActivity();
                    return true;
                case R.id.navigation_main:
                    openMainActivity();
                    return true;
                case R.id.navigation_profile:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);

        Bundle b = getIntent().getExtras();
        storeId = b.getString("storeId");

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_profile);

        storeNameText = (EditText) findViewById(R.id.store_name);
        storeNameText.setText(storeId);

        companyNameText = (EditText) findViewById(R.id.company_name);
        balanceTextView = (TextView) findViewById(R.id.balance);
        bonusCoefficientText = (EditText) findViewById(R.id.bonus_coefficient);
        maxCoefficientText = (EditText) findViewById(R.id.max_coefficient);

        getStore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_profile);
    }

    @Override
    public void processResult(String result) {
        processStore(result);
    }

    private void getStore() {
        new ConnectionAsyncTask(this, "Store/" + storeId, this).execute();
    }

    private void processStore(String result) {
        store = new Store(result);

        companyNameText.setText(store.getCompanyName());
        balanceTextView.setText(String.valueOf(store.getBalance()));
        bonusCoefficientText.setText(String.valueOf(store.getBonusCoefficient()));
        maxCoefficientText.setText(String.valueOf(store.getMaxBonuses()));
    }


    private void openScanActivity(){
        Intent intent = new Intent(this, StoreScanActivity.class);
        Bundle b = new Bundle();
        b.putString("storeId", storeId);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openMainActivity(){
        Intent intent = new Intent(this, StoreMainActivity.class);
        Bundle b = new Bundle();
        b.putString("storeId", storeId);
        intent.putExtras(b);
        startActivity(intent);
    }
}
