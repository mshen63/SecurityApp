package com.example.android.twoactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecondActivity extends AppCompatActivity {

    Button signOut;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    TextView username;

    private static final String LOG_TAG = SecondActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_second);
        //mReply=findViewById(R.id.editText_second);
        signOut = (Button)findViewById(R.id.button_sign_out);
        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        username = (TextView) findViewById(R.id.welcome_message);
      //  Intent intent = getIntent();
 //       String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

     //
        String name = user.getDisplayName();
        username.setText("Welcome back "+name+"!");
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(SecondActivity.this, MainActivity.class));
                    finish();
                }
            }
        };


    }



    public void launchHelpActivity(View view) {

        Log.d(LOG_TAG, "Button clicked!");
        Intent intent=new Intent(this, PingingPage.class);
        startActivity(intent);
    }

    public void onSignOut(View view) {

        auth.signOut();
        startActivity(new Intent(SecondActivity.this, MainActivity.class));

    }

   /* public void returnReply(View view) {
        String reply = mReply.getText().toString();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, reply);
        setResult(RESULT_OK, replyIntent);
        Log.d(LOG_TAG, "End SecondActivity");
        finish();
    }*/
}