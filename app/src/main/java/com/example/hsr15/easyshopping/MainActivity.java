package com.example.hsr15.easyshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hsr15.easyshopping.Model.User;
import com.example.hsr15.easyshopping.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        joinNowButton = findViewById(R.id.main_join_now_button);
        loginButton = findViewById(R.id.main_login_button);
        loadingBar = new ProgressDialog(this);

        //Paper is a github library used to store data in mobile itself
        Paper.init(this);

        //to go to Login Page when user touch login button

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);

            }
        });

        //to go to register activity when user touch join now

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);

            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "") {

            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {

                loadingBar.setTitle("Already Logged In :)");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.setMessage("Please wait...");
                loadingBar.show();

                AllowAccess(UserPhoneKey, UserPasswordKey);

            }
        }

    }

    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //if user exist
                if (dataSnapshot.child("Users").child(phone).exists()) {

                    User usersData = dataSnapshot.child("Users").child(phone).getValue(User.class);

                    if (usersData.getPhone().equals(phone)) {


                        if (usersData.getPassword().equals(password)) {

                            Toast.makeText(MainActivity.this, "Please Wait...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password !!!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                    }

                }
                //if user not exist using given number
                else {

                    Toast.makeText(MainActivity.this, "Account not Exist!!\nPlease check number or password", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
