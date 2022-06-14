package ru.vsu.cs.weatherforecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vsu.cs.weatherforecast.model.response.WeatherApiFullResponse;
import ru.vsu.cs.weatherforecast.model.response.WeatherHourBodyResponse;
import ru.vsu.cs.weatherforecast.model.response.WeatherMainBodyResponse;
import ru.vsu.cs.weatherforecast.util.AppUtils;
import ru.vsu.cs.weatherforecast.util.ForecastRestService;

public class ForecastData extends AppCompatActivity {
    private Double latitude;
    private Double longitude;
    private String cityName;
    private Integer position;
    private WeatherMainBodyResponse weatherDayResponse;
    private List<WeatherHourBodyResponse> weatherHourResponses;
    private Boolean hourlyAvailable;

    private TextView tvCityName;
    private TextView tvTemperatureValue;
    private TextView tvFeelsLikeValue;
    private TextView tvWindSpeedValue;
    private TextView tvCloudsValue;
    private TextView tvPressureValue;
    private TextView tvSunriseValue;
    private TextView tvSunsetValue;
    private ProgressBar progressBar;
    private LineChart temperatureChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_data);
        setEverything(View.INVISIBLE);
        progressBar = findViewById(R.id.forecastDataProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        getExtras();
        getDataFromApi();
        setEverything(View.VISIBLE);
        setupLayout();
    }

    private void setBackgroundImg(String weather) {
        Map<String, Integer> pictures = AppUtils.getPicturesMap();
        ScrollView cl = findViewById(R.id.forecastDataMain);
        if (pictures.get(weather.toLowerCase(Locale.ROOT)) != null) {
            cl.setBackgroundResource(pictures.get(weather.toLowerCase(Locale.ROOT)));
        }
        else {
            cl.setBackgroundResource(R.drawable.background_img);
        }
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        latitude = (Double) extras.get("latitude");
        longitude = (Double) extras.get("longitude");
        cityName = (String) extras.get("cityName");
        position = (Integer) extras.get("position");
        hourlyAvailable = (Boolean) extras.get("hourlyAvailable");
        if (position == null)
            position = 0;
        if (hourlyAvailable == null)
            hourlyAvailable = false;
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

    private void setData() {
        tvCityName.setText(cityName);
        tvTemperatureValue.setText(String.format("%s %s", weatherDayResponse.getTemp().getDay(), "℃"));
        tvFeelsLikeValue.setText(String.format("%s %s", weatherDayResponse.getFeelsLike().getDay(), "℃"));
        tvWindSpeedValue.setText(String.format("%s %s", weatherDayResponse.getWindSpeed(), "м/c"));
        tvCloudsValue.setText(String.format("%s%s", weatherDayResponse.getClouds(), "%"));
        tvPressureValue.setText(String.format("%s %s", weatherDayResponse.getPressure(), "мм рт. ст."));
        tvSunriseValue.setText(DateFormat.format("HH:mm:ss", new Date(weatherDayResponse.getSunrise() * 1000L)).toString());
        tvSunsetValue.setText(DateFormat.format("HH:mm:ss", new Date(weatherDayResponse.getSunset() * 1000L)).toString());
    }

    private void setChart() {
        if (hourlyAvailable) {
            temperatureChart = findViewById(R.id.temperatureChart);
            temperatureChart.setVisibility(View.VISIBLE);
        } else {
            temperatureChart = findViewById(R.id.temperatureChart);
            temperatureChart.setVisibility(View.INVISIBLE);
        }

        List<Entry> entries = new ArrayList<>();
        List<Float> xValues = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        for(int i = 0; i < 24; i++) {
            calendar.setTime(new Date(weatherHourResponses.get(i).getDt() * 1000L));
            entries.add(new Entry(i + currentHour, weatherHourResponses.get(i).getTemp().floatValue()));
            xValues.add(i + currentHour * 1f);
        }

        int chartMainColor = ResourcesCompat.getColor(getResources(), R.color.chartTemperatureLine, null);

        LineDataSet set1 = new LineDataSet(entries, "Temperature");
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_temperature_above_zero);
        set1.setFillDrawable(drawable);
        set1.setLineWidth(1.8f);
        set1.setCircleRadius(4f);
        set1.setDrawCircleHole(false);
        set1.setCircleColor(chartMainColor);
        set1.setColor(chartMainColor);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setDrawVerticalHighlightIndicator(false);
        set1.setValueTextSize(9);
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(),"%.1f", value);
            }
        });
        LineData data = new LineData(set1);

        temperatureChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int)value % 24 + ":00";
            }
        });
        temperatureChart.setViewPortOffsets(50, 50, 50, 20);
        temperatureChart.getXAxis().setTextSize(12);
        temperatureChart.setData(data);
        temperatureChart.getDescription().setEnabled(false);
        temperatureChart.getLegend().setEnabled(false);
        temperatureChart.setTouchEnabled(true);
        temperatureChart.setDragYEnabled(false);
        temperatureChart.setDragXEnabled(true);
        temperatureChart.setScaleEnabled(true);
        temperatureChart.getBackground().setAlpha(130);
        temperatureChart.animateY(1000);
        temperatureChart.invalidate();
    }

    private void getDataFromApi() {
        ForecastRestService service = AppUtils.retrofit.create(ForecastRestService.class);
        Call<WeatherApiFullResponse> jsonObjectCall = service.getForecast(latitude, longitude, "current,minutely,alerts", AppUtils.WEATHER_API_KEY, "metric", "ru");
        jsonObjectCall.enqueue(new Callback<WeatherApiFullResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApiFullResponse> call, @NonNull Response<WeatherApiFullResponse> response) {
                if(response.body() != null) {
                    WeatherMainBodyResponse day = response.body().getDailyList().get(position);
                    weatherDayResponse = day;
                    progressBar.setVisibility(View.INVISIBLE);
                    setBackgroundImg(day.getWeather().get(0).getMain());
                    setData();
                    if (hourlyAvailable) {
                        weatherHourResponses = response.body().getHourlyList();
                        setChart();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<WeatherApiFullResponse> call, @NonNull Throwable t) {
                Toast.makeText(ForecastData.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEverything(Integer visible) {
        TextView tvWeatherInCity = findViewById(R.id.tvWeatherInCity);
        TextView tvTemperature = findViewById(R.id.tvTemperature);
        TextView tvFeelsLike = findViewById(R.id.tvFeelsLike);
        TextView tvWindSpeed = findViewById(R.id.tvWindSpeed);
        TextView tvClouds = findViewById(R.id.tvClouds);
        TextView tvPressure = findViewById(R.id.tvPressure);
        TextView tvSunset = findViewById(R.id.tvSunset);
        TextView tvSunrise = findViewById(R.id.tvSunrise);

        tvWeatherInCity.setVisibility(visible);
        tvTemperature.setVisibility(visible);
        tvFeelsLike.setVisibility(visible);
        tvWindSpeed.setVisibility(visible);
        tvClouds.setVisibility(visible);
        tvPressure.setVisibility(visible);
        tvSunset.setVisibility(visible);
        tvSunrise.setVisibility(visible);
    }
}