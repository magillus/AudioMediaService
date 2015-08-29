package com.matsdevelopsolutions.service.audiomediaservicelib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;

import com.matsdevelopsolutions.service.audiomediaservicelib.IntentBroadcaster;
import com.matsdevelopsolutions.service.audiomediaservicelib.MediaInfo;

/**
 * Broadcast receiver that receives media info updates.
 */
public abstract class MediaInfoBroadcastReceiver extends BroadcastReceiver {

    /**
     * Logging tag.
     */
    private static final String TAG = MediaInfoBroadcastReceiver.class.getSimpleName();


    /**
     * Registers {MediaInfoBroadcastReceiver} with the context.
     *
     * @param context  Context.
     * @param receiver receiver to be registered with context.
     * @return register intent.
     */
    public static Intent register(Context context, MediaInfoBroadcastReceiver receiver) {
        return context.registerReceiver(receiver, getIntentFilter());
    }

    /**
     * Returns intent filter for this broadcast receiver.
     *
     * @return
     */
    public static IntentFilter getIntentFilter() {
        return new IntentFilter(IntentBroadcaster.ACTION_MEDIA_INFO_CHANGE);
    }

    /**
     * Called on received intent.
     *
     * @param context context
     * @param intent  received intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            MediaInfo mediaInfo = (MediaInfo) intent.getSerializableExtra(IntentBroadcaster.MEDIA_INFO_ARG);
            onMediaInfoChanged(mediaInfo);
        } catch (Exception ex) {
            Log.w(TAG, String.format("Error receiving intent %s", IntentBroadcaster.ACTION_MEDIA_INFO_CHANGE), ex);
        }
    }

    /**
     * Called when mediaInfo is received.
     *
     * @param mediaInfo
     */
    public abstract void onMediaInfoChanged(@Nullable MediaInfo mediaInfo);
}
