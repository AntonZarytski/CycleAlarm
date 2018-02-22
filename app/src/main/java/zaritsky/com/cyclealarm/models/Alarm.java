package zaritsky.com.cyclealarm.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class Alarm {
    private Timer timeOfActive;
    private String note;
    private List<Date> datesOfActive;
    private int awekingImageId;
    private int weatherImageId;
    private int preAlarmMusicId;
    private int alarmMusicId;
    private int volOfAlarmMusic;
    private int forceOfVibro;
    private boolean isActive;

    /**set/get*/
    public Timer getTimeOfActive() {
        return timeOfActive;
    }

    public void setTimeOfActive(Timer timeOfActive) {
        this.timeOfActive = timeOfActive;
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

    public void setActive(boolean active) {
        isActive = active;
    }

    public Alarm(Timer timeOfActive, String note, Date ... dates) {
        datesOfActive = new ArrayList<>();
        this.timeOfActive = timeOfActive;
        this.note = note;
        this.isActive = true;
        Collections.addAll(datesOfActive, dates);
    }

}
