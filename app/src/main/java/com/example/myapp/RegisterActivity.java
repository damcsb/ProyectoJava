package com.example.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth fbauth;
    private FirebaseDatabase fbdatabase;
    private FirebaseStorage fStorage;

    private EditText TextEmail;
    private EditText TextPassword;
    private EditText TextUsername;

    private Button btnRegister;

    private ProgressDialog progressDialog;

    private ImageView userImage;
    Uri imagePath;

    private StorageReference storageReference;

    private static int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //views

        fbauth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReference();

        userImage = findViewById(R.id.ProfileImage);

        TextEmail = findViewById(R.id.TxtEmail);
        TextPassword = findViewById(R.id.TxtPassword);
        TextUsername = findViewById(R.id.Txtusername);

        btnRegister = findViewById(R.id.Register);

        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(this);
        Button btn = findViewById(R.id.goLogin);


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), MainActivity.class);
                startActivityForResult(intent, 0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void registrarUsuario(){
        String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Put your email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Put your password", Toast.LENGTH_LONG).show();
            return;
        }
        if(imagePath == null){
            Toast.makeText(this, "Put an Image", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        fbauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            sendUserImageandUserName();
                            Toast.makeText(RegisterActivity.this,"Succesfully registered: "+TextEmail.getText(), Toast.LENGTH_LONG).show();
                        }else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(RegisterActivity.this, "An user already exists with this credentials", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(RegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View view){
        registrarUsuario();
    }

    private void sendUserImageandUserName() {
        String username = TextUsername.getText().toString().trim();
        String email = TextEmail.getText().toString().trim();

        StorageReference imageReference = storageReference.child(fbauth.getUid()).child("Images").child("Profile Pic");  //Ruta firebase//User id/Images/Profile Pic.jpg
        UploadTask uploadTask = imageReference.putFile(imagePath);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("Users").child(fbauth.getUid()).child("username");
        DatabaseReference myRef2 = database.getReference("Users").child(fbauth.getUid()).child("email");

        myRef.setValue(username);
        myRef2.setValue(email);

    }

}