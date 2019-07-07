package com.example.hsr15.easyshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsr15.easyshopping.Admin.AdminCategoryActivity;
import com.example.hsr15.easyshopping.Model.User;
import com.example.hsr15.easyshopping.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText InputPhoneNumber,InputPassword;
    private Button LoginButton;
    private ProgressDialog LoadingBar;
    private CheckBox chkBoxRememberMe;
    private String parentDbName = "Users";
    private TextView AdminLink,NotAdminLink,ForgetPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InputPhoneNumber = findViewById(R.id.login_phone_number_input);
        InputPassword = findViewById(R.id.login_password_input);
        LoginButton = findViewById(R.id.login_button);
        LoadingBar = new ProgressDialog(this);
        AdminLink = findViewById(R.id.admin_panel_link);
        NotAdminLink = findViewById(R.id.not_admin_panel_link);
        ForgetPassword = findViewById(R.id.forgot_password_link);

        chkBoxRememberMe = findViewById(R.id.remember_me);
        Paper.init(this);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admin");
                chkBoxRememberMe.setVisibility(View.GONE);
                ForgetPassword.setVisibility(View.GONE);
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";


            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login ");
                AdminLink.setVisibility(View.VISIBLE);
                chkBoxRememberMe.setVisibility(View.VISIBLE);
                ForgetPassword.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";

            }
        });

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check", "Login");
                startActivity(intent);
                finish();
            }
        });

    }

    private void LoginUser() {

        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(LoginActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        else{
            LoadingBar.setTitle("Login Account");
            LoadingBar.setMessage("Please wait, while we checking the credentials");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();

            AllowAccessToAccount(phone,password);
        }

    }

    private void AllowAccessToAccount(final String phone, final String password) {

        //checkbox coding

        if(chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        //login

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //if user exist
                if(dataSnapshot.child(parentDbName).child(phone).exists()){

                    User usersData = dataSnapshot.child(parentDbName).child(phone).getValue(User.class);

                    //Toast.makeText(LoginActivity.this, parentDbName, Toast.LENGTH_SHORT).show();

                    if(usersData.getPhone().equals(phone)){


                        if(usersData.getPassword().equals(password)){

                            //Login as Admin
                            if(parentDbName == "Admins"){
                                Toast.makeText(LoginActivity.this, "Login as Admin Successful...", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            //Login as a User

                            else if(parentDbName == "Users"){
                                Toast.makeText(LoginActivity.this, "Login Successful...", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                           }

                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Wrong Password !!!", Toast.LENGTH_SHORT).show();
                            LoadingBar.dismiss();

                        }
                    }

                }
                //if user not exist using given number
                else{

                    Toast.makeText(LoginActivity.this, "Account not Exist!!\nPlease check number or password", Toast.LENGTH_LONG).show();
                    LoadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
