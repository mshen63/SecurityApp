package com.example.android.twoactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE="com.example.android.twoactivities.extra.MESSAGE";

    private EditText etEmail;
    private EditText etPassword;
    Button btnLogin;
    Button btnRegister;

    private FirebaseAuth.AuthStateListener authStateListener;
    ProgressBar progressBar;

    public static final int TEXT_REQUEST=1;

    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        etEmail =(EditText)findViewById(R.id.editText_username);
        etPassword = (EditText)findViewById(R.id.editText_password);
        btnLogin = (Button)findViewById(R.id.button_main);
        btnRegister = (Button)findViewById(R.id.button_sign_up);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent home = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(home);
                } else{
                    Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };
       // progressBar = (ProgressBar)findViewById(R.id.progressLog);
        //mReplyHeadTextView=findViewById(R.id.text_header_reply);
        //mReplyTextView=findViewById(R.id.text_message_reply);
        /*if (savedInstanceState!=null){
            boolean isVisible = savedInstanceState.getBoolean("reply_visible");
            if (isVisible){
                mReplyHeadTextView.setVisibility((View.VISIBLE));
                mReplyTextView.setText(savedInstanceState.getString("reply_text"));
                mReplyTextView.setVisibility(View.VISIBLE);
            }
        }*/
        Log.d(LOG_TAG, "-------");
        Log.d(LOG_TAG, "onCreate");

    }

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

    public void launchSecondActivity(View view) {


        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (email.isEmpty()){
            etEmail.setError("Please enter a valid Email Address!");
            etEmail.requestFocus();
        } else if(password.isEmpty()){
            etPassword.setError("Please enter your password!");
            etPassword.requestFocus();
        } else if (email.isEmpty() && password.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter a valid email address and password!", Toast.LENGTH_SHORT).show();
        } else if (!(email.isEmpty() && !password.isEmpty())){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else{
                        Intent dashboard = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(dashboard);
                    }
                }
            });
        } else{
            Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
    }


    public void launchSignUpActivity(View view) {
        Log.d(LOG_TAG, "Button clicked!");
        Intent signup=new Intent(this, SignUpActivity.class);
        startActivity(signup);
        // startActivityForResult(intent, TEXT_REQUEST);
    }



    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mReplyHeadTextView.getVisibility() == View.VISIBLE){
            outState.putBoolean("reply_visible", true);
            outState.putString("reply_text", mReplyTextView.getText().toString());
        }
    }*/


    /*@Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply =
                        data.getStringExtra(SecondActivity.EXTRA_REPLY);
                mReplyHeadTextView.setVisibility(View.VISIBLE);
                mReplyTextView.setText(reply);
                mReplyTextView.setVisibility(View.VISIBLE);
            }
        }
    }*/
}