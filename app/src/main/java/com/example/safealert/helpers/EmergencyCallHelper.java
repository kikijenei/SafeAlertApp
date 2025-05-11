package com.example.safealert.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class EmergencyCallHelper {
    private static final int CALL_PERMISSION_REQUEST = 100;
    private static final String EMERGENCY_NUMBER = "112";
    private CountDownTimer countDownTimer;
    private final Activity activity;
    private final TextView timerTextView;

    public EmergencyCallHelper(Activity activity, TextView timerTextView) {
        this.activity = activity;
        this.timerTextView = timerTextView;
    }

    public void startEmergencyCountdown() {
        Toast.makeText(activity, "Urgent call in 10 seconds.", Toast.LENGTH_SHORT).show();
        timerTextView.setVisibility(TextView.VISIBLE);

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                activity.runOnUiThread(() -> timerTextView.setText(String.valueOf(secondsLeft)));
            }

            @Override
            public void onFinish() {
                activity.runOnUiThread(() -> timerTextView.setVisibility(TextView.GONE));
                makeEmergencyCall();
            }
        }.start();
    }

    public void cancelEmergencyCall() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            activity.runOnUiThread(() -> {
                timerTextView.setText("10");
                timerTextView.setVisibility(TextView.GONE);
            });
        }
    }

    private void makeEmergencyCall() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST);
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + EMERGENCY_NUMBER));
        activity.startActivity(callIntent);
    }
}