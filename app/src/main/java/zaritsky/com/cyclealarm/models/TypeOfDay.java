package zaritsky.com.cyclealarm.models;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * model-class TypeOfDay contains color(for calendarView), alarm? and this
 * alarm position in AlarmList.class
 */
public class TypeOfDay implements Serializable {
    private String name;
    private Alarm alarmOfType;
    private String timeOfWakeUp;
    private int color;
    private int alarmPosition;

    @SuppressLint("NewApi")
    public TypeOfDay(String name, Alarm alarmOfType, int color) {
        this.name = name;
        this.alarmOfType = alarmOfType;
        this.timeOfWakeUp = alarmOfType.getFormatedTime();
        this.color = color;
    }

    public TypeOfDay() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeOfWakeUp(String timeOfWakeUp) {
        this.timeOfWakeUp = timeOfWakeUp;
    }

    public String getTimeOfWakeUp() {
        return timeOfWakeUp;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Alarm getAlarmOfType() {
        return alarmOfType;
    }

    public void setAlarmOfType(Alarm alarmOfType) {
        this.alarmOfType = alarmOfType;
    }

    public int getAlarmPosition() {
        return alarmPosition;
    }

    public void setAlarmPosition(int alarmPosition) {
        this.alarmPosition = alarmPosition;
    }
}
