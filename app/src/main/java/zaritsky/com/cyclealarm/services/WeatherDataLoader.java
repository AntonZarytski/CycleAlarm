package zaritsky.com.cyclealarm.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * download JSON from openweathermap.org by gps coordinates and parse it to weather data parameters
 */
public class WeatherDataLoader extends IntentService {

    //private static final String cityId = "524901";
    //определение погоды по cityId
    //private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/forecast?id=%s&appid=%s";
    //определение погоды по координатам
    private static final String LOG = "GPS";
    private static final String LON_KEY = "LON_KEY";
    private static final String LAT_KEY = "LAT_KEY";

    private static final String appId = "e8e2cd56b5ff9dc03867cb204da1f6d9";
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&appid=%s";

    private static String lat;
    private static String lon;
    private static final String RESPONSE = "cod";
    private static final String NEW_LINE = "\n";
    private static final int ALL_GOOD = 200;
    private LocationManager locationManager;
    private static JSONObject jsonObject;


    public WeatherDataLoader() {
        super("name");
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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, "НАЧАЛО РАБОТЫ СЕРВИСА", Toast.LENGTH_SHORT).show();
        lon = intent.getStringExtra(LON_KEY);
        lat = intent.getStringExtra(LAT_KEY);
        jsonObject = getJSONData();
        if (jsonObject != null)
            Log.e(LOG, "lat" + lat + " lon " + lon + " JSONObject " + jsonObject.toString());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        lon = intent.getStringExtra(LON_KEY);
        lat = intent.getStringExtra(LAT_KEY);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onStopService() {
        Intent intent = new Intent(getBaseContext(), WeatherDataLoader.class);
        stopService(intent);
    }

    public static JSONObject getJsonObject() {
        return jsonObject;
    }

}

