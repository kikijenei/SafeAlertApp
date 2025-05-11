package com.example.safealert.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.example.safealert.helpers.LocationHelper;
import com.example.safealert.helpers.SMSHelper;

import java.util.Calendar;

public class MessageService extends Service {
    private static final String TAG = "ScheduledMessageService";
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_DELAY_MINUTES = "extra_delay_minutes";
    public static final String PREF_SCHEDULED_MESSAGE = "pref_scheduled_message";
    public static final String PREF_SCHEDULED_TIME = "pref_scheduled_time";

    Context context;
    LocationHelper locationHelper;
    public MessageService(Context context, LocationHelper locationHelper) {
        this.context = context;
        this.locationHelper = locationHelper;
    }

    public MessageService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if ("SCHEDULE_MESSAGE".equals(action)) {
                String message = intent.getStringExtra(EXTRA_MESSAGE);
                int delayMinutes = intent.getIntExtra(EXTRA_DELAY_MINUTES, 0);
                scheduleMessage(this, message, delayMinutes);
            } else if ("CANCEL_MESSAGE".equals(action)) {
                cancelScheduledMessage(this);
            } else if ("SEND_MESSAGE".equals(action)) {
                SMSHelper.sendSOSMessage(context, locationHelper);
                clearScheduledMessagePrefs(this);
            }
        }
        return START_STICKY;
    }

    public static void scheduleMessage(Context context, String message, int delayMinutes) {
        saveScheduledMessagePrefs(context, message, delayMinutes);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, delayMinutes);

        Intent intent = new Intent(context, MessageService.class);
        intent.setAction("SEND_MESSAGE");
        intent.putExtra(EXTRA_MESSAGE, message);
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);
        }

        Toast.makeText(context, "Mesaj programat pentru " + delayMinutes + " minute", Toast.LENGTH_SHORT).show();
    }

    public static void cancelScheduledMessage(Context context) {
        clearScheduledMessagePrefs(context);

        Intent intent = new Intent(context, MessageService.class);
        intent.setAction("SEND_MESSAGE");
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
    private static void saveScheduledMessagePrefs(Context context, String message, int delayMinutes) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(PREF_SCHEDULED_MESSAGE, message)
                .putLong(PREF_SCHEDULED_TIME, System.currentTimeMillis() + (delayMinutes * 60 * 1000))
                .apply();
    }
    private static void clearScheduledMessagePrefs(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .remove(PREF_SCHEDULED_MESSAGE)
                .remove(PREF_SCHEDULED_TIME)
                .apply();
    }
    public static boolean hasScheduledMessage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.contains(PREF_SCHEDULED_MESSAGE);
    }
    public static String getScheduledMessage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_SCHEDULED_MESSAGE, null);
    }
    public static long getScheduledTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(PREF_SCHEDULED_TIME, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}