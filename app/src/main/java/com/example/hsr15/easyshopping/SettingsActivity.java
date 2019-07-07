package com.example.hsr15.easyshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsr15.easyshopping.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextButton;
    private Button securityAnswer;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = findViewById(R.id.settings_profile_image);
        fullNameEditText = findViewById(R.id.settings_full_name);
        userPhoneEditText = findViewById(R.id.settings_phone_number);
        addressEditText = findViewById(R.id.settings_address);
        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings);
        saveTextButton = findViewById(R.id.update_account_settings);
        securityAnswer = findViewById(R.id.security_question_btn);


        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checker.equals("clicked")) {

                    userInfoSave();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });

        securityAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                i.putExtra("check", "Settings");
                startActivity(i);
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        } else {

            Toast.makeText(this, "Error: Try Again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSave() {

        if (TextUtils.isEmpty(fullNameEditText.getText().toString())) {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())) {
            Toast.makeText(this, "Phone Number is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("update Profile Photo");
        loadingBar.setMessage("Please WAIT !");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if (imageUri != null) {

            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();

                        userMap.put("name", fullNameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        loadingBar.dismiss();

                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile Info Update Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "image is NOT selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(this, "Profile Info Update Successfully", Toast.LENGTH_SHORT).show();
        finish();

    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("name").exists()) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        fullNameEditText.setText(name);
                    }
                    if (dataSnapshot.child("phone").exists()) {
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        userPhoneEditText.setText(phone);
                    }
                    if (dataSnapshot.child("address").exists()) {
                        String address = dataSnapshot.child("address").getValue().toString();
                        addressEditText.setText(address);
                    }


                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
