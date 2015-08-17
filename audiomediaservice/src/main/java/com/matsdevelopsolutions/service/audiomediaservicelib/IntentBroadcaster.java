package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Context;
import android.content.Intent;

/**
 * Broadcasts intents for an defined actions
 */
public class IntentBroadcaster {

    /**
     * Action for buffer progress intent.
     */
    public static final String ACTION_BUFFER_PROGRESS = IntentBroadcaster.class.getName() + ".ACTION_BUFFER_PROGRESS";

    /**
     * Action for audio state change intent.
     */
    public static final String ACTION_STATE_CHANGE = IntentBroadcaster.class.getName() + ".ACTION_STATE_CHANGE";

    /**
     * Action for media info change intent.
     */
    public static final String ACTION_MEDIA_INFO_CHANGE = IntentBroadcaster.class.getName() + ".ACTION_MEDIA_INFO_CHANGE";


    /**
     * Media info value = Intent extra argument.
     */
    static final String MEDIA_INFO_ARG = "MEDIA_INFO_ARG";
    /**
     * Buffer progress value - Intent extra argument.
     */
    static final String BUFFER_PROGRESS_ARG = "BUFFER_PROGRESS_ARG";
    /**
     * Media player status - Intent extra argument.
     */
    static final String MEDIA_STATUS_ARG = "MEDIA_STATUS_ARG";
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

    /**
     * Broadcasts media player state changed intent.
     *
     * @param state
     */
    public void stateChange(MediaPlayerState state) {
        Intent intent = new Intent(ACTION_STATE_CHANGE);
        intent.putExtra(MEDIA_STATUS_ARG, state.name());
        context.sendBroadcast(intent);
    }

    /**
     * Broadcasts buffering progress intent.
     *
     * @param bufferProgress
     */
    public void buffering(int bufferProgress) {

    }

    /**
     * Broadcasts media info changed intent.
     *
     * @param mediaInfo
     */
    public void mediaInfoChanged(MediaInfo mediaInfo) {

    }

}
