package ru.vsu.cs.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView etCityName;
    private Button btnGetForecastForToday;
    private Button btnGetForecastForWeek;
    private LocationManager locationManager;
    private ProgressBar progressBar;

    private static final String[] CITIES = new String[] {
            "Moskow", "Kiyv", "Berlin", "London", "Paris", "Warsaw", "New York", "Hong Kong", "Tokyo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
        setListeners();
    }

    private void setUpViews() {
        etCityName = findViewById(R.id.etCityName);
        btnGetForecastForToday = findViewById(R.id.btnGetForecastForToday);
        btnGetForecastForWeek = findViewById(R.id.btnGetForecastForWeek);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressBar = findViewById(R.id.progressBarMain);
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CITIES);
        etCityName.setAdapter(citiesAdapter);
    }

    private void setListeners() {
        btnGetForecastForToday.setOnClickListener(v -> buttonHandler(MainActivity.this, ForecastData.class));
        btnGetForecastForWeek.setOnClickListener(v -> buttonHandler(MainActivity.this, ForecastList.class));
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
                    Intent toForecastView = new Intent(context, cls);
                    toForecastView.putExtra("latitude", latitude);
                    toForecastView.putExtra("longitude", longitude);
                    toForecastView.putExtra("position", 0);
                    toForecastView.putExtra("cityName", etCityName.getText().toString());
                    startActivity(toForecastView);
                }//implementation 'io.github.ParkSangGwon:tedpermission-normal:3.3.0'
            } catch (IOException e) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Toast.makeText(MainActivity.this, R.string.connectionIssue, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000, 10,
                locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String cityName;
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                    etCityName.setText(cityName);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

}