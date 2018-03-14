package zaritsky.com.cyclealarm.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;
import zaritsky.com.cyclealarm.R;


public class CalendarWidget extends AppWidgetProvider {
    public static final String UPDATE_WIDGET_ACTION
            = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String ITEM_ON_CLIC_ACTION
            = "android.appwidget.action.ITEM_ON_CLICK";
    public static final String DATA = "some_data";
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calendar_widget);
        setList(views, context, appWidgetId);
        //CharSequence widgetText = context.getString(R.string.mywidget);
        //views.setTextViewText(R.id.text_view, widgetText);
        setListClick(views, context);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    //метод setListClick в WidgetNotes, который будет создавать события при нажатии на список:
    void setListClick(RemoteViews views, Context context){
        Intent listClickIntent = new Intent(context, CalendarWidget.class);
        listClickIntent.setAction(ITEM_ON_CLIC_ACTION);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0);
        views.setPendingIntentTemplate(R.id.list_view_for_widget, listClickPIntent);
    }

    private void setList(RemoteViews views, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, WidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.list_view_for_widget, adapter);
        views.setEmptyView(R.id.list_view_for_widget, R.id.empty_view_for_widget);
    }

    //    Вызывается в соответствии с установленным интервалом updatePeriodMillis и при добавлении
//    виджета. Он выполняет настройку, такую как определение обработчиков событий для View. Однако
//    если открывается Activity настроек для виджета, то первоначальные настройки выполняются там.
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    //    Вызывается для каждого broadcast запроса и callback метода. Чаще всего его можно не
//    реализовывать, т.к. базовая реализация все фильтрует и автоматически вызывает нужные методы.
    @Override
    public void onReceive(Context context, Intent intent) {
//        Метод onReceive() обрабатывает событие UPDATE_MEETING_ACTION, которое обновляет виджет
//        при изменении списка.
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equalsIgnoreCase(UPDATE_WIDGET_ACTION)){
            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, CalendarWidget.class));
            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_for_widget);
        }
        //добавим обработку нашего нового события. В нем просто покажем Toast с нужным нам текстом:
        if (intent.getAction().equalsIgnoreCase(ITEM_ON_CLIC_ACTION)){
            String itemText = intent.getStringExtra(DATA);
            if (!itemText.equalsIgnoreCase("")){
                Toast.makeText(context, itemText, Toast.LENGTH_SHORT).show();
            }
        }
        super.onReceive(context, intent);
    }

    //    Вызывается после первого помещения виджета на рабочий стол и каждый раз, когда происходит
//    изменение его размеров. Этот метод используется, если вы хотите скрывать или показывать контент
//    виджета, основываясь на его размере. Однако этот метод был реализован в версии 4.1(API Level 16),
//    в более ранних версиях приложение будет падать.
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    //Будет вызываться каждый раз при удалении виджета.
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    //Вызывается только, когда виджет создается первый раз.
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    //Когда последний виджет этого класса будет удален, вызовется этот метод.
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

