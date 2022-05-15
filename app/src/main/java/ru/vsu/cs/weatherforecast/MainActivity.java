package ru.vsu.cs.weatherforecast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private AutoCompleteTextView etCityName;
    private Button btnGetForecastForToday;
    private Button btnGetForecastForWeek;
    private LocationManager locationManager;
    private ProgressBar progressBar;

    private static final String[] CITIES = new String[]{
            "Moskow", "Kiyv", "Berlin", "London", "Paris", "Warsaw", "New York", "Hong Kong", "Tokyo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLocationPermission();
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

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //Request location updates:
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000, 10, locationListener);
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 400, 10,
                            locationListener);
                }
            }
            return;
        }
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
                        location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                    Toast.makeText(MainActivity.this, getString(R.string.location_determined) + " " + cityName, Toast.LENGTH_SHORT).show();
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