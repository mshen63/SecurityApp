package com.example.android.twoactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;

public class PingingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinging_page);
    }

    public void goToMap(View view) {
//        Intent login=new Intent(this, MapsActivity.class);
//        startActivity(login);
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/place/33%C2%B046'23.6%22N+84%C2%B023'35.7%22W/@33.7732144,-84.3954297,17z/data=!3m1!4b1!4m6!3m5!1s0x0:0x0!7e2!8m2!3d33.7732104!4d-84.3932408");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        startActivity(mapIntent);
    }


}