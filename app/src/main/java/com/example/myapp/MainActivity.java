package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //
    ////
    private EditText Txtemail;
    private EditText Txtpassword;
    private Button btnLogin;
    //
    ////SpinnerDialog
    private ProgressDialog progressDialog;
    //
    ////
    private FirebaseAuth fbauth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        ////Firebase Auth Instace
        fbauth = FirebaseAuth.getInstance();
        //
        ////
        Txtemail = findViewById(R.id.loguser);
        Txtpassword = findViewById (R.id.logpassword);
        btnLogin = findViewById(R.id.Login);
        //
        ////
        progressDialog = new ProgressDialog(this);
        //
        ////
        btnLogin.setOnClickListener(this);
        //
        ////Go To Register
        Button btn = findViewById(R.id.goRegistro);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), RegisterActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }
    //
    ////Login user method
    private void logearusuario(){
        final String email = Txtemail.getText().toString().trim();
        final String password = Txtpassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Put your email", Toast.LENGTH_LONG).show();   //if there isnt a email
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Put your password", Toast.LENGTH_LONG).show();//if there isnt a password
            return;
        }

        progressDialog.setMessage("Logging...");
        progressDialog.show();


        fbauth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Welcome!",Toast.LENGTH_LONG).show();
                            //
                            ///Go to HomeActivity
                            Intent intencion = new Intent(getApplication(), HomeActivity.class);
                            startActivity(intencion);
                        }else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(MainActivity.this, "Your credentials are incorrect", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(MainActivity.this, "Login Failed, Try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                        progressDialog.hide(); //Hide spinner
                    }
                });

    }

    @Override
    public void onClick(View view){
        logearusuario();
    } //Wait the click for execute the method
}
