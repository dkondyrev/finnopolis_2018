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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ru.nsk.decentury.bonuses.connection.ConnectionAsyncTask;
import ru.nsk.decentury.bonuses.hyperledger.Buyer;
import ru.nsk.decentury.bonuses.hyperledger.Product;
import ru.nsk.decentury.bonuses.hyperledger.ProductInfo;
import ru.nsk.decentury.bonuses.hyperledger.ProductPriceInfo;
import ru.nsk.decentury.bonuses.hyperledger.Purchase;
import ru.nsk.decentury.bonuses.hyperledger.Store;

public class StoresActivity extends AppCompatActivity implements ResponseProcessor {
    private String buyerId;

    private List<Product> products;
    private List<Store> stores;
    private String[] storesNames;
    private int currentStoreIndex = 0;
    private List<ProductPriceInfo> currentProducts;

    private BottomNavigationView navigation;

    private Spinner storesSpinner;
    private ArrayAdapter<String> spinnerAdapter;

    private RecyclerView productsRecyclerView;
    private RecyclerView.Adapter productsAdapter;
    private RecyclerView.LayoutManager productsLayoutManager;

    private Button cartButton;

    enum RequestType {STORES, PRODUCTS}
    private RequestType currentRequest;

    private List<Boolean> checkBox;

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
        currentProducts = new ArrayList<>();
        checkBox = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        buyerId = b.getString("buyerId");

        setContentView(R.layout.activity_stores);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_stores);

        storesSpinner = (Spinner) findViewById(R.id.stores_spinner);
        storesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currentStoreIndex = position;
                System.out.println("ITEM SELECTED. Position: " + position);
                changeProductsList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        productsRecyclerView = (RecyclerView) findViewById(R.id.stores_products_list);
        productsRecyclerView.setHasFixedSize(true);
        productsLayoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(productsLayoutManager);

        cartButton = (Button) findViewById(R.id.cart_button);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCartActivity();
            }
        });

        getStores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_stores);
    }


    public void setCheckBox(int position, boolean value) {
        checkBox.set(position, value);
        System.out.println("CheckBox: " + position + " " + value);
    }


    @Override
    public void processResult(String result) {
        switch (currentRequest) {
            case STORES:
                processStores(result);
                break;
            case PRODUCTS:
                processProducts(result);
                break;
        }
    }

    private void getProducts() {
        new ConnectionAsyncTask(this, "Product", this).execute();
        currentRequest = RequestType.PRODUCTS;
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

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storesNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storesSpinner.setAdapter(spinnerAdapter);

        getProducts();
    }

    private void processProducts(String result) {
        products = Product.parseProductsArray(result);
        changeProductsList();
    }

    private void changeProductsList() {
        if (products == null) {
            return;
        }

        Store store = stores.get(currentStoreIndex);
        String storeName = storesNames[currentStoreIndex];
        currentProducts = new ArrayList<>();
        checkBox = new ArrayList<>();


        for (int i = 0; i < products.size(); ++i) {
            Product product = products.get(i);
            if (product.getOwner().equals(storeName)) {
                String name = product.getId();
                int price = store.getPrice(product.getVendorCode());
                currentProducts.add(new ProductPriceInfo(name, price));
                checkBox.add(false);
            }
        }

        productsAdapter = new StoresProductsAdapter(currentProducts, this);
        productsRecyclerView.setAdapter(productsAdapter);
    }

    private void openCartActivity() {
        ArrayList<String> cartProducts = new ArrayList<>();
        ArrayList<Integer> prices = new ArrayList<>();

        for (int i = 0; i < checkBox.size(); ++i) {
            if(checkBox.get(i)) {
                cartProducts.add(currentProducts.get(i).product);
                prices.add(currentProducts.get(i).price);
            }
        }

        Intent intent = new Intent(this, CartActivity.class);
        Bundle b = new Bundle();
        b.putStringArrayList("products", cartProducts);
        b.putIntegerArrayList("prices", prices);
        b.putString("buyerId", buyerId);
        b.putString("storeId", stores.get(currentStoreIndex).getId());
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

    private void openExchangeActivity(){
        Intent intent = new Intent(this, ExchangeActivity.class);
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

    private void openCardActivity(){
        Intent intent = new Intent(this, CardActivity.class);
        Bundle b = new Bundle();
        b.putString("buyerId", buyerId);
        intent.putExtras(b);
        startActivity(intent);
    }

}
