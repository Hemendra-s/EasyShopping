package com.example.hsr15.easyshopping.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hsr15.easyshopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.Button;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private ImageView productImage;
    private EditText productName, productDescription, productPrice;
    private Button applyChanges,deleteProduct;
    private String productID = "";
    private ProgressDialog loadingBar;
    DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productID = getIntent().getStringExtra("pid");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);


        productImage = findViewById(R.id.maintain_product_image);
        productName = findViewById(R.id.maintain_product_name);
        productDescription = findViewById(R.id.maintain_product_description);
        productPrice = findViewById(R.id.maintain_product_price);
        applyChanges = findViewById(R.id.apply_changes_product_btn);
        deleteProduct = findViewById(R.id.delete_product_btn);
        loadingBar = new ProgressDialog(this);

        displaySpecificProductInfo();

        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChange();
            }
        });
        
        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Processing..");
                loadingBar.setMessage("Please wait, while we remove this product from Database...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                deleteProductFromDatabase();
            }
        });

    }

    private void deleteProductFromDatabase() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();

                    Intent intent = new Intent(AdminMaintainProductsActivity.this,AdminCategoryActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(AdminMaintainProductsActivity.this, "Product Remove Successfully :)", Toast.LENGTH_SHORT).show();


                }else{
                    Toast.makeText(AdminMaintainProductsActivity.this, "ERROR!!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }


    private void applyChange() {

        String name = productName.getText().toString();
        String description = productDescription.getText().toString();
        String price = productPrice.getText().toString();

        if(name.equals("")){
            Toast.makeText(this, "Write product name.", Toast.LENGTH_SHORT).show();
        }
        else if(description.equals("")){
            Toast.makeText(this, "Write product description.", Toast.LENGTH_SHORT).show();
        }
        else if(price.equals("")){
            Toast.makeText(this, "Write product price.", Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("description",description);
            productMap.put("price",price);
            productMap.put("pname",name);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductsActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminMaintainProductsActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });

        }

    }

    private void displaySpecificProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();

                    productName.setText(pName);
                    productDescription.setText(pDescription);
                    productPrice.setText(pPrice);
                    Picasso.get().load(pImage).into(productImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
