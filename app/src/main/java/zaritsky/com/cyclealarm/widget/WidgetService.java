package zaritsky.com.cyclealarm.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

//этот класс будет формировать список и отслеживать изменения в нем. Для
//        этого создаем класс, наследуемый от RemoteViewsService:
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetFactory(this.getApplicationContext(), intent);
    }
}
