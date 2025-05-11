package com.example.safealert.fragments;

import static com.example.safealert.services.MessageService.cancelScheduledMessage;
import static com.example.safealert.services.MessageService.scheduleMessage;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safealert.helpers.EmailHelper;
import com.example.safealert.R;
import com.example.safealert.helpers.EmergencyCallHelper;
import com.example.safealert.helpers.LocationHelper;
import com.example.safealert.helpers.SMSHelper;
import com.example.safealert.services.MessageService;
import com.example.safealert.services.WeatherALERTService;

public class SOSFragment extends Fragment {

    EmergencyCallHelper emergencyCallHelper;
    TextView timerTextView;
    Button cancelButton, btnWeatherAlert, scheduleButton, cancelScheduleButton;
    EditText messageEditText, delayEditText;
    LocationHelper locationHelper;
    WeatherALERTService weatherALERTService;
    private boolean isBound = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sos, container, false);
        Button btnSendSOS = view.findViewById(R.id.sosButton);
        timerTextView = view.findViewById(R.id.timerTextView);
        cancelButton = view.findViewById(R.id.cancelButton);
        btnWeatherAlert = view.findViewById(R.id.btnWeatherAlert);

        scheduleButton = view.findViewById(R.id.scheduleButton);
        cancelScheduleButton = view.findViewById(R.id.cancelScheduleButton);
        messageEditText = view.findViewById(R.id.scheduledText);
        delayEditText = view.findViewById(R.id.delayText);

        locationHelper = new LocationHelper();
        emergencyCallHelper = new EmergencyCallHelper(requireActivity(), timerTextView);
        weatherALERTService = new WeatherALERTService(requireContext(), locationHelper);

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LocationHelper.LocalBinder binder = (LocationHelper.LocalBinder) service;
                locationHelper = binder.getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };

        btnSendSOS.setOnClickListener(v -> {
            SMSHelper.sendSOSMessage(requireContext(), locationHelper);
            //EmailHelper.sendSOSEmail(requireContext(), locationHelper);
            emergencyCallHelper.startEmergencyCountdown();
        });

        btnWeatherAlert.setOnClickListener(v -> {
            weatherALERTService.checkWeatherAndSendAlert();
        });
        //scheduleButton.setOnClickListener(v -> scheduleMessage();
        //cancelScheduleButton.setOnClickListener(v -> cancelScheduledMessage();

        cancelButton.setOnClickListener(v -> emergencyCallHelper.cancelEmergencyCall());


        return view;
    }
//    private void scheduleMessage() {
//        String message = messageEditText.getText().toString().trim();
//        String delayStr = delayEditText.getText().toString().trim();
//        int delayMinutes = Integer.parseInt(delayStr);
//        Intent intent = new Intent(this, MessageService.class);
//        intent.setAction("SCHEDULE_MESSAGE");
//        intent.putExtra(MessageService.EXTRA_MESSAGE, message);
//        intent.putExtra(MessageService.EXTRA_DELAY_MINUTES, delayMinutes);
//        startService(intent);
//    }

//    private void cancelScheduledMessage() {
//        Intent intent = new Intent(this, MessageService.class);
//        intent.setAction("CANCEL_MESSAGE");
//        startService(intent);
//    }
}
