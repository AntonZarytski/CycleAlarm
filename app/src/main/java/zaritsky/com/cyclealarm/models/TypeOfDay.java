package zaritsky.com.cyclealarm.models;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TypeOfDay implements Serializable {
   private String name;
   private Calendar wakeUp;
   private String timeOfWakeUp;
   private int color;

    @SuppressLint("NewApi")
    public TypeOfDay(String name, Calendar wakeUp, int color) {
        this.name = name;
        this.wakeUp = wakeUp;
        //TODO рахобраться с временем
        timeOfWakeUp = new SimpleDateFormat("H:mm", new Locale("ru", "RU")).format(wakeUp.getTime());
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
