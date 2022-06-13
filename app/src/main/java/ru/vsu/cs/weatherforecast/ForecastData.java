package ru.vsu.cs.weatherforecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vsu.cs.weatherforecast.model.response.WeatherApiFullResponse;
import ru.vsu.cs.weatherforecast.model.response.WeatherDailyResponse;
import ru.vsu.cs.weatherforecast.util.AppUtils;
import ru.vsu.cs.weatherforecast.util.ForecastRestService;

public class ForecastData extends AppCompatActivity {
    private Double latitude;
    private Double longitude;
    private String cityName;
    private Integer position;

    private TextView tvCityName;
    private TextView tvTemperatureValue;
    private TextView tvFeelsLikeValue;
    private TextView tvWindSpeedValue;
    private TextView tvCloudsValue;
    private TextView tvPressureValue;
    private TextView tvSunriseValue;
    private TextView tvSunsetValue;

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
        position = (Integer) extras.get("position");
        if (position == null) {
            position = 0;
        }
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
        ForecastRestService service = AppUtils.retrofit.create(ForecastRestService.class);
        Call<WeatherApiFullResponse> jsonObjectCall = service.getForecast(latitude, longitude, "minutely,hourly,alerts", AppUtils.WEATHER_API_KEY, "metric", "ru");
        jsonObjectCall.enqueue(new Callback<WeatherApiFullResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApiFullResponse> call, @NonNull Response<WeatherApiFullResponse> response) {
                if(response.body() != null) {
                    WeatherDailyResponse day = response.body().getDailyList().get(position);
                    tvCityName.setText(cityName);
                    tvTemperatureValue.setText(String.format("%s %s", day.getTemp().getDay(), "℃"));
                    tvFeelsLikeValue.setText(String.format("%s %s", day.getFeelsLike().getDay(), "℃"));
                    tvWindSpeedValue.setText(String.format("%s %s", day.getWindSpeed(), "м/c"));
                    tvCloudsValue.setText(String.format("%s%s", day.getClouds(), "%"));
                    tvPressureValue.setText(String.format("%s %s", day.getPressure(), "мм рт. ст."));
                    tvSunriseValue.setText(DateFormat.format("HH:mm:ss", new Date(day.getSunrise() * 1000L)).toString());
                    tvSunsetValue.setText(DateFormat.format("HH:mm:ss", new Date(day.getSunset() * 1000L)).toString());
                }
            }
            @Override
            public void onFailure(Call<WeatherApiFullResponse> call, Throwable t) {
                Toast.makeText(ForecastData.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
            }
        });
    }
}