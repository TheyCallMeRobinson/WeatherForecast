package ru.vsu.cs.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastList extends AppCompatActivity {

    private Double longitude;
    private Double latitude;
    private String cityName;
    private Integer days;
    private ListView forecastList;

    private List<String> datesData;
    private List<String> temperatureData;
    private List<String> cloudsData;
    private List<String> windSpeedData;
    private List<String> descriptionData;

    private static final String WEATHER_API_KEY = "4ee0653bd41375d67d0527057889f757";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_list);
        setUpArrays();
        getExtras();
        getDataFromApi();
        //setUpViews();
        //setUpListeners();
        //System.err.println(datesData);
    }


    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        latitude = (Double) extras.get("latitude");
        longitude = (Double) extras.get("longitude");
        cityName = (String) extras.get("cityName");
        days = (Integer) extras.get("days");
    }

    private void setUpViews() {
        ForecastListAdapter fla = new ForecastListAdapter(
                this,
                datesData.toArray(new String[0]),
                temperatureData.toArray(new String[0]),
                cloudsData.toArray(new String[0]),
                windSpeedData.toArray(new String[0]),
                descriptionData.toArray(new String[0])
        );
        forecastList = findViewById(R.id.forecastList);
        forecastList.setAdapter(fla);
    }

    private void setUpArrays() {
        datesData = new ArrayList<>();
        temperatureData = new ArrayList<>();
        cloudsData = new ArrayList<>();
        windSpeedData = new ArrayList<>();
        descriptionData = new ArrayList<>();
    }

    private void setUpListeners() {
        forecastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent toForecastView = new Intent(ForecastList.this, ForecastData.class);
                toForecastView.putExtra("latitude", latitude);
                toForecastView.putExtra("longitude", longitude);
                toForecastView.putExtra("cityName", cityName);
                startActivity(toForecastView);
            }
        });
    }

    private void getDataFromApi() {
        String url = "https://api.openweathermap.org/data/2.5/onecall" +
                "?lat=" + latitude +
                "&lon=" + longitude +
                "&exclude=minutely,hourly,alert" +
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
                longitude = o.getDouble("lon");
                latitude = o.getDouble("lat");
                JSONArray arr = o.getJSONArray("daily");
                for (int i = 0; i < arr.length(); i++) {
                    Date date = new Date(arr.getJSONObject(i).getInt("dt"));
                    DateFormat df = new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault());
                    datesData.add(df.format(date));
                    temperatureData.add(String.valueOf(arr.getJSONObject(i).getJSONObject("temp").getDouble("day")));
                    cloudsData.add(String.valueOf(arr.getJSONObject(i).getDouble("clouds")));
                    windSpeedData.add(String.valueOf(arr.getJSONObject(i).getInt("wind_speed")));
                    descriptionData.add(String.valueOf(arr.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main")));
                }

//                tvCityName.setText(cityName);
//                tvTemperatureValue.setText(String.format("%s %s", o.getJSONObject("main").getDouble("temp"), "℃"));
//                tvFeelsLikeValue.setText(String.format("%s %s", o.getJSONObject("main").getDouble("feels_like"), "℃"));
//                tvWindSpeedValue.setText(String.format("%s %s", o.getJSONObject("wind").getDouble("speed"), "м/c"));
//                tvCloudsValue.setText(String.format("%s%s", o.getJSONObject("clouds").getDouble("all"), "%"));
//                tvPressureValue.setText(String.format("%s %s", o.getJSONObject("main").getDouble("pressure"), "мм рт. ст."));
//                tvSunriseValue.setText(new Time(o.getJSONObject("sys").getInt("sunrise")).toString());
//                tvSunsetValue.setText(new Time(o.getJSONObject("sys").getInt("sunset")).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setUpViews();
            setUpListeners();
        }
    }
}