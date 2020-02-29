package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialActivity extends AppCompatActivity {

    private ImageView imageback;

    private CircleImageView userPic;
    private TextView msgusername;
    private EditText msgMessage;
    private Button btnSend;
    private RecyclerView rvMessages;
    private Adapter_Messages adapter;
    private ProgressBar spinner;

    private FirebaseDatabase mDatabase;
    private FirebaseAuth fbauth;
    private FirebaseStorage firebaseStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        //
        //
        ////
        userPic = findViewById(R.id.profilechatpic);
        msgusername = findViewById(R.id.usernamechat);
        msgMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSendMessage);
        rvMessages = findViewById(R.id.containerchat);
        spinner = findViewById(R.id.ProfileSpin);
        //
        ////
        LinearLayoutManager l = new LinearLayoutManager(this);
        //
        ////
        adapter = new Adapter_Messages(this);
        rvMessages.setLayoutManager(l);
        rvMessages.setAdapter(adapter);
        //
        ////Firebase
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = mDatabase.getReference("chat");
        DatabaseReference databaseReferenceUsername = mDatabase.getReference("Users");
        fbauth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storage = firebaseStorage.getReference();
        //
        ////Read and Set Username from database
        databaseReferenceUsername.child(fbauth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //
                    ////Get Value from Database
                    String username = dataSnapshot.child("username").getValue().toString();
                    msgusername.setText(username);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        //
        ////Read IMG from database
        storage.child(fbauth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(userPic);
                spinner.setVisibility(View.INVISIBLE); //Spinner off
            }
        });

        //
        ////Method start-position chat
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount){
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });
                //
        ////Back Page Arrow Image
        imageback = findViewById(R.id.logoback);

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), HomeActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        //
        ////Send Message
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(msgMessage.getText().toString().matches("")){
                    Toast.makeText(SocialActivity.this, "Write a Message", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    databaseReference.push().setValue(new Message(msgMessage.getText().toString(), msgusername.getText().toString()));
                    msgMessage.setText(null);
                }
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                adapter.addMessage(m);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //
    ///Method scroll last-msg
    private void setScrollbar() {
        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
    }
}
