package com.example.safealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;
import com.example.safealert.helpers.LocationHelper;
import com.example.safealert.helpers.SMSHelper;

public class VolButtonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                int keyCode = keyEvent.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    Toast.makeText(context, "SOS! Sending SMS...", Toast.LENGTH_SHORT).show();
                    LocationHelper locationHelper = new LocationHelper();
                    locationHelper.initializeLocation();
                    SMSHelper.sendSOSMessage(context, locationHelper);
                }
            }
        }
    }
}