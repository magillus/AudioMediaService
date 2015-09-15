package com.matsdevelopsolutions.service.audiomediaservicelib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IntRange;
import android.util.Log;

import com.matsdevelopsolutions.service.audiomediaservicelib.IntentBroadcaster;

/**
 * Broadcast receiver that process buffering progress messages.
 */
public abstract class MediaBufferProgressBroadcastReceiver extends BroadcastReceiver {

    /**
     * Logging tag.
     */
    private static final String TAG = MediaBufferProgressBroadcastReceiver.class.getSimpleName();


    /**
     * Registers {MediaBufferProgressBroadcastReceiver} with the context.
     *
     * @param context  Context.
     * @param receiver receiver to be registered with context.
     * @return register intent.
     */
    public static Intent register(Context context, MediaBufferProgressBroadcastReceiver receiver) {
        return context.registerReceiver(receiver, getIntentFilter());
    }

    /**
     * Returns intent filter for this broadcast receiver.
     *
     * @return
     */
    public static IntentFilter getIntentFilter() {
        return new IntentFilter(IntentBroadcaster.ACTION_BUFFER_PROGRESS);
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
            int bufferProgressValue = intent.getIntExtra(IntentBroadcaster.BUFFER_PROGRESS_ARG, -1);
            if (bufferProgressValue >= 0) {
                onBufferProgressChanged(bufferProgressValue);
            } else {
                Log.w(TAG, String.format("Received wrong value in %s intent.", IntentBroadcaster.ACTION_BUFFER_PROGRESS));
            }
        } catch (Exception ex) {
            Log.w(TAG, String.format("Error receiving intent %s.", IntentBroadcaster.ACTION_BUFFER_PROGRESS));
        }

    }

    /**
     * Called when buffer progress changed event is received.
     *
     * @param progress progress value in 0-100;
     */
    public abstract void onBufferProgressChanged(@IntRange(from = 0, to = 100) final int progress);

}
