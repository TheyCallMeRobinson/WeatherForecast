package ru.vsu.cs.weatherforecast;

import static android.view.View.INVISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.vsu.cs.weatherforecast.util.AppUtils;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private TextView tvMainTextView;
    private AutoCompleteTextView etCityName;
    private Button btnGetForecastForToday;
    private Button btnGetForecastForWeek;
    private Button btnGetLocationAuto;
    private LocationManager locationManager;
    private ProgressBar progressBar;
    private String currentCity;

    private static final String[] CITIES = new String[]{
            "Moskow", "Kiyv", "Berlin", "London", "Paris", "Warsaw", "New York", "Hong Kong", "Tokyo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
        startAnimations();
        requestLocationPermission();
        setListeners();
    }

    private void startAnimations() {
        AnimatorSet setFirst = new AnimatorSet();
        List<Animator> animators = AppUtils.getFadeInAnimatorsForViews(2000, tvMainTextView);
        setFirst.playSequentially(animators);
        setFirst.start();
        animators.addAll(AppUtils.getFadeInAnimatorsForViews(500, etCityName, btnGetLocationAuto, btnGetForecastForToday, btnGetForecastForWeek));
        setFirst.playSequentially(animators);
        setFirst.start();
    }

    private void setUpViews() {
        tvMainTextView = findViewById(R.id.tvMainTextView);
        etCityName = findViewById(R.id.etCityName);
        btnGetForecastForToday = findViewById(R.id.btnGetForecastForToday);
        btnGetForecastForWeek = findViewById(R.id.btnGetForecastForWeek);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressBar = findViewById(R.id.progressBarMain);
        btnGetLocationAuto = findViewById(R.id.btnGetLocationAuto);
        etCityName.setAlpha(0.0f);
        btnGetForecastForWeek.setAlpha(0.0f);
        btnGetForecastForToday.setAlpha(0.0f);
        btnGetLocationAuto.setAlpha(0.0f);
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CITIES);
        etCityName.setAdapter(citiesAdapter);
    }

    private void setListeners() {
        btnGetForecastForToday.setOnClickListener(v -> buttonHandler(MainActivity.this, ForecastData.class));
        btnGetForecastForWeek.setOnClickListener(v -> buttonHandler(MainActivity.this, ForecastList.class));
        btnGetLocationAuto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
                Toast.makeText(MainActivity.this, getString(R.string.requestGeo), Toast.LENGTH_SHORT).show();
            } else {
                if (currentCity != null) {
                    etCityName.setText(currentCity);
                    Toast.makeText(MainActivity.this, getString(R.string.locationDetermined) + " " + currentCity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.geocoderError), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void buttonHandler(Context context, Class<?> cls) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (etCityName.getText().toString().trim().equals("")) {
            Toast.makeText(MainActivity.this, R.string.requestUserInput, Toast.LENGTH_SHORT).show();
        } else {
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(etCityName.getText().toString(), 1);
                if (addresses.size() == 0) {
                    Toast.makeText(MainActivity.this, R.string.checkInput, Toast.LENGTH_SHORT).show();
                } else {
                    Double latitude = addresses.get(0).getLatitude();
                    Double longitude = addresses.get(0).getLongitude();
                    String cityName = addresses.get(0).getLocality();
                    if (cityName == null) {
                        throw new IllegalArgumentException(getString(R.string.checkCityName));
                    }
                    Intent toForecastView = new Intent(context, cls);
                    toForecastView.putExtra("latitude", latitude);
                    toForecastView.putExtra("longitude", longitude);
                    toForecastView.putExtra("position", 0);
                    toForecastView.putExtra("cityName", cityName);
                    toForecastView.putExtra("hourlyAvailable", true);
                    startActivity(toForecastView);
                }//implementation 'io.github.ParkSangGwon:tedpermission-normal:3.3.0'
            } catch (IOException e) {
                progressBar.setVisibility(INVISIBLE);
                Toast.makeText(MainActivity.this, R.string.connectionIssue, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                progressBar.setVisibility(INVISIBLE);
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        progressBar.setVisibility(INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    400, 10, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 400, 10,
                    locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String cityName;
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 100);
                if (addresses.size() > 0) {
                    cityName = addresses.get(0).getLocality();
                    if (cityName != null)
                        currentCity = cityName;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
}