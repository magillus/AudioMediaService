package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by mateusz on 8/17/15.
 */
public abstract class PlayerStateBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = PlayerStateBroadcastReceiver.class.getSimpleName();


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
