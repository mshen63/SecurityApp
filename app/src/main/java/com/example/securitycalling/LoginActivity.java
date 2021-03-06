package com.example.securitycalling;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if (ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CallActivity.class);
                intent.putExtra("callerId", ((TextView) findViewById(R.id.callerId)).getText().toString());
                intent.putExtra("recipientId", ((TextView) findViewById(R.id.recipientId)).getText().toString());
                startActivity(intent);
            }
        });
    }

}
