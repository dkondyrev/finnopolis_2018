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

public class CardActivity extends AppCompatActivity implements ResponseProcessor {
    private String buyerId;

    private BottomNavigationView navigation;

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
                    openMainActivity();
                    return true;
                case R.id.navigation_card:
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
        buyerId = b.getString("buyerId");

        setContentView(R.layout.activity_card);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_card);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_card);
    }


    @Override
    public void processResult(String result) {

    }

    private void openStoresActivity(){
        Intent intent = new Intent(this, StoresActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyerId);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyerId);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyerId);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openExchangeActivity(){
        Intent intent = new Intent(this, ExchangeActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyerId);
        intent.putExtras(b);
        startActivity(intent);
    }

}
