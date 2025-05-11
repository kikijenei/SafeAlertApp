package com.example.safealert;

import static com.example.safealert.FavContacts.getFavContacts;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import android.hardware.SensorEventListener;

import com.example.safealert.helpers.SMSHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class SensorInactivity implements SensorEventListener {
    SensorManager sensorManager;
    Sensor accelerometer, gyroscope;
    Handler handler;
    Runnable runnable;
    Context context;
    private boolean isMoving = false;
    private final long inactivityTime = 10 * 60 * 1000;//10 minute

    public SensorInactivity(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        handler = new Handler();
        startMonitoring();
    }
    private void startMonitoring() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        resetInactivityTimer();
    }
    private void resetInactivityTimer() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isMoving) {
                    sendInactiveAlert();
                }
            }
        };
        handler.postDelayed(runnable, inactivityTime);
    }

    private void sendInactiveAlert() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String message = "I have not been active for..: " + (inactivityTime / 60000) + " minutes. " +
                                "My location is: https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        sendSmsToFavorites(message);
                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendSmsToFavorites(String message) {
        List<String> contacts = getFavContacts(context);
        SmsManager smsManager = SmsManager.getDefault();
        for (String contact : contacts) {
            smsManager.sendTextMessage(contact, null, message, null, null);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float threshold = 0.5f;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER || event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float movement = Math.abs(x) + Math.abs(y) + Math.abs(z);
            if (movement > threshold) {
                isMoving = true;
                resetInactivityTimer();
            } else {
                isMoving = false;
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}