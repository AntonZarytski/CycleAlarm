package zaritsky.com.cyclealarm.models;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Alarm extends BroadcastReceiver implements Serializable {
    private Calendar currentCalendar;
    private String name;
    private String note;
    private boolean[] weekCycle;
    private List<Calendar> calendar;
    private String nameOfVibroType;
    private String nameOfMusic;
    private String nameOfSmoothMusic;
    private boolean isCustomCycle;
    private boolean isPause;
    private boolean isMusic;
    private boolean isVibro;
    private boolean isSmoothWakeUp;
    private boolean isScoringOfTime;
    private int volumeOfSmooth;
    private int longPause;
    private int repeatTimePause;
    private int smoothMusicId;
    private int alarmMusicId;
    private int volumeOfAlarmMusic;
    private int preparedSmoothTime;
    private boolean isActive;
    private boolean isOn;
    private long[] vibratorPattern;


    public void setActive(boolean active) {
        isActive = active;
    }

    public Alarm(Calendar currentCalenadar, String note, boolean isCustomCycle) {
        this.currentCalendar = currentCalenadar;
        this.note = note;
        this.isActive = true;
        this.isCustomCycle = isCustomCycle;
        calendar = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LOG_TAG", "ALARM_IS_ON");
    }

    @SuppressLint("NewApi")
    public String getFormatedTime() {
        return new SimpleDateFormat("H:mm",
                new Locale("ru", "RU")).format(currentCalendar.getTime());
    }

    public Calendar getCurrentCalendar() {
        return currentCalendar;
    }

    public void setCurrentCalendar(Calendar currentCalendar) {
        this.currentCalendar = currentCalendar;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setMusic(boolean music) {
        isMusic = music;
    }

    public boolean isVibro() {
        return isVibro;
    }

    public void setVibro(boolean vibro) {
        isVibro = vibro;
    }

    public boolean isSmoothWakeUp() {
        return isSmoothWakeUp;
    }

    public void setSmoothWakeUp(boolean smoothWakeUp) {
        isSmoothWakeUp = smoothWakeUp;
    }

    public boolean isScoringOfTime() {
        return isScoringOfTime;
    }

    public void setScoringOfTime(boolean scoringOfTime) {
        isScoringOfTime = scoringOfTime;
    }

    public int getSmoothMusicId() {
        return smoothMusicId;
    }

    public void setSmoothMusicId(int smoothMusicId) {
        this.smoothMusicId = smoothMusicId;
    }

    public int getAlarmMusicId() {
        return alarmMusicId;
    }

    public void setAlarmMusicId(int alarmMusicId) {
        this.alarmMusicId = alarmMusicId;
    }

    public int getVolumeOfAlarmMusic() {
        return volumeOfAlarmMusic;
    }

    public void setVolumeOfAlarmMusic(int volumeOfAlarmMusic) {
        this.volumeOfAlarmMusic = volumeOfAlarmMusic;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public int getLongPause() {
        return longPause;
    }

    public void setLongPause(int longPause) {
        this.longPause = longPause;
    }

    public String getNameOfVibroType() {
        return nameOfVibroType;
    }

    public void setNameOfVibroType(String nameOfVibroType) {
        this.nameOfVibroType = nameOfVibroType;
    }

    public String getNameOfMusic() {
        return nameOfMusic;
    }

    public void setNameOfMusic(String nameOfMusic) {
        this.nameOfMusic = nameOfMusic;
    }

    public String getNameOfSmoothMusic() {
        return nameOfSmoothMusic;
    }

    public void setNameOfSmoothMusic(String nameOfSmoothMusic) {
        this.nameOfSmoothMusic = nameOfSmoothMusic;
    }

    public int getRepeatTimePause() {
        return repeatTimePause;
    }

    public void setRepeatTimePause(int repeatTimePause) {
        this.repeatTimePause = repeatTimePause;
    }

    public long[] getVibratorPattern() {
        return vibratorPattern;
    }

    public void setVibratorPattern(long[] vibratorPattern) {
        this.vibratorPattern = vibratorPattern;
    }

    public int getVolumeOfSmooth() {
        return volumeOfSmooth;
    }

    public void setVolumeOfSmooth(int volumeOfSmooth) {
        this.volumeOfSmooth = volumeOfSmooth;
    }

    public int getPreparedSmoothTime() {
        return preparedSmoothTime;
    }

    public void setPreparedSmoothTime(int preparedSmoothTime) {
        this.preparedSmoothTime = preparedSmoothTime;
    }

    public List<Calendar> getCalendar() {
        return calendar;
    }

    public void setCalendar(List<Calendar> calendar) {
        this.calendar = calendar;
    }

    public boolean isCustomCycle() {
        return isCustomCycle;
    }

    public void setCustomCycle(boolean customCycle) {
        isCustomCycle = customCycle;
    }

    public boolean[] getWeekCycle() {
        return weekCycle;
    }

    public void setWeekCycle(boolean[] weekCycle) {
        this.weekCycle = weekCycle;
    }
}
