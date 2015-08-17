package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IntRange;
import android.util.Log;

/**
 * Created by mateusz on 8/17/15.
 */
public abstract class MediaBufferProgressBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = MediaBufferProgressBroadcastReceiver.class.getSimpleName();

    /**
     * Returns intent filter for this broadcast reciever.
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
