package zaritsky.com.cyclealarm.models;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherDataLoader {

    private static final String cityId = "524901";
    private static final String appId = "fac8298fbb01aea28d39b019a9839657";
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/forecast?id=%s&appid=%s";
    private static final String KEY = "e8e2cd56b5ff9dc03867cb204da1f6d9";
    private static final String RESPONSE = "cod";
    private static final String NEW_LINE = "\n";
    private static final int ALL_GOOD = 200;

    public static JSONObject getJSONData(Context context, String city) {
        try {

            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, cityId, appId));
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
}

/*
public class WeatherDataLoader {
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/forecast?id=%s&appid=%s"; //weather?q=%s&units=metric";
    private final static String API = "e8e2cd56b5ff9dc03867cb204da1f6d9";
    private static final String RESPONSE = "cod";
    private static final String NEW_LINE = "\n";
    private static final int ALL_GOOD = 200;
    private static final String cityid = "524901";

    public static JSONObject getJSONDate(Context context, String cityName) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, cityName, API));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty(API, cityid);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder rawData = new StringBuilder(1024);
            String tempvariable;
            while ((tempvariable = reader.readLine()) != null) {
                rawData.append(tempvariable).append(NEW_LINE);
            }
            JSONObject jsonObject = new JSONObject(rawData.toString());
            if (jsonObject.getInt(RESPONSE) != ALL_GOOD) {
                return null;
            } else return jsonObject;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}*/
