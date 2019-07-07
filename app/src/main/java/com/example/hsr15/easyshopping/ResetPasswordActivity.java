package com.example.hsr15.easyshopping;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsr15.easyshopping.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    TextView title;
    EditText txtResetPhone, txtAnswer;
    Button Verify;
    String check = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        txtResetPhone = findViewById(R.id.reset_phone);
        txtAnswer = findViewById(R.id.reset_question);
        Verify = findViewById(R.id.verify);
        title = findViewById(R.id.page_title);

        check = getIntent().getStringExtra("check");


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (check.equals("Login")) {
            txtResetPhone.setVisibility(View.VISIBLE);

            Verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   VerifyUser();

                }
            });
        } else if (check.equals("Settings")) {

            title.setText("Set Security Answer");
            txtResetPhone.setVisibility(View.GONE);
            Verify.setText("Save Answer");

            displayPreviousAnswer();

            Verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setAnswer();

                }
            });

        }

    }


    private void  setAnswer(){

        String answer = txtAnswer.getText().toString().toLowerCase();

        if (txtAnswer.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please answer the Question..", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("answer", answer);

            reference.child("Security Answer")
                    .updateChildren(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "Answer Saved Successfully", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(ResetPasswordActivity.this, SettingsActivity.class);
                                startActivity(i);
                                finish();
                            }

                        }
                    });
        }

    }

    private void displayPreviousAnswer(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        reference.child("Security Answer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String ans = dataSnapshot.child("answer").getValue().toString();

                    txtAnswer.setText(ans);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void VerifyUser() {

        final String phone = txtResetPhone.getText().toString();
        final String answer = txtAnswer.getText().toString().toLowerCase();

        if(phone.equals("") || answer.equals("")){
            Toast.makeText(this, "Please Enter the data First.", Toast.LENGTH_SHORT).show();
        }else {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phone);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        String Phone = dataSnapshot.child("phone").getValue().toString();

                        if (dataSnapshot.hasChild("Security Answer")) {

                            String Answer = dataSnapshot.child("Security Answer").child("answer").getValue().toString();

                            if (Answer.equals(answer)) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");



                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Write New Password Here...");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (newPassword.getText().toString().equals("")) {
                                            Toast.makeText(ResetPasswordActivity.this, "Please Enter a Password..", Toast.LENGTH_SHORT).show();
                                        } else {
                                            reference.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(ResetPasswordActivity.this, "Password Changed Successfully.", Toast.LENGTH_SHORT).show();

                                                                Intent i = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();
                                    }
                                });

                                builder.show();


                            } else if(!Answer.equals(answer)){
                                Toast.makeText(ResetPasswordActivity.this, "Sorry! \nWrong Answer.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "SORRY! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




    }

}
