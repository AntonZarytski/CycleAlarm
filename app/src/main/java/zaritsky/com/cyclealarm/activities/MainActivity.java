package zaritsky.com.cyclealarm.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.fragments.CalendarFragment;
import zaritsky.com.cyclealarm.services.WeatherDataLoader;
import zaritsky.com.cyclealarm.fragments.AlarmsRecyclerList;
import zaritsky.com.cyclealarm.fragments.CycleRecyclerList;
import zaritsky.com.cyclealarm.fragments.TypeDayAdd;
import zaritsky.com.cyclealarm.fragments.TypeDayRecyclerList;
import zaritsky.com.cyclealarm.interfaces.AbleToChangeFragment;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;

public class MainActivity extends AppCompatActivity implements AbleToChangeFragment {
    private static final String LOG = "GPS";
    private static final String LON_KEY = "LON_KEY";
    private static final String LAT_KEY = "LAT_KEY";
    public final static String ALARMFILE = "AlarmsList";
    public final static String CYCLEFILE = "CycleList";
    public final static String CITYFORWEATHER = "CityForWeather";
    private FragmentManager fm;
    private AlarmsRecyclerList alarmsListFragment;
    private CalendarFragment calendarFragment;
    private CycleRecyclerList cycleRecyclerList;
    private Fragment editTypeDay;
    private TypeDayRecyclerList typesList;
    private AlarmList alarmList;
    private CycleList cycleList;
    private LocationManager locationManager;
    private static String lat;
    private static String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            ObjectInputStream ois;
            alarmList = AlarmList.getInstance(getApplicationContext());
            cycleList = CycleList.getInstance(getApplicationContext());
            List<Alarm> alarms;
            File alarmsSavePath = new File(getApplicationContext().getFilesDir(), ALARMFILE);
            File cyclesSavePath = new File(getApplicationContext().getFilesDir(), CYCLEFILE);
            if (alarmsSavePath.exists()) {
                ois = new ObjectInputStream(new FileInputStream(alarmsSavePath));
                alarms = (List<Alarm>) ois.readObject();
                if (alarms ==null){
                    alarms = new ArrayList<>();
                    alarmList.setAlarmList(alarms);
                }
                ois.close();
            }
            List<Cycle> cycles;
            if (cyclesSavePath.exists()) {
                ois = new ObjectInputStream(new FileInputStream(cyclesSavePath));
                cycles = (List<Cycle>) ois.readObject();
                if (cycles==null){
                    cycles = new ArrayList<>();
                }
                cycleList.setCycleList(cycles);
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        initFragments();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final DrawerLayout drawer = findViewById(R.id.container_in_main_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_header_container);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Обработка нажатий на пункты меню
                int id = item.getItemId();
                if (id == R.id.to_calendar_fragment) {
                    replaceFragments(R.id.content_main, calendarFragment);
                } else if (id == R.id.nav_my_graphics) {
                    replaceFragments(R.id.content_main, cycleRecyclerList);
                } else if (id == R.id.to_alarms_fragment) {
                    replaceFragments(R.id.content_main, alarmsListFragment);
                }  else if (id == R.id.new_day_type_fragment) {
                    replaceFragments(R.id.content_main, typesList);
                } /*else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }*/
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    private void initFragments() {
        fm = getSupportFragmentManager();
        alarmsListFragment = new AlarmsRecyclerList();
        calendarFragment = new CalendarFragment();
        cycleRecyclerList = new CycleRecyclerList();
        typesList = new TypeDayRecyclerList();
        editTypeDay = new TypeDayAdd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_item:

                break;
            case R.id.help_item:

                break;
            case R.id.about_programm_item:

                break;
            case R.id.to_alarms_fragment:
                replaceFragments(R.id.container_in_main_activity, alarmsListFragment);
                break;
            case R.id.to_calendar_fragment:
                replaceFragments(R.id.container_in_main_activity, calendarFragment);
                break;
        }
        return true;
    }

    @Override
    public void removeFragment(Fragment removingFragment) {

    }

    @Override
    public void addFragment(int containerViewId, Fragment addingFragment) {
        fm.beginTransaction().add(containerViewId, addingFragment).commit();
    }

    @Override
    public void replaceFragments(int containerViewId, Fragment newFragment) {
        fm.beginTransaction().addToBackStack(null).replace(containerViewId, newFragment).commit();

    }

    @Override
    public void onSelectedFragment(Fragment fragment, int position) {
        //AlarmAdd alarm = AlarmAdd.newInstance(position);
        replaceFragments(R.id.content_main, fragment);
    }

    public void onStartService() {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), WeatherDataLoader.class);
            intent.putExtra(LON_KEY, lon);
            intent.putExtra(LAT_KEY, lat);
            startService(intent);

    }

    public void onStopService() {
        Intent intent = new Intent(getBaseContext(), WeatherDataLoader.class);
        stopService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        //onStartService();
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
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Log.e(LOG, "lon: " +  formatLocation(location));
            Log.e(LOG, "lat: " +  formatLocation(location));
            /**запуск сервиса по загрузке погоды(json), сделать проверку на случай если у пользователя
             * не доступен интернет(Пр нет денег)*/
            onStartService();
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
}

