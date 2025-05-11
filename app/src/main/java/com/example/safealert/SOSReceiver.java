package com.example.safealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.safealert.helpers.EmailHelper;
import com.example.safealert.helpers.LocationHelper;
import com.example.safealert.helpers.SMSHelper;

public class SOSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationHelper locationHelper = new LocationHelper();
        SMSHelper.sendSOSMessage(context, locationHelper);
        EmailHelper.sendSOSEmail(context, locationHelper);
    }
}
