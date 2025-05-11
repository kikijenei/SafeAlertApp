package com.example.safealert.helpers;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class EmailHelper {
    public static void sendSOSEmail(Context context, LocationHelper locationHelper) {
        double latitude = locationHelper.getLatitude();
        double longitude = locationHelper.getLongitude();
        String email = "person@gmail.com";
        String subject = "SOS Alert!";
        String message = "SOS! HELP! Location: https://maps.google.com/?q=" + latitude + "," + longitude;
        if (latitude == 0.0 || longitude == 0.0) {
            return;
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            Toast.makeText(context, "Error sending e-mail..", Toast.LENGTH_SHORT).show();
        }
    }
}
