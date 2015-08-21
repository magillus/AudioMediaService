package com.matsdevelopsolutions.service.audiomediaservicelib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.matsdevelopsolutions.service.audiomediaservicelib.IntentBroadcaster;

/**
 * Broadcast receiver that receives updates about current media position.
 */
public abstract class MediaPositionBroadcastReceiver extends BroadcastReceiver {
    /**
     * Logging tag.
     */
    private static final String TAG = MediaPositionBroadcastReceiver.class.getSimpleName();

    /**
     * Registers {MediaPositionBroadcastReceiver} with the context.
     *
     * @param context  Context.
     * @param receiver receiver to be registered with context.
     * @return register intent.
     */
    public static Intent register(Context context, MediaPositionBroadcastReceiver receiver) {
        return context.registerReceiver(receiver, getIntentFilter());
    }

    /**
     * Gets intent filter for this broadcast
     *
     * @return
     */
    public static IntentFilter getIntentFilter() {
        return new IntentFilter(IntentBroadcaster.ACTION_CURRENT_POSITION_CHANGE);
    }

    /**
     * Called on received intent.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            int currentPosition = intent.getIntExtra(IntentBroadcaster.CURRENT_POSITION_ARG, -1);
            if (currentPosition >= 0) {
                onCurrentPositionChanged(currentPosition);
            } else {
                Log.w(TAG, String.format("Received wrong value in %s intent.", IntentBroadcaster.ACTION_CURRENT_POSITION_CHANGE));
            }
        } catch (Exception ex) {
            Log.w(TAG, String.format("Error receiving intent %s", IntentBroadcaster.ACTION_CURRENT_POSITION_CHANGE), ex);
        }
    }

    /**
     * Called when current position event is received.
     *
     * @param currentPosition value of current position of media player.
     */
    public abstract void onCurrentPositionChanged(int currentPosition);
}
