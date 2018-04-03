package zaritsky.com.cyclealarm.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.LOCATION_SERVICE;

public class LocationService {
    private static LocationService locationService;
    private static final String LOG = "GPS";
    private static final String LON_KEY = "LON_KEY";
    private static final String LAT_KEY = "LAT_KEY";
    private static LocationManager locationManager;
    private static String lat;
    private static String lon;
    private static Context context;
    private static TimeZone timeZone;

    private LocationService(){
    }

    public static LocationService getInstance(){
        if (locationService==null){
            return locationService = new LocationService();
        }else return locationService;
    }

    public void onStartWeatherService(Activity activity) {
        Toast.makeText(context, "service starting", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, WeatherDataLoader.class);
        intent.putExtra(LON_KEY, lon);
        intent.putExtra(LAT_KEY, lat);
        activity.startService(intent);
    }

    public void startLocationService(Activity activity) {
        //TODO AlarmManager для запуска сервиса за 5 мин до сработки будильника
        context = activity.getBaseContext();
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }


        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Log.e(LOG, "Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                Log.e(LOG, "Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER) || location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            lon = String.valueOf(location.getLongitude());
            lat = String.valueOf(location.getLatitude());
            timeZone =  TimeZone.getDefault();
            Log.e(LOG, "lon: " +  formatLocation(location));
            Log.e(LOG, "lat: " +  formatLocation(location));
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

    @SuppressLint("SetTextI18n")
    private void checkEnabled() {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static String getLat() {
        return lat;
    }

    public static String getLon() {
        return lon;
    }

    public static TimeZone getTimeZone() {
        return timeZone;
    }
}
