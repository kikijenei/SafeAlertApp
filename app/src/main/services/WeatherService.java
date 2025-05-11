package com.example.safealert.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService extends Service {
    private static final String API_KEY = "65a8d05a5d4048ed88a235811252503";
    private static final String TAG = "WeatherService";

    public static String getWeather(double latitude, double longitude) {
        try{
            String Url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid="+API_KEY+"&lang=ro";
            URL url = new URL(Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "API Response: " + response);

                return parseWeatherResponse(response.toString());
            } else {
                Log.e(TAG, "Error API: " + responseCode);
            }
        }catch (Exception e){
            Log.e(TAG, "Error getting weather", e);
        }
        return null;
    }
    private static String parseWeatherResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.has("alerts")) {
                JSONArray alertsArray = jsonObject.getJSONArray("alerts");
                if (alertsArray.length() > 0) {
                    JSONObject alert = alertsArray.getJSONObject(0);
                    return alert.getString("event") + ": " + alert.getString("description");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error json: " + e.getMessage());
        }
        return "Alert meteo does not exist.";
    }
    public WeatherService() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }
}