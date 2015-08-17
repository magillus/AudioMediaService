package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Context;

/**
 * Broadcasts intents for an defined actions
 */
public class IntentBroadcaster {

    /**
     * Context.
     */
    private Context context;

    /**
     * Creates instance of broacaster for the given context.
     *
     * @param context
     */
    public IntentBroadcaster(final Context context) {
        this.context = context;
    }

    public void stateChange(MediaPlayerState state) {

    }
}
