package ru.nsk.decentury.bonuses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


import ru.nsk.decentury.bonuses.hyperledger.ProductInfo;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ProductsListViewHolder> {
    private List<ProductInfo> products;


    public static class ProductsListViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productStore;


        public ProductsListViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Element  " + getAdapterPosition() + " clicked.");
                }
            });


            productName = (TextView) v.findViewById(R.id.bonuses_text);
            productStore = (TextView) v.findViewById(R.id.bonuses_number);
        }
    }

    public ProductsListAdapter(List<ProductInfo> productsList) {
        products = productsList;
    }

    @Override
    public ProductsListAdapter.ProductsListViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bonuses_list_layout, parent, false);

        ProductsListViewHolder vh = new ProductsListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ProductsListViewHolder holder, int position) {
        ProductInfo product = products.get(position);
        holder.productName.setText(product.product);
        holder.productStore.setText(product.store);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}