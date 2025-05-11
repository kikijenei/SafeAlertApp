package com.example.safealert.services;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.app.Service;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.safealert.helpers.LocationHelper;
import com.example.safealert.helpers.SMSHelper;

import java.util.ArrayList;

public class VoiceRecognitionService extends Service {
    SpeechRecognizer speechRecognizer;
    LocationHelper locationHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        locationHelper = new LocationHelper();
        locationHelper.requestLocationPosition(this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float rmsdB) {}
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEndOfSpeech() {}
            @Override
            public void onError(int error) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.contains("SOS")) {
                    Toast.makeText(VoiceRecognitionService.this, "SOS detected! Sending alert...", Toast.LENGTH_SHORT).show();
                    SMSHelper.sendSOSMessage(getApplicationContext(), locationHelper);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}
            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        startListening();
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        speechRecognizer.startListening(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
