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

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.fragments.AlarmAdd;
import zaritsky.com.cyclealarm.fragments.AlarmsRecyclerList;
import zaritsky.com.cyclealarm.fragments.Calendar;
import zaritsky.com.cyclealarm.fragments.CycleRecyclerList;
import zaritsky.com.cyclealarm.fragments.EditTypeDay;
import zaritsky.com.cyclealarm.fragments.TypeDayRecyclerList;
import zaritsky.com.cyclealarm.interfaces.AbleToChangeFragment;

public class MainActivity extends AppCompatActivity implements AbleToChangeFragment {
    private FragmentManager fm;
    private AlarmsRecyclerList alarmsListFragment;
    private Calendar calendarFragment;
    private CycleRecyclerList cycleRecyclerList;
    private Fragment editTypeDay;
    private TypeDayRecyclerList typesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        alarmsListFragment = new AlarmsRecyclerList();
        calendarFragment = new Calendar();
        cycleRecyclerList = new CycleRecyclerList();
        typesList = new TypeDayRecyclerList();

        editTypeDay = new EditTypeDay();
//        testBtnAlarm = findViewById(R.id.test_active_alarm);
//
//        testBtnAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ArarmIsActive.class);
//                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getApplicationContext().startActivity(intent);
//            }
//        });

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
