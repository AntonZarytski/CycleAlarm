package zaritsky.com.cyclealarm.models;

/**
 * Created by Anton&&Natasha on 01.03.2018.
 */

public class Cycle {
    private static final Cycle ourInstance = new Cycle();

    public static Cycle getInstance() {
        return ourInstance;
    }

    private Cycle() {
    }
}
