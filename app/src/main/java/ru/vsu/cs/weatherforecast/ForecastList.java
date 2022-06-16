package ru.vsu.cs.weatherforecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vsu.cs.weatherforecast.adapter.ForecastListAdapter;
import ru.vsu.cs.weatherforecast.listener.OnItemListener;
import ru.vsu.cs.weatherforecast.model.ForecastListItem;
import ru.vsu.cs.weatherforecast.model.response.WeatherApiFullResponse;
import ru.vsu.cs.weatherforecast.model.response.WeatherMainBodyResponse;
import ru.vsu.cs.weatherforecast.util.AppUtils;
import ru.vsu.cs.weatherforecast.util.ForecastRestService;

public class ForecastList extends AppCompatActivity implements OnItemListener {
    private Double longitude;
    private Double latitude;
    private String cityName;
    private RecyclerView forecastList;
    private ProgressBar progressBar;
    private List<ForecastListItem> list = new ArrayList<>();
    private List<String> picNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_list);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        getExtras();
        getDataFromApi();
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
        if (position == 0)
            toDetailedForecastData.putExtra("hourlyAvailable", true);
        startActivity(toDetailedForecastData);
    }

    private void setUpViews() {
        ForecastListAdapter forecastListAdapter = new ForecastListAdapter(this, list, this);
        forecastList = findViewById(R.id.forecastList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        forecastList.setLayoutManager(manager);
        forecastList.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(forecastList.getContext(), 1);
        forecastList.addItemDecoration(dividerItemDecoration);
        forecastList.setAdapter(forecastListAdapter);
    }

    private void getDataFromApi() {
        ForecastRestService service = AppUtils.retrofit.create(ForecastRestService.class);
        Call<WeatherApiFullResponse> jsonObjectCall = service.getForecast(latitude, longitude, "minutely,hourly,alerts", AppUtils.WEATHER_API_KEY, "metric", "ru");
        jsonObjectCall.enqueue(new Callback<WeatherApiFullResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApiFullResponse> call, @NonNull Response<WeatherApiFullResponse> response) {
                if(response.body() != null) {
                    for(WeatherMainBodyResponse day : response.body().getDailyList()) {
                        picNames.add(day.getWeather().get(0).getIcon());
                        String description = day.getWeather().get(0).getDescription();
                        Double temperature = day.getTemp().getDay();
                        String strTemperature = String.format(Locale.getDefault(), "%s%.1f%s", temperature > 0 ? "+" : "-", temperature, "℃");
                        Long date = day.getDatetime() * 1000L;
                        ForecastListItem fli = new ForecastListItem(
                                null,
                                AppUtils.firstUpperCase(description),
                                strTemperature,
                                new SimpleDateFormat("dd.MM\nEEE", Locale.getDefault()).format(new Date(date))
                        );
                        list.add(fli);
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                setUpViews();
                getPicturesFromApi();
            }
            @Override
            public void onFailure(@NonNull Call<WeatherApiFullResponse> call, @NonNull Throwable t) {
                Toast.makeText(ForecastList.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPicturesFromApi() {
        ForecastRestService service = AppUtils.retrofit.create(ForecastRestService.class);
        for(int i = 0; i < picNames.size(); i++) {
            Call<ResponseBody> picCall = service.getListPicture(AppUtils.PIC_API_URL + picNames.get(i) + "@2x.png");
            int finalI = i;
            picCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Bitmap btm = BitmapFactory.decodeStream(response.body().byteStream());
                        forecastList.getAdapter().notifyItemChanged(finalI, btm);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
            });
        }
    }
}