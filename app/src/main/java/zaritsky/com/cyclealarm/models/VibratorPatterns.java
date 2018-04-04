package zaritsky.com.cyclealarm.models;

import java.util.ArrayList;
import java.util.List;

/**
 * singleton clas contains ist<long[]> with paterns for Vibrator service
 */
public class VibratorPatterns {
    private static final VibratorPatterns ourInstance = new VibratorPatterns();
    private static List<long[]> vibratorData;

    public static VibratorPatterns getInstance() {
        return ourInstance;
    }

    private VibratorPatterns() {
        vibratorData = new ArrayList<>();
        vibratorData.add(new long[]{0L, 3000L});
        vibratorData.add(new long[]{500L, 500L, 500L, 500L, 500L, 1500L});
        vibratorData.add(new long[]{200L, 200L, 200L, 200L, 200L, 600L});
        vibratorData.add(new long[]{100L, 50L, 100L, 50L, 100L, 50L});
        vibratorData.add(new long[]{50L, 1000L, 50L, 1000L, 50L, 1000L});
        vibratorData.add(new long[]{50L, 70L, 70L, 70L, 30L, 50L, 30L, 70L, 70L, 70L, 70L, 70L, 30L, 50L, 30L, 70L});
    }

    public static List<long[]> getVibratorData() {
        return vibratorData;
    }
}
