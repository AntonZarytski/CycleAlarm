package zaritsky.com.cyclealarm.models;

/**
 * Created by Anton&&Natasha on 02.03.2018.
 */

public class CycleList {
    private static final CycleList ourInstance = new CycleList();

    public static CycleList getInstance() {
        return ourInstance;
    }

    private CycleList() {
    }
}
