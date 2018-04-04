package zaritsky.com.cyclealarm.models;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * singleton class, contains List<Alarm> alarmList with all alarms of app
 */
public class AlarmList implements Serializable {
    private static AlarmList ourInstance = null;
    private transient Context context;
    private List<Alarm> alarmList;

    public static AlarmList getInstance(Context context) {
        if (ourInstance == null) {
            return ourInstance = new AlarmList(context);
        } else {
            return ourInstance;
        }
    }

    public static AlarmList getOurInstance() {
        return ourInstance;
    }

    private AlarmList(Context context) {
        this.context = context;
        alarmList = new ArrayList<>();
    }

    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    public List<Alarm> getAlarmList() {
        return alarmList;
    }

    public void addAlarm(Alarm alarm) {
        alarmList.add(alarm);
    }

    public void removeAlarm(Alarm alarm) {
        alarmList.remove(alarm);
    }

    public void removeAlarm(int position) {
        alarmList.remove(position);
    }

    public void changeAlarm(Alarm newAlarm, int position) {
        alarmList.remove(position);
        alarmList.add(position, newAlarm);
    }

    public int getAlarmPosition(Alarm alarm) {
        return alarmList.indexOf(alarm);
    }
}
