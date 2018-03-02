package zaritsky.com.cyclealarm.models;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TypeOfDay {
   private String name;
   private Calendar wakeUp;
   private String timeOfWakeUp;
   private Color color;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TypeOfDay(String name, Calendar wakeUp, Color color) {
        this.name = name;
        this.wakeUp = wakeUp;
        timeOfWakeUp = new SimpleDateFormat("H:mm", new Locale("ru", "RU")).format(wakeUp.getTime());
        //TODO сделать выбор цвета для отображения на календаре
        this.color = color;
    }
    public TypeOfDay(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getWakeUp() {
        return wakeUp;
    }

    public void setWakeUp(Calendar wakeUp) {
        this.wakeUp = wakeUp;
    }

    public void setTimeOfWakeUp(String timeOfWakeUp) {
        this.timeOfWakeUp = timeOfWakeUp;
    }

    public String getTimeOfWakeUp() {
        return timeOfWakeUp;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
