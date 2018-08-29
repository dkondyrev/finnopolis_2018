package ru.nsk.decentury.bonuses;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


import ru.nsk.decentury.bonuses.connection.ConnectionAsyncTask;
import ru.nsk.decentury.bonuses.hyperledger.Buyer;

public class ProfileActivity extends AppCompatActivity implements ResponseProcessor {
    private Buyer buyer;

    private BottomNavigationView navigation;

    private TextView balanceTextView;
    private EditText phoneEditText;
    private EditText nameEditText;

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
                    openCardActivity();
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

        Bundle b = getIntent().getExtras();
        String buyerId = b.getString("buyerId");

        setContentView(R.layout.activity_profile);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_profile);

        balanceTextView = (TextView) findViewById(R.id.balance_text_view);
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);
        nameEditText = (EditText) findViewById(R.id.bonuses_edit_text);

        getUserInfo(buyerId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_profile);
    }


    @Override
    public void processResult(String result) {
        processUserInfo(result);
    }

    private void getUserInfo(String userId) {
        new ConnectionAsyncTask(this, "Buyer/" + userId, this).execute();
    }

    private void processUserInfo(String result) {
        buyer = new Buyer(result);

        nameEditText.setText(buyer.getId());
        phoneEditText.setText(buyer.getPhoneNumber());
        balanceTextView.setText(String.valueOf(buyer.getBalance()));
    }

    private void openStoresActivity(){
        Intent intent = new Intent(this, StoresActivity.class);
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
