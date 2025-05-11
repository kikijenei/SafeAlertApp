package com.example.safealert;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.telephony.SmsManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.safealert.activities.MainActivity;

public class BatteryLowReceiver extends BroadcastReceiver {
    Context context;
    private static final String CONTACT_NR = "0770814114";
    private static final int SMS_PERMISSION_CODE = 1;
    private static final int LOCATION_PERMISSION_CODE = 100;

    public BatteryLowReceiver(Context context) {
        this.context = context;
        permissionRequest();
    }
    private void permissionRequest()
    {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }
    private void suggestPowerSavingMode()
    {
        Intent intent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void sendLowBatterySms()
    {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                String message = "Very low battery! Under 10";
                smsManager.sendTextMessage(CONTACT_NR, null, message, null, null);
                Toast.makeText(context, "SMS sent to the contact", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, "Error sending SMS", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void registerBatteryReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        context.registerReceiver(this, filter);
    }
    public void unregisterBatteryReceiver()
    {
        context.unregisterReceiver(this);
    }
    public void updateBatteryStatus()
    {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);

        if (batteryStatus != null)
        {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = (level / (float) scale) * 100;

            if (batteryPct <= 10)
            {
                suggestPowerSavingMode();
                sendLowBatterySms();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        float batteryPct = -1;

        if (Intent.ACTION_BATTERY_CHANGED.equals(action))
        {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPct = (level / (float) scale) * 100;
        }

        if (batteryPct > 0 && batteryPct <= 10)
        {
            suggestPowerSavingMode();
            sendLowBatterySms();
        }
    }
}