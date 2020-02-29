package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    ////
    private ImageView UserPicture;
    private TextView UserName, UserEmail;
    private ProgressBar spinner;
    private Button btnLogout;
    //
    private Button button;

    ////
    private FirebaseDatabase mDatabase;
    private FirebaseAuth fbAuth;
    private FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //
        ////
        UserPicture = findViewById(R.id.UserPicture);
        UserName = findViewById(R.id.UserName);
        UserEmail = findViewById(R.id.UserEmail);
        spinner = findViewById(R.id.spinnerPicture);
        btnLogout = findViewById(R.id.logout_button);
        //
        button = findViewById(R.id.gotoposts);
        //
        ////Firebase Instances
        fbAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        //
        ////References Database and Storage
        DatabaseReference databaseReference = mDatabase.getReference("Users");
        StorageReference storageReference = firebaseStorage.getReference();
        //
        ////Show Profile Picture
        storageReference.child(fbAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(UserPicture);
                spinner.setVisibility(View.INVISIBLE); //Spinner off
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), SocialActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        databaseReference.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //
                    ////Get Values from Database
                    String username = dataSnapshot.child("username").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    UserEmail.setText(email);
                    UserName.setText(username);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });

    }

    private void Logout(){
        fbAuth.signOut();
        finish();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
    }

}
