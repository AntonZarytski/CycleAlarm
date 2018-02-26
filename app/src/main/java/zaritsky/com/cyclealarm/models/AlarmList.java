package zaritsky.com.cyclealarm.models;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlarmList implements Serializable {
    private static AlarmList ourInstance = null;
    private Context context;
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
    public void addAlarm(Alarm alarm){
        alarmList.add(alarm);
    }

}
