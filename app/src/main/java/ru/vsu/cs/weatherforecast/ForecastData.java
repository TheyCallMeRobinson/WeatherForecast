package ru.vsu.cs.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;

public class ForecastData extends AppCompatActivity {

    private Double latitude;
    private Double longitude;
    private String cityName;

    private TextView tvCityName;
    private TextView tvTemperatureValue;
    private TextView tvFeelsLikeValue;
    private TextView tvWindSpeedValue;
    private TextView tvCloudsValue;
    private TextView tvPressureValue;
    private TextView tvSunriseValue;
    private TextView tvSunsetValue;

    private static final String WEATHER_API_KEY = "4ee0653bd41375d67d0527057889f757";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_data);
        getExtras();
        setupLayout();
        getDataFromApi();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        latitude = (Double) extras.get("latitude");
        longitude = (Double) extras.get("longitude");
        cityName = (String) extras.get("cityName");
    }

    private void setupLayout() {
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperatureValue = findViewById(R.id.tvTemperatureValue);
        tvFeelsLikeValue = findViewById(R.id.tvFeelsLikeValue);
        tvWindSpeedValue = findViewById(R.id.tvWindSpeedValue);
        tvCloudsValue = findViewById(R.id.tvCloudsValue);
        tvPressureValue = findViewById(R.id.tvPressureValue);
        tvSunriseValue = findViewById(R.id.tvSunriseValue);
        tvSunsetValue = findViewById(R.id.tvSunsetValue);
    }

    private void getDataFromApi() {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude +
                "&lon=" + longitude +
                "&appid=" + WEATHER_API_KEY +
                "&units=metric&lang=ru";
        new GetAPIData().execute(url);
    }

    private class GetAPIData extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder builder = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject o = new JSONObject(s);
                longitude = o.getJSONObject("coord").getDouble("lon");
                latitude = o.getJSONObject("coord").getDouble("lat");
                cityName = o.getString("name");
                tvCityName.setText(cityName);
                tvTemperatureValue.setText(String.format("%s %s", o.getJSONObject("main").getDouble("temp"), "℃"));
                tvFeelsLikeValue.setText(String.format("%s %s", o.getJSONObject("main").getDouble("feels_like"), "℃"));
                tvWindSpeedValue.setText(String.format("%s %s", o.getJSONObject("wind").getDouble("speed"), "м/c"));
                tvCloudsValue.setText(String.format("%s%s", o.getJSONObject("clouds").getDouble("all"), "%"));
                tvPressureValue.setText(String.format("%s %s", o.getJSONObject("main").getDouble("pressure"), "мм рт. ст."));
                tvSunriseValue.setText(new Time(o.getJSONObject("sys").getInt("sunrise")).toString());
                tvSunsetValue.setText(new Time(o.getJSONObject("sys").getInt("sunset")).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}