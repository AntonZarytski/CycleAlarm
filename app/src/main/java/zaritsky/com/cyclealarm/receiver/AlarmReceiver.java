package zaritsky.com.cyclealarm.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import zaritsky.com.cyclealarm.activities.ArarmIsActive;

/**
 * AlarmReceiver call Activity when alarm must by start
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context, ArarmIsActive.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
