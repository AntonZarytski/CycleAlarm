package zaritsky.com.cyclealarm.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import zaritsky.com.cyclealarm.activities.MainActivity;

public class WeatherDataLoader extends IntentService {

    //private static final String cityId = "524901";
    //определение погоды по cityId
    //private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/forecast?id=%s&appid=%s";
    //определение погоды по координатам
    private static final String LOG = "GPS";
    private static final String appId = "fac8298fbb01aea28d39b019a9839657";
    private static final String OPEN_WEATHER_MAP_API = "http:// api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";
    private static String lat;
    private static String lon;
    private static final String RESPONSE = "cod";
    private static final String NEW_LINE = "\n";
    private static final int ALL_GOOD = 200;
    private LocationManager locationManager;
    private JSONObject jsonObject;

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            setCoordinates(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            setCoordinates(locationManager.getLastKnownLocation(provider));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            if (provider.equals(LocationManager.GPS_PROVIDER)) {
//               String.valueOf(status);
//            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
//               String.valueOf(status);
//            }
//        }
        }
    };

    public WeatherDataLoader() {
        super("name");
    }


    private void setCoordinates(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    //переход к настройкам и включению GPS
    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public static JSONObject getJSONData() {
        try {

            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, lat, lon, appId));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //зачем эти методы???
            // connection.addRequestProperty("id", cityId);
            //connection.addRequestProperty(KEY, context.getString(R.string.open_weather_map_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder rawData = new StringBuilder(1024);
            String tempVariable;

            while ((tempVariable = reader.readLine()) != null) {
                rawData.append(tempVariable).append(NEW_LINE);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(rawData.toString());
            if (jsonObject.getInt(RESPONSE) != ALL_GOOD) {
                return null;
            }
            return jsonObject;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        jsonObject = getJSONData();
        Log.e(LOG, "lat" + lat + " lon " + lon + " JSONObject " + jsonObject.toString());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}

