package zaritsky.com.cyclealarm.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;


class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mcontext;
    private List<String> datasForWidget;
    private List<Alarm> alarms;

    public WidgetFactory(Context context, Intent intent) {
        mcontext = context;
    }
    //что надо сделать при создании, например, создаем список наших заметок.
    @Override
    public void onCreate() {
        alarms = AlarmList.getInstance(mcontext).getAlarmList();
        datasForWidget = new ArrayList<>();
        for (int i = 0; i <alarms.size() ; i++) {
            datasForWidget.add(alarms.get(i).getFormatedTime());
        }
    }
    //вызывается, когда меняется набор данных (список), поэтому в нем загружаем наш список из базы.
//перезаписываем данные в виджет
    @Override
    public void onDataSetChanged() {
        datasForWidget.clear();
        for (int i = 0; i <alarms.size() ; i++) {
            datasForWidget.add(alarms.get(i).getFormatedTime());
        }
    }
    //возвращает число элементов в Factory: у нас это — число элементов в списке.
    @Override
    public int getCount() {
        return datasForWidget.size();
    }
    //мы возвращаем null, что означает использование стандартного загрузочного View:
//иначе позволяет задать его для настройки, вызывается перед каждым getViewAt().
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }
    //создаем ячейку по позиции position: она должна быть типа RemoteViews.
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView = new RemoteViews(mcontext.getPackageName(), R.layout.item_widget);
        rView.setTextViewText(R.id.text_view_for_widget, datasForWidget.get(position));
        Intent clickIntent = new Intent();
        clickIntent.putExtra(CalendarWidget.DATA, datasForWidget.get(position));
        rView.setOnClickFillInIntent(R.id.text_view_for_widget, clickIntent);
        return rView;
    }
    //возвращает число типов элементов в Factory: у нас всегда одинаковая ячейка, поэтому 1.
    @Override
    public int getViewTypeCount() {
        return 1;
    }
    //возвращает id элемента по позиции в списке: делаем id, равным позиции в списке.
    @Override
    public long getItemId(int position) {
        return position;
    }
    //возвращает true, если id всегда ссылается на тот же объект.
    @Override
    public boolean hasStableIds() {
        return true;
    }
    //вызывается при отсоединении последнего RemoteViewsAdapter, привязанного к нему.
    @Override
    public void onDestroy() {
    }
}
