package zaritsky.com.cyclealarm.models;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CycleList implements Serializable{
    private List<Cycle> cycleList;
    private transient Context context;
    private static CycleList ourInstance = null;

    public static CycleList getInstance(Context context) {
        if (ourInstance==null){
            return ourInstance = new CycleList(context);
        }
        return ourInstance;
    }

    public static CycleList getOurInstance() {
        return ourInstance;
    }

    private CycleList(Context context) {
        cycleList = new ArrayList<>();
    }

    public void addToCycles(Cycle cycle){
        cycleList.add(cycle);
    }
    public void removeCycle(Cycle cycle){
        cycleList.remove(cycle);
    }

    public List<Cycle> getCycleList() {
        return cycleList;
    }

    public void setCycleList(List<Cycle> cycleList) {
        this.cycleList = cycleList;
    }
}
