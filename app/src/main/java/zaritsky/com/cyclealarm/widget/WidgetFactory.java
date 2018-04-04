package zaritsky.com.cyclealarm.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;


class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    final String ACTION_ON_CLICK = "android.appwidget.action.APPWIDGET_UPDATE";
    public final static String ITEM_POSITION = "item_position";
    private List<Alarm>alarms;
    private ArrayList<String> timeWakeUp;
    private ArrayList<String> notesAlarm;
    private ArrayList<Boolean> alarmIsOn;
    private Context context;
    private SimpleDateFormat sdf;
    private int widgetID;

    WidgetFactory(Context ctx, Intent intent) {
        context = ctx;
        sdf = new SimpleDateFormat("HH:mm:ss");
        widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        alarms = AlarmList.getInstance(context).getAlarmList();
        timeWakeUp = new ArrayList<>();
        notesAlarm = new ArrayList<>();
        alarmIsOn = new ArrayList<>();
        for (int i = 0; i <alarms.size() ; i++) {
            timeWakeUp.add(alarms.get(i).getFormatedTime());
            notesAlarm.add(alarms.get(i).getNote());
            alarmIsOn.add(alarms.get(i).isOn());
        }

    }

    @Override
    public int getCount() {
        return timeWakeUp.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView = new RemoteViews(context.getPackageName(),
                R.layout.item_widget);
        rView.setTextViewText(R.id.item_alarm_time_text, timeWakeUp.get(position));
        rView.setTextViewText(R.id.item_alarm_cycle_text, notesAlarm.get(position));
        if (alarms.get(position).isOn()){
            rView.setImageViewResource(R.id.item_take_of_alarm, R.drawable.on);
        }else {
            rView.setImageViewResource(R.id.item_take_of_alarm, R.drawable.off);
        }
        Intent clickIntent = new Intent();
        clickIntent.putExtra(ITEM_POSITION, position);
        rView.setOnClickFillInIntent(R.id.item_alarm_time_text, clickIntent);
        return rView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        timeWakeUp.clear();
        notesAlarm.clear();
        alarmIsOn.clear();
        for (int i = 0; i <alarms.size() ; i++) {
            timeWakeUp.add(alarms.get(i).getFormatedTime());
            notesAlarm.add(alarms.get(i).getNote());
            alarmIsOn.add(alarms.get(i).isOn());
        }
    }

    @Override
    public void onDestroy() {

    }
}
