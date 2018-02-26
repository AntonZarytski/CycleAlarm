package zaritsky.com.cyclealarm.fragments;


import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.io.ObjectOutputStream;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class AlarmAdd extends Fragment {
    private static final String CURRENT_ALARM_POSITION = "CURRENT_ALARM_POSITION";
    private Alarm currentAlarm;
    private AlarmList alarmList;
    private Calendar calendar;
    private TimePicker timePicker;
    private Button saveAlarmBtn;
    private TextView dataeOfNearestActive;
    private TextView nameOfAlarm;
    private TextView notificationOfAlarm;
    private Switch onPause;
    private TextView durationOfPause;
    private Switch onSound;
    private TextView nameOfSound;
    private SeekBar volumeOfSound;
    private Switch onVibro;
    private TextView typeOfVibro;
    private SeekBar powerOfVibro;
    private Switch onSmoothWake;
    private TextView nameOfSmoothMelody;
    private Switch onScoringOfTime;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_add_fragment, container, false);
        alarmList = AlarmList.getInstance(getContext());
        initViews(view);
        initListeners();
        return view;
    }

    private void initViews(View view) {
        saveAlarmBtn = view.findViewById(R.id.save_alarm_button);
        timePicker = view.findViewById(R.id.time_picker);
        dataeOfNearestActive = view.findViewById(R.id.date_of_nearest_active_text_view);
        nameOfAlarm = view.findViewById(R.id.name_of_alarm_signal);
        notificationOfAlarm = view.findViewById(R.id.notification_of_alarm);
        onPause = view.findViewById(R.id.pause_switch);
        durationOfPause = view.findViewById(R.id.value_of_pause_text_view);
        onSound = view.findViewById(R.id.sound_switch);
        nameOfSound = view.findViewById(R.id.sound_of_alarm_text_view);
        volumeOfSound = view.findViewById(R.id.volume_of_alarms_sound_seek_bar);
        onVibro = view.findViewById(R.id.vibro_switch);
        typeOfVibro = view.findViewById(R.id.vibro_of_alarm_text_view);
        powerOfVibro = view.findViewById(R.id.power_of_vibro_seek_bar);
        onSmoothWake = view.findViewById(R.id.smooth_stand_switch);
        nameOfSmoothMelody = view.findViewById(R.id.smooth_stand_up_text_view);
        onScoringOfTime = view.findViewById(R.id.scoring_of_time_switch);
    }

    private void initListeners() {
        saveAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                currentAlarm = new Alarm(calendar, notificationOfAlarm.getText().toString());
                alarmList.addAlarm(currentAlarm);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE));
                            oos.writeObject(alarmList.getAlarmList());
                            oos.flush();
                            oos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public static AlarmAdd newInstance(int position) {
        AlarmAdd fragment = new AlarmAdd();
        Bundle args = new Bundle();
        fragment.setCurrentAlarm(AlarmList.getOurInstance().getAlarmList().get(position));
        args.putInt(CURRENT_ALARM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public Alarm getCurrentAlarm() {
        return currentAlarm;
    }

    public void setCurrentAlarm(Alarm currentAlarm) {
        this.currentAlarm = currentAlarm;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_for_save_alarm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_alarm:
                break;
        }
        return true;
    }
}
