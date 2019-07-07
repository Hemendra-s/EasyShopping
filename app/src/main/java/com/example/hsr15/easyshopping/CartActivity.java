package com.example.hsr15.easyshopping;

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
import android.widget.Toast;

import com.example.hsr15.easyshopping.Model.Cart;
import com.example.hsr15.easyshopping.Prevalent.Prevalent;
import com.example.hsr15.easyshopping.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private TextView txtTotalAmount,txtMsg1;
    private Button nextProcessBtn;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private int overAllTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        txtTotalAmount = findViewById(R.id.total_price);
        txtMsg1 = findViewById(R.id.msg1);
        nextProcessBtn = findViewById(R.id.next_process_btn);

        recyclerView = findViewById(R.id.cart_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);



        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtTotalAmount.setText(String.valueOf(overAllTotalPrice));

                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overAllTotalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone())
                                .child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                        holder.txtProductQuantity.setText(model.getQuantity());
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductPrice.setText( model.getPrice());

                        int oneTypeProductPrice = ((Integer.valueOf(model.getPrice()))) * ((Integer.valueOf(model.getQuantity())));

                        overAllTotalPrice = overAllTotalPrice + oneTypeProductPrice ;


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Edit",
                                        "Remove"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options:");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {
                                            Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if (which == 1) {

                                            cartListRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(CartActivity.this, "Item Removed Successfully", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout, viewGroup, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;

                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private void checkOrderState(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped")){
                        txtTotalAmount.setText("Dear " + userName+ ",\nOrder is shipped successfully.");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulation, Your final order is verified & Shipped. Soon you will receive your order at your door step.");
                        nextProcessBtn.setVisibility(View.GONE);


                    }
                    else if(shippingState.equals("not shipped")){
                        txtTotalAmount.setText("Please Wait your Order is under Verification :)");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
