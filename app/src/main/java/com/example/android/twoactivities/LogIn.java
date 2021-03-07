package com.example.android.twoactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {
    private static final String LOG_TAG = LogIn.class.getSimpleName();

    EditText etEmail;
    EditText etPassword;
    ImageButton btnLogin;

    FirebaseAuth.AuthStateListener authStateListener;

    FirebaseAuth firebaseAuth;


    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "onStart");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();

        etEmail =(EditText)findViewById(R.id.editText_username);
        etPassword = (EditText)findViewById(R.id.editText_password);
        btnLogin = (ImageButton)findViewById(R.id.button_log_in);


        authStateListener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                Toast.makeText(LogIn.this, "WRONG WAY", Toast.LENGTH_SHORT).show();

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    //Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent home = new Intent(LogIn.this, SecondActivity.class);
                    startActivity(home);
                } else{
                    Toast.makeText(LogIn.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        Log.d(LOG_TAG, "-------");
        Log.d(LOG_TAG, "onCreate");

    }


    public void LogInActivity(View view) {
//        Toast.makeText(LogIn.this, "I got here", Toast.LENGTH_SHORT).show();


        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (email.isEmpty()){
            etEmail.setError("Please enter a valid Email Address!");
            etEmail.requestFocus();
        } else if(password.isEmpty()){
            etPassword.setError("Please enter your password!");
            etPassword.requestFocus();
        } else if (email.isEmpty() && password.isEmpty()){
            Toast.makeText(LogIn.this, "Please enter a valid email address and password!", Toast.LENGTH_SHORT).show();
        } else if (!(email.isEmpty() && !password.isEmpty())){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LogIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    } else{
                        Intent dashboard = new Intent(LogIn.this, SecondActivity.class);
                        startActivity(dashboard);
                        firebaseAuth.addAuthStateListener(authStateListener);
                    }
                }
            });
        } else{
            Toast.makeText(LogIn.this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
    }
}