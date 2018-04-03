package zaritsky.com.cyclealarm.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.fragments.CalendarFragment;
import zaritsky.com.cyclealarm.services.LocationService;
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
    public final static String ALARMFILE = "AlarmsList";
    public final static String CYCLEFILE = "CycleList";
    private FragmentManager fm;
    private AlarmsRecyclerList alarmsListFragment;
    private CalendarFragment calendarFragment;
    private CycleRecyclerList cycleRecyclerList;
    private Fragment editTypeDay;
    private TypeDayRecyclerList typesList;
    private AlarmList alarmList;
    private CycleList cycleList;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLocationService();
        if (savedInstanceState == null)
            loadData();
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
                int id = item.getItemId();
                if (id == R.id.to_calendar_fragment) {
                    changeFragments(R.id.content_main, calendarFragment);
                } else if (id == R.id.nav_my_graphics) {
                    changeFragments(R.id.content_main, cycleRecyclerList);
                } else if (id == R.id.to_alarms_fragment) {
                    changeFragments(R.id.content_main, alarmsListFragment);
                } else if (id == R.id.new_day_type_fragment) {
                    changeFragments(R.id.content_main, typesList);
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    /**
     * start locationService
     */
    private void startLocationService() {
        locationService = LocationService.getInstance();
        locationService.startLocationService(this);
        //TODO проверка изменений широты и долготы а после чего
        locationService.onStartWeatherService(this);
    }

    /**
     * Load AlarmList and CycleList from internal memory
     */
    private void loadData() {
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
                if (alarms == null) {
                    alarms = new ArrayList<>();
                    alarmList.setAlarmList(alarms);
                }
                ois.close();
            }
            List<Cycle> cycles;
            if (cyclesSavePath.exists()) {
                ois = new ObjectInputStream(new FileInputStream(cyclesSavePath));
                cycles = (List<Cycle>) ois.readObject();
                if (cycles == null) {
                    cycles = new ArrayList<>();
                }
                cycleList.setCycleList(cycles);
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialisation of fragments
     */
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
                changeFragments(R.id.container_in_main_activity, alarmsListFragment);
                break;
            case R.id.to_calendar_fragment:
                changeFragments(R.id.container_in_main_activity, calendarFragment);
                break;
        }
        return true;
    }

    /**
     * delete fragment from backStack
     */
    @Override
    public void removeFragment(Fragment removingFragment) {
    }

    /**
     * add new fragment to backStack
     */
    @Override
    public void addFragment(int containerViewId, Fragment addingFragment) {
        fm.beginTransaction().add(containerViewId, addingFragment).commit();
    }

    /**
     * Change fragments by selected View
     */
    @Override
    public void changeFragments(int containerViewId, Fragment newFragment) {
        fm.beginTransaction().addToBackStack(null).replace(containerViewId, newFragment).commit();

    }

    /**
     * Change fragments by selected View`s position
     */
    @Override
    public void onSelectedFragment(Fragment fragment, int position) {
        changeFragments(R.id.content_main, fragment);
    }

}

