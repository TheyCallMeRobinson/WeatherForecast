package ru.vsu.cs.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ru.vsu.cs.weatherforecast.listener.OnItemListener;
import ru.vsu.cs.weatherforecast.util.AppUtils;

public class ForecastList extends AppCompatActivity implements OnItemListener {
    private Double longitude;
    private Double latitude;
    private String cityName;
    private RecyclerView forecastList;
    private ProgressBar progressBar;
    private List<ForecastListItem> list = new ArrayList<>();

    private static final String WEATHER_API_KEY = "4ee0653bd41375d67d0527057889f757";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_list);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        getExtras();
        getDataFromApi();
//        setUpViews();
//        setUpListeners();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        latitude = (Double) extras.get("latitude");
        longitude = (Double) extras.get("longitude");
        cityName = (String) extras.get("cityName");
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(this,"Item " + position, Toast.LENGTH_SHORT).show();
        Intent toDetailedForecastData = new Intent(this, ForecastData.class);
        toDetailedForecastData.putExtra("longitude", longitude);
        toDetailedForecastData.putExtra("latitude", latitude);
        toDetailedForecastData.putExtra("cityName", cityName);
        toDetailedForecastData.putExtra("position", position);
        startActivity(toDetailedForecastData);
    }

    private void setUpViews() {
        ForecastListAdapter forecastListAdapter = new ForecastListAdapter(this, list, this);
        forecastList = findViewById(R.id.forecastList);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(forecastList.getContext(), 1);
        forecastList.addItemDecoration(dividerItemDecoration);
        forecastList.setAdapter(forecastListAdapter);
    }

    private void getDataFromApi() {
        String urlJsonData = "https://api.openweathermap.org/data/2.5/onecall" +
                "?lat=" + latitude +
                "&lon=" + longitude +
                "&exclude=minutely,hourly,alert" +
                "&appid=" + WEATHER_API_KEY +
                "&units=metric&lang=ru";
        new GetAPIData().execute(urlJsonData);
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
                String line;
                while((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
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
                    Drawable pic = new GetAPIPicture().execute(arr.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")).get();
                    pic.setBounds(0, 0, 75, 75);
                    String description = arr.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");
                    double temperature = arr.getJSONObject(i).getJSONObject("temp").getDouble("day");
                    String temp = (temperature > 0 ? "+" : "-") + temperature + "℃";
                    long date = arr.getJSONObject(i).getInt("dt") * 1000L;
                    ForecastListItem fli = new ForecastListItem(
                            pic,
                            AppUtils.firstUpperCase(description),
                            temp,
                            new SimpleDateFormat("dd.MM.yy\nEEE", Locale.getDefault()).format(new Date(date))
                    );
                    list.add(fli);
                }
            } catch (JSONException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            setUpViews();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    private class GetAPIPicture extends AsyncTask<String, Void, Drawable> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Drawable s) {
            super.onPostExecute(s);
        }

        @Override
        protected Drawable doInBackground(String... strings) {
            String picName = strings[0];
            Drawable pic;
            try {
                String urlPicData = "https://openweathermap.org/img/wn/" + picName + "@2x.png";
                InputStream is = (InputStream) new URL(urlPicData).getContent();
                pic = Drawable.createFromStream(is, "src name");
            } catch (Exception e) {
                e.printStackTrace();
                pic = null;
            }
            return pic;
        }
    }
}