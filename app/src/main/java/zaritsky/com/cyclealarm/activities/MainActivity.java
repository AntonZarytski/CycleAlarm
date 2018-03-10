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
import zaritsky.com.cyclealarm.fragments.AlarmAdd;
import zaritsky.com.cyclealarm.fragments.AlarmsRecyclerList;
import zaritsky.com.cyclealarm.fragments.Calendar;
import zaritsky.com.cyclealarm.fragments.CycleRecyclerList;
import zaritsky.com.cyclealarm.fragments.EditTypeDay;
import zaritsky.com.cyclealarm.fragments.TypeDayRecyclerList;
import zaritsky.com.cyclealarm.interfaces.AbleToChangeFragment;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;

public class MainActivity extends AppCompatActivity implements AbleToChangeFragment {
    private final String ALARMFILE = "AlarmsList";
    private final String CYCLEFILE = "CycleList";
    private FragmentManager fm;
    private AlarmsRecyclerList alarmsListFragment;
    private Calendar calendarFragment;
    private CycleRecyclerList cycleRecyclerList;
    private Fragment editTypeDay;
    private TypeDayRecyclerList typesList;
    private AlarmList alarmList;
    private CycleList cycleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            alarmList = AlarmList.getInstance(getApplicationContext());
            cycleList = CycleList.getInstance(getApplicationContext());
            List<Alarm> alarms;
            File alarmsSavePath = new File(getApplicationContext().getFilesDir(), ALARMFILE);
            File cyclesSavePath = new File(getApplicationContext().getFilesDir(), CYCLEFILE);
            if (!alarmsSavePath.exists()) {
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(alarmsSavePath));
            alarms = (List<Alarm>) ois.readObject();
            if (alarms ==null){
                alarms = new ArrayList<>();
            }
            List<Cycle> cycles;
            if (!cyclesSavePath.exists()) {
                return;
            }
            ois = new ObjectInputStream(new FileInputStream(cyclesSavePath));
            cycles = (List<Cycle>) ois.readObject();
            if (cycles==null){
               cycles = new ArrayList<>();
            }
            cycleList.setCycleList(cycles);
            alarmList.setAlarmList(alarms);
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        fm = getSupportFragmentManager();
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
        alarmsListFragment = new AlarmsRecyclerList();
        calendarFragment = new Calendar();
        cycleRecyclerList = new CycleRecyclerList();
        typesList = new TypeDayRecyclerList();
        editTypeDay = new EditTypeDay();
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
    public void onSelectedFragment(int position) {
        AlarmAdd alarm = AlarmAdd.newInstance(position);
        replaceFragments(R.id.content_main, alarm);
    }

}
