package com.example.android.twoactivities;

import android.widget.ImageView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.concurrent.ExecutionException;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.UserProfileChangeRequest;

import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;


public class CallActivity extends AppCompatActivity {
    FirebaseDatabase database;

    DatabaseReference users;

    private EditText usernames;
    private TextView nameCallerDisplay;
    private String recipient_name;

    private EditText emails;

    private EditText passwords;

    private EditText names;
    private ImageView profilePico;
    ImageButton btnRegister;

    FirebaseAuth firebaseAuth;

    private static final String APP_KEY = "133ed0e4-4d34-423c-8d15-330825736eee";
    private static final String APP_SECRET = "4X4yoBpjO0GIyYw9moOh3w==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private TextView callState;
    private SinchClient sinchClient;
    private Button button;
    private String callerId;
    private String recipientId;
    private double lat;
    private double lng;
    private HashMap<String, Double> candidates = new HashMap<String, Double>();

    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("call", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user_collection").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        GeoPoint point = document.getGeoPoint("location");
                         lat = point.getLatitude();
                         lng = point.getLongitude();
                    } else {
                        Log.d("DATABASE", "No such document");
                    }
                } else {
                    Log.d("DATABASE", "get failed with ", task.getException());
                }
            }
        });
        db.collection("user_collection")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.getData().get("name").toString().equals(name)){
                                    GeoPoint point = document.getGeoPoint("location");
                                    double lat2 = point.getLatitude();
                                    double lng2 = point.getLongitude();
                                    double dist = distance(lat, lng, lat2, lng2);
                                    if(dist <= 0.5){
                                        candidates.put(document.getData().get("name").toString(), dist);
                                        Log.d("DATABASE", candidates.toString());
                                    }

                                }

                            }
                        } else {
                            Log.d("DATABASE", "Error getting documents: ", task.getException());
                        }
                    }
                });


        createNotificationChannel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

//        Intent intent = getIntent();
//        callerId = intent.getStringExtra("callerId");
//        recipientId = intent.getStringExtra("recipientId");
        callerId = "b";
        recipientId = "a";
        profilePico = findViewById(R.id.profilePic);
        nameCallerDisplay = findViewById(R.id.nameCallerDisplay);
        recipient_name = "Bob";

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(callerId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
//        sinchClient.setSupportManagedPush(true);
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        button = (Button) findViewById(R.id.button);
        callState = (TextView) findViewById(R.id.callState);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button)view;
                String buttonText = b.getText().toString();
                if (buttonText.equals("Accept Incoming Call")){
                    Log.d("CALL","accepted call");
                    call.answer();
                    call.addCallListener(new SinchCallListener());
                    button.setText("Hang Up");
                } else if (call == null) {
                    call = sinchClient.getCallClient().callUser(recipientId);
                    sinchClient.getAudioController().enableAutomaticAudioRouting(false, AudioController.UseSpeakerphone.SPEAKERPHONE_AUTO);
                    call.addCallListener(new SinchCallListener());
                    button.setText("Hang Up");
                } else {
                    profilePico.setVisibility(View.INVISIBLE);
                    call.hangup();
                    nameCallerDisplay.setText("Call ended.");

                }
            }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            profilePico.setVisibility(View.INVISIBLE);
            nameCallerDisplay.setText("Call ended.");
            call = null;
            SinchError a = endedCall.getDetails().getError();
            button.setText("Call");
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            profilePico.setVisibility(View.VISIBLE);
            nameCallerDisplay.setText("Calling " + recipient_name);
            callState.setText("connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
            Log.d("DEBUG", callClient.toString());
            Log.d("DEBUG", incomingCall.toString());
            sendNotification();

            call = incomingCall;
            button.setText("Accept Incoming Call");
            profilePico.setVisibility(View.VISIBLE);
            nameCallerDisplay.setText("Call from " + recipient_name);

            Runnable delayedDecline = new Runnable() {
                @Override
                public void run() {
                    if(button.getText().equals("Accept Incoming Call")){
                        button.setText("CALL");
                        call.hangup();
                        call = null;
                    }

                }
            };
            mainThreadHandler.postDelayed(delayedDecline, 10000);

        }
    }
    public void sendNotification(){
        int requestID = (int) System.currentTimeMillis();
        Intent intent = new Intent(this, PingingPage.class);
        intent.putExtra("callerId", callerId);
        intent.putExtra("recipientId", recipientId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "call")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("CALL FOR HELP")
                .setContentText("Call for help from someone nearby")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);
//                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

    }

    public float distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }
}
