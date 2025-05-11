package com.example.safealert.helpers;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.safealert.FavContacts;

import java.util.List;

public class SMSHelper {
    public static void sendSOSMessage(Context context, LocationHelper locationHelper) {
        double latitude = locationHelper.getLatitude();
        double longitude = locationHelper.getLongitude();
        //String phoneNumber = "0770814114";
        List<String> favContacts = FavContacts.getFavContacts(context);
        if (favContacts.isEmpty()) {
            Toast.makeText(context, "No favorite contacts found!", Toast.LENGTH_SHORT).show();
            return;
        }
        String message = "SOS! HELP! \n My location is: https://maps.google.com/?q=" + latitude + "," + longitude;
        if (latitude == 0.0 || longitude == 0.0) {
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        for (String phoneNumber : favContacts) {
            try{
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(context, "SMS sent with succes", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Error sending SMS", Toast.LENGTH_SHORT).show();
            }
        }

    }
}