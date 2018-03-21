package zaritsky.com.cyclealarm.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;
import zaritsky.com.cyclealarm.models.AlarmReceiver;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;
import zaritsky.com.cyclealarm.models.VibratorPatterns;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class AlarmAdd extends Fragment {
    private static final String CURRENT_ALARM_POSITION = "CURRENT_ALARM_POSITION";
    private final String ALARMFILE = "AlarmsList";
    private final String LOG_TAG = "myLogs";
    private Alarm currentAlarm;
    private List<Cycle> cycleList;
    private Cycle currentCycle;
    private int currentAlarmPosition;
    private AlarmList alarmList;
    private AlarmManager alarmManager;
    private Calendar calendar;
    private TimePicker timePicker;
    private Button saveAlarmBtn;
    private TextView dataeOfNearestActive;
    private TextView nameOfAlarm;
    private TextView notificationOfAlarm;
    private Spinner periodOfRecycler;
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
    int pause = 0;
    int repeat = 0;
    int currentIdSound;
    long[] currentPattern;
    int currentSmoothId;
    Vibrator vibrator;
    //TODO костыль, или 2 раза делать recycler view
    boolean isVibrationDialogShow = false;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_add_fragment, container, false);
        this.inflater = inflater;
        alarmList = AlarmList.getInstance(getContext());
        initViews(view);
        initListeners();
        if (currentAlarm != null) {
            currentAlarmPosition = getArguments().getInt(CURRENT_ALARM_POSITION);
            setCurrentParameters();
        }
        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        audioManager = (AudioManager) getContext().getSystemService(AUDIO_SERVICE);
        vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);

        cycleList = CycleList.getInstance(getContext()).getCycleList();
        List<CharSequence> cyclesNames = new ArrayList<>();
        for (int i = 0; i < cycleList.size(); i++) {
            cyclesNames.add(cycleList.get(i).getName());
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, cyclesNames);
        periodOfRecycler.setAdapter(adapter);
        periodOfRecycler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!cycleList.isEmpty())
                    currentCycle = cycleList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO цикл неделя
                if (!cycleList.isEmpty())
                    currentCycle = cycleList.get(0);
            }
        });
        return view;
    }

    private void setCurrentParameters() {
        String wakeUpTime[] = currentAlarm.getFormatedTime().split(":");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(Integer.valueOf(wakeUpTime[0]));
            timePicker.setMinute(Integer.valueOf(wakeUpTime[1]));
        }
        dataeOfNearestActive.setText("TODO ближайшая дата сработки");
        nameOfAlarm.setText(currentAlarm.getName());
        notificationOfAlarm.setText(currentAlarm.getNote());
        currentCycle = currentAlarm.getDatesOfActiveCycle();
        periodOfRecycler.setPrompt(currentCycle.getName());
        pause = currentAlarm.getLongPause();
        repeat = currentAlarm.getRepeatTimePause();
        nameOfSound.setText(currentAlarm.getNameOfMusic());
        nameOfVibro.setText(currentAlarm.getNameOfVibroType());
        nameOfSmoothMelody.setText(currentAlarm.getNameOfSmoothMusic());
        volumeOfSound.setProgress(currentAlarm.getVolumeOfAlarmMusic());
        volumeOfSmooth.setProgress(currentAlarm.getVolemeOfSmooth());
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
        nameOfVibro = view.findViewById(R.id.vibro_of_alarm_text_view);
        onSmoothWake = view.findViewById(R.id.smooth_stand_switch);
        nameOfSmoothMelody = view.findViewById(R.id.smooth_stand_up_text_view);
        onScoringOfTime = view.findViewById(R.id.scoring_of_time_switch);
        volumeOfSmooth = view.findViewById(R.id.volume_of_smooth);
        if (currentAlarm == null) {
            volumeOfSound.setVisibility(View.GONE);
            nameOfSound.setVisibility(View.GONE);
            nameOfVibro.setVisibility(View.GONE);
            durationOfPause.setVisibility(View.GONE);
            nameOfSmoothMelody.setVisibility(View.GONE);
            volumeOfSmooth.setVisibility(View.GONE);
        }
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
                if (currentAlarm == null) {
                    currentAlarm = new Alarm(calendar, notificationOfAlarm.getText().toString(), currentCycle);
                    currentAlarm.setPause(onPause.isChecked());
                    currentAlarm.setMusic(onSound.isChecked());
                    currentAlarm.setVibro(onVibro.isChecked());
                    currentAlarm.setSmoothWakeUp(onSmoothWake.isChecked());
                    currentAlarm.setScoringOfTime(onScoringOfTime.isChecked());
                    currentAlarm.setName(nameOfAlarm.getText().toString());
                    dataeOfNearestActive.setText("TODO ближайшая дата сработки");
                    currentAlarm.setLongPause(pause);
                    currentAlarm.setRepeatTimePause(repeat);

                    currentAlarm.setNameOfSmoothMusic(nameOfSmoothMelody.getText().toString());
                    currentAlarm.setSmoothMusicId(currentSmoothId);
                    currentAlarm.setVolumeOfSmooth(volumeOfSmooth.getProgress());
                    currentAlarm.setVolumeOfAlarmMusic(volumeOfSound.getProgress());

                    currentAlarm.setNameOfMusic(nameOfSound.getText().toString());
                    currentAlarm.setNameOfVibroType(nameOfVibro.getText().toString());
                    currentAlarm.setAlarmMusicId(currentIdSound);
                    currentAlarm.setVibratorPattern(currentPattern);

                    alarmList.addAlarm(currentAlarm);
                    currentAlarm = null;
                } else {
                    alarmList.changeAlarm(currentAlarm, currentAlarmPosition);
                    currentAlarm = null;
                }
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
                long min = timePicker.getMinute() * 60000;
                Log.d(LOG_TAG, "start from " + min + " mills ");
                alarmManager.set(AlarmManager.RTC_WAKEUP, min, pIntent1);
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
                shouSoundChooseDialog(soundIds1, nameOfSound, volumeOfSound, true);
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
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.stop();
            }
        });
        nameOfSmoothMelody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypedArray soundIds1 = getContext().getResources().obtainTypedArray(R.array.smooth_ids);
                shouSoundChooseDialog(soundIds1, nameOfSmoothMelody, volumeOfSmooth, false);
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
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.stop();
            }
        });
        onSmoothWake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (currentAlarm == null) {
                        currentSmoothId = getContext().getResources().obtainTypedArray(R.array.smooth_ids).getResourceId(0, 0);
                        //TODO regex to name of sound
                        nameOfSmoothMelody.setText(getResources().getResourceEntryName(currentSmoothId));
                    }
                    nameOfSmoothMelody.setVisibility(View.VISIBLE);
                    volumeOfSmooth.setVisibility(View.VISIBLE);
                } else {
                    nameOfSmoothMelody.setVisibility(View.GONE);
                    volumeOfSmooth.setVisibility(View.GONE);
                }
            }
        });

    }

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

    private void shouSoundChooseDialog(TypedArray soundIds1, final TextView nameSound, final SeekBar volumeOfSound, final boolean isSoundDialog) {
        View dialog = inflater.inflate(R.layout.sound_check_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
        mDialogBuilder.setView(dialog);
        final List<String> files = new ArrayList<>();
        final List<Integer> idsList = new ArrayList<>();
        int[] soundIds = new int[11];
        for (int i = 0; i < soundIds.length; i++) {
            soundIds[i] = soundIds1.getResourceId(i, i);
            //TODO make regex? from "name_sound" to "Name sound."
            idsList.add(soundIds[i]);
            files.add(getResources().getResourceEntryName(soundIds[i]));
        }
        final TextView onOffSoundTextView = dialog.findViewById(R.id.sound_text_view_dialog);
        final Switch onOffSoundSwith = dialog.findViewById(R.id.switch_sound_dialog);
        onOffSoundSwith.setChecked(onSound.isChecked());
        final LinearLayout dataSound = dialog.findViewById(R.id.data_sound_dialog);
        final SeekBar volumeSound = dialog.findViewById(R.id.volume_sound_dialog_spinner);
        volumeSound.setProgress(volumeOfSound.getProgress());
        final RecyclerView chosseSound = dialog.findViewById(R.id.recycler_choose_sound);

        //TODO костыль 1 recycler на 2 параметра
        final FileChooseAdapter chooseAdapter = new FileChooseAdapter(files, new ArrayList<long[]>(), idsList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        chosseSound.setAdapter(chooseAdapter);

        chosseSound.setLayoutManager(layoutManager);
        if (!onSound.isChecked()) {
            onOffSoundSwith.setChecked(false);
            dataSound.setVisibility(View.GONE);
        } else
            onOffSoundSwith.setChecked(true);
        volumeSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.setVolume(progress / 100f, progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer = MediaPlayer.create(getContext(), idsList.get(chooseAdapter.currentposition));
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }else {
                    mediaPlayer.stop();
                    mediaPlayer.start();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.stop();
            }
        });
        onOffSoundSwith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataSound.setVisibility(View.VISIBLE);
                    onOffSoundTextView.setText("Включена");
                } else {
                    onOffSoundTextView.setText("Выключена");
                    dataSound.setVisibility(View.GONE);
                }
            }
        });
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (onOffSoundSwith.isChecked()) {
                                    nameSound.setText(files.get(chooseAdapter.currentposition));
                                    volumeOfSound.setProgress(volumeSound.getProgress());
                                    if (isSoundDialog) {
                                        currentIdSound = idsList.get(chooseAdapter.currentposition);
                                    } else {
                                        currentSmoothId = idsList.get(chooseAdapter.currentposition);
                                    }
                                } else {
                                    nameSound.setVisibility(View.GONE);
                                    volumeOfSound.setVisibility(View.GONE);
                                }
                                onSound.setChecked(onOffSoundSwith.isChecked());
                                mediaPlayer.stop();
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mediaPlayer.stop();
                                dialog.cancel();
                            }
                        });
        //Создаем Alert
        // Dialog:
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    private void showVibrationChooseDialog() {
        isVibrationDialogShow = true;
        View dialog = inflater.inflate(R.layout.sound_check_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
        mDialogBuilder.setView(dialog);
        final List<String> vibratorNames = Arrays.asList(getContext().getResources().getStringArray(R.array.vibrator_names));
        final List<long[]> vibratorPatterns = VibratorPatterns.getVibratorData();
        final TextView onOffVibrationTextView = dialog.findViewById(R.id.sound_text_view_dialog);
        final Switch onOffVibrationdSwith = dialog.findViewById(R.id.switch_sound_dialog);
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
                        if (mediaPlayer==null) {
                            mediaPlayer = MediaPlayer.create(getContext(), idsSound.get(position));
                            mediaPlayer.setLooping(false);
                            mediaPlayer.start();
                        }else {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                            }
                            mediaPlayer = MediaPlayer.create(getContext(), idsSound.get(position));
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

    static class FileChooseViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout fileView;
        TextView fileName;

        public FileChooseViewHolder(View itemView) {
            super(itemView);
            fileView = itemView.findViewById(R.id.cycle_recycler_view);
            fileName = itemView.findViewById(R.id.name_of_cycle_text_view);
        }
    }

}
