package zaritsky.com.cyclealarm.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;
import zaritsky.com.cyclealarm.receiver.AlarmReceiver;
import zaritsky.com.cyclealarm.models.VibratorPatterns;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * This fragment may be start with different inflate of view(for user`s custom
 * cycleType of for usual weekCycle), there is create and add(or change) Alarm-model
 */
public class AlarmAdd extends Fragment {
    public static final String CURRENT_ALARM_POSITION = "CURRENT_ALARM_POSITION";
    private final String ALARMFILE = "AlarmsList";
    private final String MP_IS_PLAY_FLAG = "MP_IS_PLAY_FLAG";
    private final String CURRENT_ID_FLAG = "CURRENT_ID_FLAG";
    private final String CURRENT_TIME_PLAY_FLAG = "CURRENT_TIME_PLAY_FLAG";
    private final String LOG_TAG = "myLogs";
    private final long MILLS_IN_HOUR = 3600000;
    private final long MILLS_IN_MIN = 60000;
    private Alarm currentAlarm;
    private int currentAlarmPosition;
    private AlarmList alarmList;
    private AlarmManager alarmManager;
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
    private SeekBar volumeOfSmooth;
    private Switch onVibro;
    private TextView nameOfVibro;
    private Switch onSmoothWake;
    private TextView nameOfSmoothMelody;
    private Switch onScoringOfTime;
    private LayoutInflater inflater;
    private LinearLayout weekCycleWakeUp;
    private LinearLayout dataOfRepeatsLayout;
    private Fragment child;
    private RelativeLayout mondayLayout;
    private RelativeLayout tuesdayLayout;
    private RelativeLayout wednesdayLayout;
    private RelativeLayout thursdayLayout;
    private RelativeLayout fridayLayout;
    private RelativeLayout saturdayLayout;
    private RelativeLayout sundayLayout;
    private boolean[] weekCycle = new boolean[7];
    private boolean isCustomCycle;
    private int smoothPrepareTime = 0;
    private int pause = 0;
    private int repeat = 0;
    private int currentIdSound;
    private long[] currentPattern;
    private int currentSmoothId;
    private int currentMediaId;
    private Vibrator vibrator;
    //TODO костыль, или 2 раза делать recycler view
    private boolean isVibrationDialogShow = false;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_add_fragment, container, false);
        this.inflater = inflater;
        alarmList = AlarmList.getInstance(getContext());
        initViews(view);
        initListeners();
        /**if currentAlarm != null, it`s mean that fragment was started by selected View`s(Alarm) position*/
        if (currentAlarm != null) {
            currentAlarmPosition = getArguments().getInt(CURRENT_ALARM_POSITION);
            child = getFragmentManager().getFragment(getArguments(), TypeDayAdd.TYPE_DAY_ADD_FRAGMENT);
            setCurrentParameters();
        } else {
            /**check that this fragment has been started for user`s custom cycleType of for usual weekCycle*/
            if (getArguments() != null) {
                child = getFragmentManager().getFragment(getArguments(), TypeDayAdd.TYPE_DAY_ADD_FRAGMENT);
                isCustomCycle = getArguments().getBoolean(TypeDayAdd.FROM_TYPE_DAY_ADD_FRAGMENT, false);
            }
        }
        if (isCustomCycle) {
            dataOfRepeatsLayout.setVisibility(View.GONE);
        }
        if (savedInstanceState != null) {
            currentMediaId = savedInstanceState.getInt(CURRENT_ID_FLAG);
            if (savedInstanceState.getBoolean(MP_IS_PLAY_FLAG)) {
                mediaPlayer = MediaPlayer.create(getContext(), currentMediaId);
                mediaPlayer.seekTo(savedInstanceState.getInt(CURRENT_TIME_PLAY_FLAG));
                mediaPlayer.start();
            }
        }
        /**initial services*/
        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        audioManager = (AudioManager) getContext().getSystemService(AUDIO_SERVICE);
        vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                outState.putInt(CURRENT_ID_FLAG, currentMediaId);
                outState.putBoolean(MP_IS_PLAY_FLAG, true);
                outState.putInt(CURRENT_TIME_PLAY_FLAG, mediaPlayer.getCurrentPosition());
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * set parameters for alarm
     */
    private void setCurrentParameters() {
        String wakeUpTime[] = currentAlarm.getFormatedTime().split(":");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(Integer.valueOf(wakeUpTime[0]));
            timePicker.setMinute(Integer.valueOf(wakeUpTime[1]));
        }
        dataeOfNearestActive.setText("TODO ближайшая дата сработки");
        nameOfAlarm.setText(currentAlarm.getName());
        notificationOfAlarm.setText(currentAlarm.getNote());
        pause = currentAlarm.getLongPause();
        repeat = currentAlarm.getRepeatTimePause();
        nameOfSound.setText(currentAlarm.getNameOfMusic());
        nameOfVibro.setText(currentAlarm.getNameOfVibroType());
        smoothPrepareTime = currentAlarm.getPreparedSmoothTime();
        nameOfSmoothMelody.setText(currentAlarm.getNameOfSmoothMusic() + " за " + smoothPrepareTime + " минуты.");
        volumeOfSound.setProgress(currentAlarm.getVolumeOfAlarmMusic());
        volumeOfSmooth.setProgress(currentAlarm.getVolumeOfSmooth());
        currentIdSound = currentAlarm.getAlarmMusicId();
        currentPattern = currentAlarm.getVibratorPattern();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onPause.setChecked(currentAlarm.isPause());
            onSound.setChecked(currentAlarm.isMusic());
            onVibro.setChecked(currentAlarm.isVibro());
            onSmoothWake.setChecked(currentAlarm.isSmoothWakeUp());
            onScoringOfTime.setChecked(currentAlarm.isScoringOfTime());
        }
        if (repeat != 0) {
            durationOfPause.setText(pause + "минут, " + repeat + "раза");
        } else {
            durationOfPause.setText(pause + "минут.");
        }
        if (!currentAlarm.isMusic()) {
            volumeOfSound.setVisibility(View.GONE);
            nameOfSound.setVisibility(View.GONE);
        }
        if (!currentAlarm.isVibro()) {
            nameOfVibro.setVisibility(View.GONE);
        }
        if (currentAlarm.isPause()) {
            durationOfPause.setVisibility(View.GONE);
        }
        if (currentAlarm.isSmoothWakeUp()) {
            nameOfSmoothMelody.setVisibility(View.GONE);
            volumeOfSmooth.setVisibility(View.GONE);
        }
        isCustomCycle = currentAlarm.isCustomCycle();
        if (!isCustomCycle) {
            weekCycle = currentAlarm.getWeekCycle();
            weekCycleWakeUp.setVisibility(View.VISIBLE);
            if (weekCycle[0])
                mondayLayout.setAlpha(1f);
            if (weekCycle[1])
                tuesdayLayout.setAlpha(1f);
            if (weekCycle[2])
                wednesdayLayout.setAlpha(1f);
            if (weekCycle[3])
                thursdayLayout.setAlpha(1f);
            if (weekCycle[4])
                fridayLayout.setAlpha(1f);
            if (weekCycle[5])
                saturdayLayout.setAlpha(1f);
            if (weekCycle[6])
                sundayLayout.setAlpha(1f);
        } else {
            weekCycleWakeUp.setVisibility(View.GONE);
        }
    }

    private void initViews(View view) {
        saveAlarmBtn = view.findViewById(R.id.save_alarm_button);
        timePicker = view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        dataeOfNearestActive = view.findViewById(R.id.date_of_nearest_active_text_view);
        nameOfAlarm = view.findViewById(R.id.name_of_alarm_signal);
        notificationOfAlarm = view.findViewById(R.id.notification_of_alarm);

        onPause = view.findViewById(R.id.pause_switch);
        durationOfPause = view.findViewById(R.id.value_of_pause_text_view);
        onSound = view.findViewById(R.id.sound_switch);
        nameOfSound = view.findViewById(R.id.sound_of_alarm_text_view);
        volumeOfSound = view.findViewById(R.id.volume_of_alarms_sound_seek_bar);
        onVibro = view.findViewById(R.id.vibro_switch);
        nameOfVibro = view.findViewById(R.id.vibro_of_alarm_text_view);
        onSmoothWake = view.findViewById(R.id.smooth_stand_switch);
        nameOfSmoothMelody = view.findViewById(R.id.smooth_stand_up_text_view);
        onScoringOfTime = view.findViewById(R.id.scoring_of_time_switch);
        volumeOfSmooth = view.findViewById(R.id.volume_of_smooth);
        weekCycleWakeUp = view.findViewById(R.id.week_cycle_wake_up_layout);
        dataOfRepeatsLayout = view.findViewById(R.id.data_of_repeats_layout);
        if (currentAlarm == null) {
            volumeOfSound.setVisibility(View.GONE);
            nameOfSound.setVisibility(View.GONE);
            nameOfVibro.setVisibility(View.GONE);
            durationOfPause.setVisibility(View.GONE);
            nameOfSmoothMelody.setVisibility(View.GONE);
            volumeOfSmooth.setVisibility(View.GONE);
        }
        mondayLayout = view.findViewById(R.id.monday_layout);
        mondayLayout.setAlpha(0.2f);
        tuesdayLayout = view.findViewById(R.id.tuesday_layout);
        tuesdayLayout.setAlpha(0.2f);
        wednesdayLayout = view.findViewById(R.id.wednesday_layout);
        wednesdayLayout.setAlpha(0.2f);
        thursdayLayout = view.findViewById(R.id.thursday_layout);
        thursdayLayout.setAlpha(0.2f);
        fridayLayout = view.findViewById(R.id.friday_layout);
        fridayLayout.setAlpha(0.2f);
        saturdayLayout = view.findViewById(R.id.saturday_layout);
        saturdayLayout.setAlpha(0.2f);
        sundayLayout = view.findViewById(R.id.sunday_layout);
        sundayLayout.setAlpha(0.2f);

    }

    /**
     * intent for AlarmReceiver
     */
    Intent createIntent(String action, String extra) {
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.setAction(action);
        intent.putExtra("extra", extra);
        return intent;
    }

    private void initListeners() {
        /**save alarm*/
        saveAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getDefault();
                calendar.setTimeZone(tz);
                calendar.clear(Calendar.HOUR_OF_DAY);
                calendar.clear(Calendar.MINUTE);
                calendar.add(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.add(Calendar.MINUTE, timePicker.getMinute());
                if (currentAlarm == null) {
                    currentAlarm = new Alarm(calendar, notificationOfAlarm.getText().toString(), isCustomCycle);
                    currentAlarm.setPause(onPause.isChecked());
                    currentAlarm.setMusic(onSound.isChecked());
                    currentAlarm.setVibro(onVibro.isChecked());
                    currentAlarm.setSmoothWakeUp(onSmoothWake.isChecked());
                    currentAlarm.setScoringOfTime(onScoringOfTime.isChecked());
                    currentAlarm.setName(nameOfAlarm.getText().toString());
                    dataeOfNearestActive.setText("TODO ближайшая дата сработки");
                    currentAlarm.setLongPause(pause);
                    currentAlarm.setRepeatTimePause(repeat);
                    currentAlarm.setPreparedSmoothTime(smoothPrepareTime);
                    currentAlarm.setNameOfSmoothMusic(nameOfSmoothMelody.getText().toString());
                    currentAlarm.setSmoothMusicId(currentSmoothId);
                    currentAlarm.setVolumeOfSmooth(volumeOfSmooth.getProgress());
                    currentAlarm.setVolumeOfAlarmMusic(volumeOfSound.getProgress());
                    currentAlarm.setNameOfMusic(nameOfSound.getText().toString());
                    currentAlarm.setNameOfVibroType(nameOfVibro.getText().toString());
                    currentAlarm.setAlarmMusicId(currentIdSound);
                    currentAlarm.setVibratorPattern(currentPattern);
                    if (!isCustomCycle) {
                        currentAlarm.setWeekCycle(weekCycle);
                    }
                    /**if its new alarm, than add it to alarmList*/
                    alarmList.addAlarm(currentAlarm);
                } else {
                    /**or change old alarm by new*/
                    alarmList.changeAlarm(currentAlarm, currentAlarmPosition);
                }
                int currentAlarmPosition = alarmList.getAlarmPosition(currentAlarm);
                /**write alarmList in internal memory*/
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
                //TODO написать грамотный AlarmReceiver
                Intent alarmIntent = createIntent("action 1", "extra 1");
                PendingIntent pIntent1 = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);
                long minute = calendar.get(Calendar.MINUTE);
                long hour = calendar.get(Calendar.HOUR);
                long hourtemp = timePicker.getHour() - hour;
                long minutetemp = timePicker.getMinute() - minute;
                long temptime = hourtemp * MILLS_IN_HOUR + minutetemp * MILLS_IN_MIN;
                alarmManager.set(AlarmManager.RTC_WAKEUP, temptime, pIntent1);
                Toast.makeText(getContext(), "будильник сработает через " + hourtemp + " час " + minutetemp + " минут", Toast.LENGTH_LONG).show();
                currentAlarm = null;
                if (isCustomCycle) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(TypeDayAdd.FROM_TYPE_DAY_ADD_FRAGMENT, true);
                    bundle.putInt(CURRENT_ALARM_POSITION, currentAlarmPosition);
                    child.setArguments(bundle);
                }
                AlarmAdd.this.getFragmentManager().popBackStack();
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
        durationOfPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouPauseDialog();
            }
        });
        if (!onPause.isChecked()) {
            durationOfPause.setVisibility(View.GONE);
        }
        onPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (currentAlarm == null) {
                        pause = 5;
                        repeat = 0;
                        durationOfPause.setText(pause + " минут.");
                    }
                    durationOfPause.setVisibility(View.VISIBLE);
                } else {
                    durationOfPause.setVisibility(View.GONE);
                }
            }
        });
        nameOfSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypedArray soundIds1 = getContext().getResources().obtainTypedArray(R.array.sounds_ids);
                int countOfSound = getResources().getIntArray(R.array.sounds_ids).length;
                showSoundChooseDialog(soundIds1, nameOfSound, volumeOfSound, countOfSound, true);
            }
        });
        onSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (currentAlarm == null) {
                        currentIdSound = getContext().getResources().obtainTypedArray(R.array.sounds_ids).getResourceId(0, 0);
                        //TODO regex to name of sound
                        nameOfSound.setText(getResources().getResourceEntryName(currentIdSound));
                    }
                    volumeOfSound.setVisibility(View.VISIBLE);
                    nameOfSound.setVisibility(View.VISIBLE);
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    volumeOfSound.setVisibility(View.GONE);
                    nameOfSound.setVisibility(View.GONE);
                }
            }
        });
        onVibro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (currentAlarm == null) {
                        currentPattern = VibratorPatterns.getVibratorData().get(0);
                        nameOfVibro.setText(getContext().getResources().getStringArray(R.array.vibrator_names)[0]);
                    }
                    nameOfVibro.setVisibility(View.VISIBLE);
                } else {
                    nameOfVibro.setVisibility(View.GONE);
                }
            }
        });
        nameOfVibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVibrationChooseDialog();
            }
        });
        volumeOfSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.setVolume(progress / 100f, progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer = MediaPlayer.create(getContext(), currentIdSound);
                mediaPlayer.setVolume(volumeOfSound.getProgress(), volumeOfSound.getProgress());
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
                currentMediaId = currentSmoothId;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });
        nameOfSmoothMelody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypedArray soundIds1 = getContext().getResources().obtainTypedArray(R.array.smooth_ids);
                int countOfSmooth = getResources().getIntArray(R.array.smooth_ids).length;
                showSoundChooseDialog(soundIds1, nameOfSmoothMelody, volumeOfSmooth, countOfSmooth, false);
            }
        });
        volumeOfSmooth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.setVolume(progress / 100f, progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer = MediaPlayer.create(getContext(), currentSmoothId);
                mediaPlayer.setVolume(volumeOfSmooth.getProgress(), volumeOfSmooth.getProgress());
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
                currentMediaId = currentSmoothId;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });
        onSmoothWake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (currentAlarm == null) {
                        currentSmoothId = getContext().getResources().obtainTypedArray(R.array.smooth_ids).getResourceId(0, 0);
                        smoothPrepareTime = 3;
                        //TODO regex to name of sound
                        nameOfSmoothMelody.setText(getResources().getResourceEntryName(currentSmoothId) + " за " + smoothPrepareTime + " минут.");
                    }
                    nameOfSmoothMelody.setVisibility(View.VISIBLE);
                    volumeOfSmooth.setVisibility(View.VISIBLE);
                } else {
                    smoothPrepareTime = 0;
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    nameOfSmoothMelody.setVisibility(View.GONE);
                    volumeOfSmooth.setVisibility(View.GONE);
                }
            }
        });
        mondayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekCycle[0]) {
                    weekCycle[0] = true;
                    mondayLayout.setAlpha(1f);
                } else {
                    weekCycle[0] = false;
                    mondayLayout.setAlpha(0.2f);
                }
            }
        });
        tuesdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekCycle[1]) {
                    weekCycle[1] = true;
                    tuesdayLayout.setAlpha(1f);
                } else {
                    weekCycle[1] = false;
                    tuesdayLayout.setAlpha(0.2f);
                }
            }
        });
        wednesdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekCycle[2]) {
                    weekCycle[2] = true;
                    wednesdayLayout.setAlpha(1f);
                } else {
                    weekCycle[2] = false;
                    wednesdayLayout.setAlpha(0.2f);
                }
            }
        });
        thursdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekCycle[3]) {
                    weekCycle[3] = true;
                    thursdayLayout.setAlpha(1f);
                } else {
                    weekCycle[3] = false;
                    thursdayLayout.setAlpha(0.2f);
                }
            }
        });
        fridayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekCycle[4]) {
                    weekCycle[4] = true;
                    fridayLayout.setAlpha(1f);
                } else {
                    weekCycle[4] = false;
                    fridayLayout.setAlpha(0.2f);
                }
            }
        });
        saturdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekCycle[5]) {
                    weekCycle[5] = true;
                    saturdayLayout.setAlpha(1f);
                } else {
                    weekCycle[5] = false;
                    saturdayLayout.setAlpha(0.2f);
                }
            }
        });
        sundayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekCycle[6]) {
                    weekCycle[6] = true;
                    sundayLayout.setAlpha(1f);
                } else {
                    weekCycle[6] = false;
                    sundayLayout.setAlpha(0.2f);
                }
            }
        });
    }

    /**
     * Show dialog window with alarm`s pause parameters
     */
    private void shouPauseDialog() {
        View dialog = inflater.inflate(R.layout.pause_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
        mDialogBuilder.setView(dialog);
        final TextView onOffPauseTextView = dialog.findViewById(R.id.pause_text_view_dialog);
        final Switch onOffPauseSwith = dialog.findViewById(R.id.switch_pause_dialog);
        final LinearLayout dataPause = dialog.findViewById(R.id.data_pause_dialog);
        if (!onPause.isChecked()) {
            onOffPauseSwith.setChecked(false);
            durationOfPause.setVisibility(View.GONE);
            dataPause.setVisibility(View.GONE);
        } else
            onOffPauseSwith.setChecked(true);
        onOffPauseSwith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataPause.setVisibility(View.VISIBLE);
                    onOffPauseTextView.setText("Включена");
                } else {
                    onOffPauseTextView.setText("Выключена");
                    dataPause.setVisibility(View.GONE);
                }
            }
        });
        final RadioGroup pauseGroup = dialog.findViewById(R.id.radiobtn_group_pause);
        pauseGroup.check(R.id.five_min_pause);
        final RadioGroup repeatGroup = dialog.findViewById(R.id.radiobtn_group_repeat);
        repeatGroup.check(R.id.newer_repeat);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (onOffPauseSwith.isChecked()) {
                                    repeat = getRepeatValue(repeatGroup.getCheckedRadioButtonId());
                                    pause = getPauseValuse(pauseGroup.getCheckedRadioButtonId());
                                    if (repeat != 0)
                                        //TODO вынести текст в ресурсы
                                        durationOfPause.setText(pause + " минут, " + repeat + " раза");
                                    else
                                        durationOfPause.setText(pause + " минут.");
                                } else durationOfPause.setVisibility(View.GONE);
                                onPause.setChecked(onOffPauseSwith.isChecked());
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        //Создаем Alert
        // Dialog:
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    private int getRepeatValue(int checkedId) {
        switch (checkedId) {
            case R.id.three_time:
                return 3;
            case R.id.fifth_time:
                return 5;
            case R.id.newer_repeat:
                return 0;
        }
        return 0;
    }

    private int getPauseValuse(int checkedId) {
        switch (checkedId) {
            case R.id.five_min_pause:
                return 5;
            case R.id.ten_min_pause:
                return 10;
            case R.id.fifteen_min_pause:
                return 15;
            case R.id.thirty_min_pause:
                return 30;
        }
        return 5;
    }

    /**
     * Show dialog window for enter someText for TextViews
     */
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
                            public void onClick(DialogInterface dialog, int id) {
                                //Вводим текст и отображаем в строке ввода на основном экране:
                                textView.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        //Создаем Alert
        // Dialog:
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    /**
     * create new AlarmAdd fragmen for calling from the outside class
     */
    public static AlarmAdd newInstance(int position) {
        AlarmAdd fragment = new AlarmAdd();
        Bundle args = new Bundle();
        fragment.setCurrentAlarm(AlarmList.getOurInstance().getAlarmList().get(position));
        args.putInt(CURRENT_ALARM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
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

    /**
     * show dialog window with sound data or smoothSound(for smooth wakeUp) data(depending on the boolean isSoundDialog)
     */
    private void showSoundChooseDialog(TypedArray soundIds1, final TextView nameSound, final SeekBar volumeOfSound, int countOffiles, final boolean isSoundDialog) {
        View dialog = inflater.inflate(R.layout.sound_check_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
        mDialogBuilder.setView(dialog);
        final List<String> files = new ArrayList<>();
        final List<Integer> idsList = new ArrayList<>();
        for (int i = 0; i < countOffiles; i++) {
            //TODO make regex? from "name_sound" to "Name sound."
            idsList.add(soundIds1.getResourceId(i, i));
            files.add(getResources().getResourceEntryName(soundIds1.getResourceId(i, i)));
        }
        final RadioGroup prepareSmoothGroup = dialog.findViewById(R.id.radiobtn_group_prepared_smooth_activation);
        final LinearLayout smoothData = dialog.findViewById(R.id.smooth_activation_data_layout);
        final TextView onOffSoundTextView = dialog.findViewById(R.id.sound_text_view_dialog);
        final Switch onOffSoundSwitch = dialog.findViewById(R.id.switch_sound_dialog);
        final SeekBar volumeSound = dialog.findViewById(R.id.volume_sound_dialog_spinner);
        final LinearLayout dataSound = dialog.findViewById(R.id.data_sound_dialog);
        final RecyclerView chosseSound = dialog.findViewById(R.id.recycler_choose_sound);
        volumeSound.setProgress(volumeOfSound.getProgress());

        if (isSoundDialog) {
            onOffSoundSwitch.setChecked(onSound.isChecked());
            smoothData.setVisibility(View.GONE);
            if (!onSound.isChecked()) {
                dataSound.setVisibility(View.GONE);
            }
        } else {
            onOffSoundSwitch.setChecked(onSmoothWake.isChecked());
            if (!onSmoothWake.isChecked()) {
                dataSound.setVisibility(View.GONE);
                smoothData.setVisibility(View.GONE);
            }
        }
        //TODO костыль 1 recycler на 2 параметра
        final FileChooseAdapter chooseAdapter = new FileChooseAdapter(files, new ArrayList<long[]>(), idsList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        chosseSound.setAdapter(chooseAdapter);
        chosseSound.setLayoutManager(layoutManager);

        volumeSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.setVolume(progress / 100f, progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer == null) {
                    currentMediaId = idsList.get(chooseAdapter.currentposition);
                    mediaPlayer = MediaPlayer.create(getContext(), currentMediaId);
                    mediaPlayer.start();

                } else {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //  mediaPlayer.stop();
            }
        });
        onOffSoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataSound.setVisibility(View.VISIBLE);
                    onOffSoundTextView.setText("Включена");
                    if (!isSoundDialog) {
                        prepareSmoothGroup.check(R.id.three_minutes_before);
                        smoothPrepareTime = 3;
                        smoothData.setVisibility(View.GONE);
                    }
                } else {
                    onOffSoundTextView.setText("Выключена");
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    dataSound.setVisibility(View.GONE);
                    if (!isSoundDialog) {
                        smoothData.setVisibility(View.GONE);
                    }
                }
            }
        });
        prepareSmoothGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.three_minutes_before:
                        smoothPrepareTime = 3;
                        break;
                    case R.id.five_minutes_before:
                        smoothPrepareTime = 5;
                        break;
                    case R.id.ten_minutes_before:
                        smoothPrepareTime = 10;
                }
            }
        });
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (onOffSoundSwitch.isChecked()) {
                                    volumeOfSound.setProgress(volumeSound.getProgress());
                                    if (isSoundDialog) {
                                        nameSound.setText(files.get(chooseAdapter.currentposition));
                                        currentIdSound = idsList.get(chooseAdapter.currentposition);
                                    } else {
                                        currentSmoothId = idsList.get(chooseAdapter.currentposition);
                                        nameSound.setText(files.get(chooseAdapter.currentposition) + " за " + smoothPrepareTime + " минуты.");
                                    }
                                } else {
                                    nameSound.setVisibility(View.GONE);
                                    volumeOfSound.setVisibility(View.GONE);
                                }
                                if (isSoundDialog) {
                                    onSound.setChecked(onOffSoundSwitch.isChecked());
                                } else {
                                    onSmoothWake.setChecked(onOffSoundSwitch.isChecked());
                                }

                                if (mediaPlayer != null) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                }
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (mediaPlayer != null) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                }
                                dialog.cancel();
                            }
                        });
        //Создаем Alert
        // Dialog:
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    /**
     * show dialog window with vibration data
     */
    private void showVibrationChooseDialog() {
        isVibrationDialogShow = true;
        View dialog = inflater.inflate(R.layout.sound_check_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
        mDialogBuilder.setView(dialog);
        final List<String> vibratorNames = Arrays.asList(getContext().getResources().getStringArray(R.array.vibrator_names));
        final List<long[]> vibratorPatterns = VibratorPatterns.getVibratorData();
        final TextView onOffVibrationTextView = dialog.findViewById(R.id.sound_text_view_dialog);
        final Switch onOffVibrationdSwith = dialog.findViewById(R.id.switch_sound_dialog);
        final LinearLayout smoothData = dialog.findViewById(R.id.smooth_activation_data_layout);
        smoothData.setVisibility(View.GONE);
        onOffVibrationdSwith.setChecked(onSound.isChecked());
        final LinearLayout dataVibration = dialog.findViewById(R.id.data_sound_dialog);
        final SeekBar vibrationForce = dialog.findViewById(R.id.volume_sound_dialog_spinner);
        vibrationForce.setVisibility(View.GONE);
        final RecyclerView chosseSound = dialog.findViewById(R.id.recycler_choose_sound);
        //TODO костыль 1 recycler на 2 параметра
        final FileChooseAdapter chooseAdapter = new FileChooseAdapter(vibratorNames, vibratorPatterns, new ArrayList<Integer>(), getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        chosseSound.setAdapter(chooseAdapter);

        chosseSound.setLayoutManager(layoutManager);
        if (!onVibro.isChecked()) {
            onOffVibrationdSwith.setChecked(false);
            dataVibration.setVisibility(View.GONE);
        } else
            onOffVibrationdSwith.setChecked(true);
        onOffVibrationdSwith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataVibration.setVisibility(View.VISIBLE);
                    onOffVibrationTextView.setText("Включена");
                } else {
                    onOffVibrationTextView.setText("Выключена");
                    dataVibration.setVisibility(View.GONE);
                }
            }
        });
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (onOffVibrationdSwith.isChecked()) {
                                    nameOfVibro.setText(vibratorNames.get(chooseAdapter.currentposition));
                                    currentPattern = vibratorPatterns.get(chooseAdapter.currentposition);
                                } else {
                                    nameOfVibro.setVisibility(View.GONE);
                                }
                                onVibro.setChecked(onOffVibrationdSwith.isChecked());
                                isVibrationDialogShow = false;
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                isVibrationDialogShow = false;
                            }
                        });
        //Создаем Alert
        // Dialog:
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    /**
     * adapter for vibration-, sound-, or smoothSoundList
     */
    private class FileChooseAdapter extends RecyclerView.Adapter<AlarmAdd.FileChooseViewHolder> {
        List<String> files;
        List<long[]> patterns;
        Context contex;
        List<Integer> idsSound;
        int currentposition;

        FileChooseAdapter(List<String> files, List<long[]> patterns, List<Integer> ids, Context context) {
            this.files = files;
            this.contex = context;
            this.patterns = patterns;
            this.idsSound = ids;
        }

        @Override
        public AlarmAdd.FileChooseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cycle_element, parent, false);
            return new AlarmAdd.FileChooseViewHolder(itemView);
        }

        /**
         * sound or vibration controll by click on list-position
         */
        @Override
        public void onBindViewHolder(AlarmAdd.FileChooseViewHolder holder, final int position) {
            holder.fileName.setText(files.get(position));
            holder.fileName.setTextSize(10);
            holder.fileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentposition = position;
                    if (isVibrationDialogShow) {
                        vibrator.vibrate(patterns.get(position), -1);
                    } else {
                        if (mediaPlayer == null) {
                            currentMediaId = idsSound.get(position);
                            mediaPlayer = MediaPlayer.create(getContext(), currentMediaId);
                            mediaPlayer.setLooping(false);
                            mediaPlayer.start();
                        } else {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                            }
                            currentMediaId = idsSound.get(position);
                            mediaPlayer = MediaPlayer.create(getContext(), currentMediaId);
                            mediaPlayer.setLooping(false);
                            mediaPlayer.start();
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            if (files != null) {
                return files.size();
            } else return 0;
        }
    }

    /**
     * ViewHolder for vibration-, sound-, or smoothSoundList
     */
    static class FileChooseViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout fileView;
        TextView fileName;

        public FileChooseViewHolder(View itemView) {
            super(itemView);
            fileView = itemView.findViewById(R.id.cycle_recycler_view);
            fileName = itemView.findViewById(R.id.name_of_cycle_text_view);
        }
    }

    public Alarm getCurrentAlarm() {
        return currentAlarm;
    }

    public void setCurrentAlarm(Alarm currentAlarm) {
        this.currentAlarm = currentAlarm;
    }

}
