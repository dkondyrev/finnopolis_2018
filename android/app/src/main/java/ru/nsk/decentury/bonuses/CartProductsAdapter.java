package ru.nsk.decentury.bonuses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.CartProductViewHolder> {
    private List<String> products;
    private List<Integer> prices;


    public static class CartProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productPrice;


        public CartProductViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product);
            productPrice = (TextView) v.findViewById(R.id.price);
        }
    }

    public CartProductsAdapter(List<String> productsList, List<Integer> pricesList) {
        products = productsList;
        prices = pricesList;
    }

    @Override
    public CartProductsAdapter.CartProductViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_product_layout, parent, false);

        CartProductsAdapter.CartProductViewHolder vh = new CartProductsAdapter.CartProductViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CartProductsAdapter.CartProductViewHolder holder, int position) {
        holder.productName.setText(products.get(position));
        holder.productPrice.setText(String.valueOf(prices.get(position)));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}