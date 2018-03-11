package zaritsky.com.cyclealarm.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.io.ObjectOutputStream;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;
import zaritsky.com.cyclealarm.models.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;
import static android.provider.Telephony.Mms.Part.FILENAME;

public class AlarmAdd extends Fragment {
    private static final String CURRENT_ALARM_POSITION = "CURRENT_ALARM_POSITION";
    private final String ALARMFILE = "AlarmsList";
    private final String LOG_TAG = "myLogs";
    private Alarm currentAlarm;
    private AlarmList alarmList;
    private AlarmManager am;
    private Calendar calendar;
    private TimePicker timePicker;
    private Button saveAlarmBtn;
    private TextView dataeOfNearestActive;
    private TextView nameOfAlarm;
    private TextView notificationOfAlarm;
    private TextView periodOfRecycler;
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
    private LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_add_fragment, container, false);
        this.inflater=inflater;
        alarmList = AlarmList.getInstance(getContext());
        am = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        initViews(view);
        initListeners();
        return view;
    }

    private void initViews(View view) {
        saveAlarmBtn = view.findViewById(R.id.save_alarm_button);
        timePicker = view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        dataeOfNearestActive = view.findViewById(R.id.date_of_nearest_active_text_view);
        nameOfAlarm = view.findViewById(R.id.name_of_alarm_signal);
        notificationOfAlarm = view.findViewById(R.id.notification_of_alarm);
        periodOfRecycler = view.findViewById(R.id.recycler_type_value);
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
    Intent createIntent(String action, String extra) {
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.setAction(action);
        intent.putExtra("extra", extra);
        return intent;
    }

    private void initListeners() {
        saveAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                //TODO сделать определение местоположения и определять временную зону
                TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
                calendar.setTimeZone(tz);
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                currentAlarm = new Alarm(calendar, notificationOfAlarm.getText().toString());
                currentAlarm.setMusic(onSound.getSplitTrack());
                currentAlarm.setVibro(onVibro.getSplitTrack());
                currentAlarm.setSmoothWakeUp(onSmoothWake.getSplitTrack());
                currentAlarm.setScoringOfTime(onScoringOfTime.getSplitTrack());
                currentAlarm.setVolumeOfAlarmMusic(volumeOfSound.getProgress());
                currentAlarm.setForceOfVibro(powerOfVibro.getProgress());
                alarmList.addAlarm(currentAlarm);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput(ALARMFILE, Context.MODE_PRIVATE));
                            oos.writeObject(alarmList.getAlarmList());
                            oos.flush();
                            oos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Intent alarmIntent = createIntent("action 1", "extra 1");
                PendingIntent pIntent1 = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);
                //long hour = timePicker.getHour()*60000*60;
                long min = timePicker.getMinute()*60000;
                Log.d(LOG_TAG, "start from " + min +" mills ");
                am.set(AlarmManager.RTC_WAKEUP, min, pIntent1);
            }
        });
        nameOfAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(nameOfAlarm);
            }
        });
        notificationOfAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(notificationOfAlarm);
            }
        });

    }

    private void showDialog(final TextView textView) {
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        View dialog = inflater.inflate(R.layout.edit_dialog, null);
        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
        //Настраиваем xml для нашего AlertDialog:
        mDialogBuilder.setView(dialog);
        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText userInput = dialog.findViewById(R.id.edit_text_dialog);
        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //Вводим текст и отображаем в строке ввода на основном экране:
                                textView.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        //Создаем Alert
        // Dialog:
        AlertDialog alertDialog = mDialogBuilder.create();

        //и отображаем его:
        alertDialog.show();
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
