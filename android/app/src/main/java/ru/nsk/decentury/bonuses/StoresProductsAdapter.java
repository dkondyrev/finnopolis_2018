package ru.nsk.decentury.bonuses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import ru.nsk.decentury.bonuses.hyperledger.ProductPriceInfo;


public class StoresProductsAdapter extends RecyclerView.Adapter<StoresProductsAdapter.ProductsListViewHolder> {
    private List<ProductPriceInfo> products;
    private StoresActivity activity;

    public static class ProductsListViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productPrice;
        public CheckBox checkBox;

        public ProductsListViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            productName = (TextView) v.findViewById(R.id.product_name);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            checkBox = (CheckBox) v.findViewById(R.id.product_checkbox);
        }
    }

    public StoresProductsAdapter(List<ProductPriceInfo> productsList, StoresActivity act) {
        products = productsList;
        activity = act;
    }

    public void setCheckBox(int position, boolean value) {
        activity.setCheckBox(position, value);
    }

    @Override
    public StoresProductsAdapter.ProductsListViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_list_layout, parent, false);

        StoresProductsAdapter.ProductsListViewHolder vh = new StoresProductsAdapter.ProductsListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(StoresProductsAdapter.ProductsListViewHolder holder, int position) {
        ProductPriceInfo product = products.get(position);
        holder.productName.setText(product.product);
        holder.productPrice.setText(String.valueOf(product.price));

        final int p = position;

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckBox(p, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}