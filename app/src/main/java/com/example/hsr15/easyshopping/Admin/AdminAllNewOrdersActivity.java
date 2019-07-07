package com.example.hsr15.easyshopping.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hsr15.easyshopping.Model.AdminOrders;
import com.example.hsr15.easyshopping.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAllNewOrdersActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_new_orders);

        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders");

        orderList = findViewById(R.id.order_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(orderRef, AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {

                        holder.userName.setText("Name: " +model.getName());
                        holder.userPhoneNumber.setText("Phone: " +model.getPhone());
                        holder.orderTotalPrice.setText("Total Amount: " +model.getTotalAmount());
                        holder.orderDateTime.setText("Order At: " +model.getDate() +" " + model.getTime());
                        holder.orderShippingAddress.setText("Shipping Address: " +model.getName() + " " + model.getCity());

                        holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(AdminAllNewOrdersActivity.this,AdminOrderProductsDetailsActivity.class);
                                intent.putExtra("uid" ,uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Yes",
                                        "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminAllNewOrdersActivity.this);
                                builder.setTitle("Have you shipped this order products?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            String uID = getRef(position).getKey();
                                            removeOrder(uID);
                                        }else{
                                                finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_layout,viewGroup, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        orderList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userPhoneNumber, orderTotalPrice, orderDateTime, orderShippingAddress;
        public Button ShowOrdersBtn;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            orderTotalPrice = itemView.findViewById(R.id.order_total_price);
            orderDateTime = itemView.findViewById(R.id.order_date_time);
            orderShippingAddress = itemView.findViewById(R.id.order_address_city);
            ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);

        }
    }

    private void removeOrder(String uID) {

        orderRef.child(uID).removeValue();
    }
}
