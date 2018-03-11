package zaritsky.com.cyclealarm.activities;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.WeatherMap;
import zaritsky.com.cyclealarm.services.WeatherDataLoader;

public class ArarmIsActive extends AppCompatActivity  {
    private static final String FONT_FILENAME = "fonts/weather.ttf";
//    private final Handler handler = new Handler();
    private TextView cityName;
    private TextView temperature;
    private TextView wind;
    private TextView humidity;
    private TextView cloud;
    private TextView preasure;
    private TextView rain;
    private Button sleep;
    private Button deactiveAlarm;
    private JSONObject jsonObject;
    private TextView weatherIcon;
    private Typeface weatherFont;
    private final Handler handler = new Handler ();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ararm_is_active);
        jsonObject = WeatherDataLoader.getJsonObject();
        initViews();
        updateWeatherData(jsonObject);
    }

    private void initViews() {
        cityName = findViewById(R.id.city_name_text_view);
        temperature = findViewById(R.id.value_of_temperature_text_view);
        wind = findViewById(R.id.value_of_wind_text_view);
        rain = findViewById(R.id.value_of_rain);
        humidity = findViewById(R.id.value_of_precipitation_text_view);
        cloud = findViewById(R.id.value_of_cloud_text_view);
        preasure = findViewById(R.id.value_of_power_text_view);
        sleep = findViewById(R.id.sleep_five_minutes_button);
        deactiveAlarm = findViewById(R.id.deactive_alarm);
        weatherIcon = findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        weatherFont = Typeface.createFromAsset(getAssets(), FONT_FILENAME);

    }

    private void updateWeatherData(final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject json = jsonObject;
                if (json == null) {
                    handler.post(new Runnable() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void renderWeather(JSONObject json) {
        try {
            WeatherMap map = new Gson().fromJson(json.toString(), WeatherMap.class);

            String city = map.getName().toUpperCase(Locale.US) + ", "
                    + map.getCountry();
            String detailsText = map.getDescription();
            cityName.setText(city);
            humidity.setText(map.getHumidity() + " %");
            preasure.setText(Math.round(map.getPressure()) + " мм рт. ст.");
            temperature.setText(Math.round(map.getTemp())+ " ℃");
            rain.setText(detailsText);
            cloud.setText("");
            wind.setText(map.getWindSpeed() + " м/с");

            setWeatherIcon(74, json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100; // Упрощение кодов (int оставляет только целочисленное значение)
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getString(R.string.weather_drizzle);
                    break;
                case 5:
                    icon = getString(R.string.weather_rainy);
                    break;
                case 6:
                    icon = getString(R.string.weather_snowy);
                    break;
                case 7:
                    icon = getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getString(R.string.weather_cloudy);
                    break;
                default:
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

}
