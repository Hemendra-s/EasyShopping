package com.example.hsr15.easyshopping.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.hsr15.easyshopping.Inteface.ItemClickListner;
import com.example.hsr15.easyshopping.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductPrice, txtProductQuantity;
    private ItemClickListner itemClickListner;


    public CartViewHolder(@NonNull View itemView) {

        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity =itemView.findViewById(R.id.cart_product_quantity);

    }

    @Override
    public void onClick(View v) {

        itemClickListner.onClick(v,getAdapterPosition(),false);

    }

    public void setItemClickListner(ItemClickListner itemClickListner) {

        this.itemClickListner = itemClickListner;
    }
}
