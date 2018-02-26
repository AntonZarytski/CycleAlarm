package zaritsky.com.cyclealarm.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.WeatherDataLoader;

public class ArarmIsActive extends AppCompatActivity {
    private final Handler handler = new Handler();
    private TextView cityName;
    private TextView temperature;
    private TextView wind;
    private TextView humidity;
    private TextView cloud;
    private TextView power;
    private TextView rain;
    private Button sleep;
    private Button deactiveAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ararm_is_active);
        initViews();
        updateWeatherData("Moscow");
    }

    private void initViews() {
        cityName = findViewById(R.id.city_name_text_view);
        temperature = findViewById(R.id.value_of_temperature_text_view);
        wind = findViewById(R.id.value_of_wind_text_view);
        rain = findViewById(R.id.value_of_rain);
        humidity = findViewById(R.id.value_of_precipitation_text_view);
        cloud = findViewById(R.id.value_of_cloud_text_view);
        power = findViewById(R.id.power);
        sleep = findViewById(R.id.sleep_five_minutes_button);
        deactiveAlarm = findViewById(R.id.deactive_alarm);
    }

    private void updateWeatherData(final String city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject json
                        = WeatherDataLoader.getJSONData(getApplicationContext(), city);
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
    private void renderWeather(JSONObject json){
        try {
            String cityname = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
            cityName.setText(cityname);
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");
            JSONObject rain = json.getJSONObject("rain");
            this.wind.setText(wind.getString("speed") + "м\\с");
            humidity.setText(main.getString("humidity") + "%");
            power.setText(main.getString("preasure") + "кПа");
            temperature.setText(String.format("%2f", main.getDouble("temp")) + " 0C");
            this.rain.setText(rain.getString("3h"));
            cloud.setText(details.getString("clouds"));
            DateFormat df = DateFormat.getDateTimeInstance();
            String lastUpdate = df.format(new Date(json.getLong("dt")*1000));
            //setWeatherIcon(details.getInt("id"), json.getJSONObject("sys").getLong("sunset")*1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
