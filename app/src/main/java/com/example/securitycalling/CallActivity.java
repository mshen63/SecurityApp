package com.example.securitycalling;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

public class CallActivity extends AppCompatActivity {

    private static final String APP_KEY = "133ed0e4-4d34-423c-8d15-330825736eee";
    private static final String APP_SECRET = "4X4yoBpjO0GIyYw9moOh3w==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private TextView callState;
    private SinchClient sinchClient;
    private Button button;
    private String callerId;
    private String recipientId;
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
        createNotificationChannel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        Intent intent = getIntent();
        callerId = intent.getStringExtra("callerId");
        recipientId = intent.getStringExtra("recipientId");

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
                    call.hangup();
                }
            }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
            button.setText("Call");
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
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

            sendNotification();

            call = incomingCall;
            Toast.makeText(CallActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
            button.setText("Accept Incoming Call");
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
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("callerId", callerId);
        intent.putExtra("recipientId", recipientId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "call")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("INCOMING CALL")
                .setContentText("Call for help from someone nearby")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

    }


}
