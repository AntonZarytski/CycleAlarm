package zaritsky.com.cyclealarm.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;


public class CalendarWidget extends AppWidgetProvider {
    final String ACTION_ON_CLICK = "android.appwidget.action.APPWIDGET_UPDATE";
    public final static String ITEM_POSITION = "item_position";
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    List<Alarm> alarms;
    ArrayList<String> timeWakeUp;
    ArrayList<String> notesAlarm;
    ArrayList<Boolean> alarmIsOn;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        alarms = AlarmList.getInstance(context).getAlarmList();
        timeWakeUp = new ArrayList<>();
        notesAlarm = new ArrayList<>();
        alarmIsOn = new ArrayList<>();
        for (int i = 0; i < alarms.size(); i++) {
            timeWakeUp.add(alarms.get(i).getFormatedTime());
            notesAlarm.add(alarms.get(i).getNote());
            alarmIsOn.add(alarms.get(i).isOn());
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    void updateWidget(Context context, AppWidgetManager appWidgetManager,
                      int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.calendar_widget);
        setUpdateTV(rv, context, appWidgetId);

        setList(rv, context, appWidgetId);

        setListClick(rv, context, appWidgetId);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                R.id.list_view_for_widget);
    }

    void setUpdateTV(RemoteViews rv, Context context, int appWidgetId) {
        rv.setTextViewText(R.id.tvUpdate, "Мои будильники");

        Intent updIntent = new Intent(context, CalendarWidget.class);
        updIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[]{appWidgetId});
        PendingIntent updPIntent = PendingIntent.getBroadcast(context,
                appWidgetId, updIntent, 0);
        rv.setOnClickPendingIntent(R.id.tvUpdate, updPIntent);
    }

    void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, WidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
        adapter.setData(data);
        rv.setRemoteAdapter(R.id.list_view_for_widget, adapter);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    void setListClick(RemoteViews rv, Context context, int appWidgetId) {
        Intent listClickIntent = new Intent(context, CalendarWidget.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                listClickIntent, 0);
        rv.setPendingIntentTemplate(R.id.list_view_for_widget, listClickPIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(ACTION_ON_CLICK)) {
            int itemPos = intent.getIntExtra(ITEM_POSITION, -1);
            if (itemPos != -1) {
                Toast.makeText(context, "Clicked on item " + itemPos,
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
}

