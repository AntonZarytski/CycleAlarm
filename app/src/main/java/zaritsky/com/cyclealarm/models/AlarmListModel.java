package zaritsky.com.cyclealarm.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class AlarmListModel {
    private static AlarmListModel ourInstance = null;
    private Context context;
    private List<Alarm> alarmList;

    public static AlarmListModel getInstance(Context context) {
        if (ourInstance == null) {
            return ourInstance = new AlarmListModel(context);
        } else {
            return ourInstance;
        }
    }

    public static AlarmListModel getOurInstance() {
        return ourInstance;
    }

    private AlarmListModel(Context context) {
        this.context = context;
        alarmList = new ArrayList<>();
        alarmList.add(new Alarm(new Timer(), "Null", new Date(55555555)));
        alarmList.add(new Alarm(new Timer(), "Null", new Date(55555555)));
        alarmList.add(new Alarm(new Timer(), "Null", new Date(55555555)));
        alarmList.add(new Alarm(new Timer(), "Null", new Date(55555555)));
        alarmList.add(new Alarm(new Timer(), "Null", new Date(55555555)));
        alarmList.add(new Alarm(new Timer(), "Null", new Date(55555555)));


    }

    public List<Alarm> getAlarmList() {
        return alarmList;
    }
    public void addAlarm(Alarm alarm){
        alarmList.add(alarm);
    }

}
