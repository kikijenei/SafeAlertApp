package com.example.safealert.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.safealert.helpers.LocationHelper;

public class WeatherALERTService extends Service {
    Context context;
    LocationHelper locationHelper;
    public WeatherALERTService(Context context, LocationHelper locationHelper) {
        this.context = context;
        this.locationHelper = locationHelper;
    }
    public void checkWeatherAndSendAlert() {
        new Thread(() -> {
            double latitude = locationHelper.getLatitude();
            double longitude = locationHelper.getLongitude();
            String alertMessage = WeatherService.getWeather(latitude, longitude);

            if (alertMessage != null) {
                sendWeatherNotification(alertMessage);
            }
        }).start();
    }
    private void sendWeatherNotification(String message) {
        String channelId = "weather_alerts";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Meteo alert", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Meteo Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}