package com.matsdevelopsolutions.service.audiomediaservicelib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.matsdevelopsolutions.service.audiomediaservicelib.IntentBroadcaster;
import com.matsdevelopsolutions.service.audiomediaservicelib.MediaPlayerState;

/**
 * Broadcast receiver that receives media player state updates.
 */
public abstract class PlayerStateBroadcastReceiver extends BroadcastReceiver {

    /**
     * Logging tag.
     */
    public static final String TAG = PlayerStateBroadcastReceiver.class.getSimpleName();

    /**
     * Registers {PlayerStateBroadcastReceiver} with the context.
     *
     * @param context  Context.
     * @param receiver receiver to be registered with context.
     * @return register intent.
     */
    public static Intent register(Context context, PlayerStateBroadcastReceiver receiver) {
        return context.registerReceiver(receiver, getIntentFilter());
    }

    /**
     * Returns intent filter for this broadcast receiver.
     *
     * @return intent filter.
     */
    public static IntentFilter getIntentFilter() {
        return new IntentFilter(IntentBroadcaster.ACTION_STATE_CHANGE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String statusString = intent.getStringExtra(IntentBroadcaster.MEDIA_STATUS_ARG);
            MediaPlayerState playerState = MediaPlayerState.valueOf(statusString);
            onPlayerStateChanged(playerState);
        } catch (Exception ex) {
            Log.w(TAG, "Error parsing media status change.", ex);
        }
    }

    /**
     * Called when received player state change broadcast.
     *
     * @param playerState current player state
     */
    protected abstract void onPlayerStateChanged(MediaPlayerState playerState);
}
