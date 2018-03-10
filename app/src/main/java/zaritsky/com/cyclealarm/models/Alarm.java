package zaritsky.com.cyclealarm.models;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Alarm extends BroadcastReceiver implements Serializable{
    private Calendar timeOfActiveCalendar;
    private AlarmManager alarmManager;
    private String note;
    private List<Date> datesOfActive;
    private int awekingImageId;
    private int weatherImageId;
    private int preAlarmMusicId;
    private int alarmMusicId;
    private int volOfAlarmMusic;
    private int forceOfVibro;
    private boolean isActive;
    private boolean isOn;


    public void setActive(boolean active) {
        isActive = active;
    }

    public Alarm(Calendar timeOfActiveCalendar, String note, Date ... dates) {
        datesOfActive = new ArrayList<>();
        this.timeOfActiveCalendar = timeOfActiveCalendar;
        this.note = note;
        this.isActive = true;
        Collections.addAll(datesOfActive, dates);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LOG_TAG", "ALARM_IS_ON");
    }

    /**set/get*/
//    public Date getTimeOfActive() {
//        return timeOfActive;
//    }
//
//    public void setTimeOfActive(Date timeOfActive) {
//        this.timeOfActive = timeOfActive;
//    }
    @SuppressLint("NewApi")
    public String getFormatedTime(){
        return new SimpleDateFormat("H:mm",
                new Locale("ru", "RU")).format(timeOfActiveCalendar.getTime());
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Date> getDatesOfActive() {
        return datesOfActive;
    }

    public void setDatesOfActive(List<Date> datesOfActive) {
        this.datesOfActive = datesOfActive;
    }

    public int getAwekingImageId() {
        return awekingImageId;
    }

    public void setAwekingImageId(int awekingImageId) {
        this.awekingImageId = awekingImageId;
    }

    public int getWeatherImageId() {
        return weatherImageId;
    }

    public void setWeatherImageId(int weatherImageId) {
        this.weatherImageId = weatherImageId;
    }

    public int getPreAlarmMusicId() {
        return preAlarmMusicId;
    }

    public void setPreAlarmMusicId(int preAlarmMusicId) {
        this.preAlarmMusicId = preAlarmMusicId;
    }

    public int getAlarmMusicId() {
        return alarmMusicId;
    }

    public void setAlarmMusicId(int alarmMusicId) {
        this.alarmMusicId = alarmMusicId;
    }

    public int getVolOfAlarmMusic() {
        return volOfAlarmMusic;
    }

    public void setVolOfAlarmMusic(int volOfAlarmMusic) {
        this.volOfAlarmMusic = volOfAlarmMusic;
    }

    public int getForceOfVibro() {
        return forceOfVibro;
    }

    public void setForceOfVibro(int forceOfVibro) {
        this.forceOfVibro = forceOfVibro;
    }

    public boolean isActive() {
        return isActive;
    }

    public Calendar getCalendar() {
        return timeOfActiveCalendar;
    }

    public void setCalendar(Calendar calendar) {
        this.timeOfActiveCalendar = calendar;
    }

}
