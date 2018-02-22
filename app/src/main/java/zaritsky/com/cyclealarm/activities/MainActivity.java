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
import zaritsky.com.cyclealarm.fragments.AlarmsList;
import zaritsky.com.cyclealarm.fragments.Calendar;
import zaritsky.com.cyclealarm.interfaces.AbleToChangeFragment;

public class MainActivity extends AppCompatActivity implements AbleToChangeFragment {
    private FragmentManager fm;
    private AlarmsList alarmsListFragment;
    private Calendar calendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        alarmsListFragment = new AlarmsList();
        calendarFragment = new Calendar();

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

                } else if (id == R.id.to_alarms_fragment) {
                    replaceFragments(R.id.content_main, alarmsListFragment);
                } /*else if (id == R.id.nav_manage) {

                } else if (id == R.id.nav_share) {

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
    public void addFragment(int containerViewId, Fragment addingFragment) {
        fm.beginTransaction().add(containerViewId, addingFragment).commit();
    }

    @Override
    public void replaceFragments(int containerViewId, Fragment newFragment) {
        fm.beginTransaction().addToBackStack(null).replace(containerViewId, newFragment).commit();

    }

}
