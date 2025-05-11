package com.example.safealert.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.IBinder;
import android.os.Build;
import android.os.Bundle;
import android.content.ServiceConnection;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Button;
import android.Manifest;
import androidx.annotation.NonNull;
import com.example.safealert.BatteryLowReceiver;
import com.example.safealert.R;
import com.example.safealert.SOSReceiver;
import com.example.safealert.SensorInactivity;
import com.example.safealert.VolButtonReceiver;
import com.example.safealert.fragments.SOSFragment;
import com.example.safealert.helpers.LocationHelper;
import com.example.safealert.services.MessageService;
import com.example.safealert.services.VoiceRecognitionService;

public class MainActivity extends AppCompatActivity {
    Button btnStart;
    SOSReceiver sosReceiver;
    LocationHelper locationHelper;
    SensorInactivity sensorInactivity;
    BatteryLowReceiver batteryLowReceiver;
    Button buttonMap;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private final String[] permissions = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonMap = findViewById(R.id.btnMap);
        sensorInactivity = new SensorInactivity(this);

        locationHelper = new LocationHelper();
        locationHelper.requestLocationPosition(this);

        checkPermissions();

        buttonMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        btnStart = findViewById(R.id.btnStartApp);
        btnStart.setOnClickListener(v -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new SOSFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        batteryLowReceiver = new BatteryLowReceiver(this);
        batteryLowReceiver.updateBatteryStatus();
        batteryLowReceiver.registerBatteryReceiver();

        sosReceiver = new SOSReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(sosReceiver, filter);

        startService(new Intent(this, VoiceRecognitionService.class));

    }
    private void checkPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                return;
            }
        }
        locationHelper.requestLocationPosition(this);
    }

    //pentru buton - shortcut - eroare la emulator..


    VolButtonReceiver volButtonReceiver = new VolButtonReceiver();
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        registerReceiver(volButtonReceiver, filter);
        batteryLowReceiver.registerBatteryReceiver();
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(volButtonReceiver);
        batteryLowReceiver.unregisterBatteryReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sosReceiver);
        unregisterReceiver(sosReceiver);
        locationHelper.stopLocationUpdates();
    }

}
