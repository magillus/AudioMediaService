package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.FloatRange;

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
     * Action for media current position change intent.
     */
    public static final String ACTION_CURRENT_POSITION_CHANGE = IntentBroadcaster.class.getName() + ".ACTION_CURRENT_POSITION_CHANGE";

    /**
     * Action for volume change intent.
     */
    public static final String ACTION_VOLUME_CHANGE = "ACTION_VOLUME_CHANGE";

    /**
     * Media info value = Intent extra argument.
     */
    public static final String MEDIA_INFO_ARG = "MEDIA_INFO_ARG";
    /**
     * Buffer progress value - Intent extra argument.
     */
    public static final String BUFFER_PROGRESS_ARG = "BUFFER_PROGRESS_ARG";
    /**
     * Media player status - Intent extra argument.
     */
    public static final String MEDIA_STATUS_ARG = "MEDIA_STATUS_ARG";
    /**
     * Media player current position - Intent extra argument.
     */
    public static final String CURRENT_POSITION_ARG = "CURRENT_POSITION_ARG";

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
        Intent intent = new Intent(ACTION_BUFFER_PROGRESS);
        intent.putExtra(BUFFER_PROGRESS_ARG, bufferProgress);
        context.sendBroadcast(intent);
    }

    /**
     * Broadcasts media info changed intent.
     *
     * @param mediaInfo
     */
    public void mediaInfoChanged(MediaInfo mediaInfo) {
        Intent intent = new Intent(ACTION_MEDIA_INFO_CHANGE);
        intent.putExtra(MEDIA_INFO_ARG, mediaInfo);
        context.sendBroadcast(intent);
    }

    /**
     * Broadcasts media player position.
     *
     * @param currentPosition
     */
    public void currentPosition(int currentPosition) {
        Intent intent = new Intent(ACTION_CURRENT_POSITION_CHANGE);
        intent.putExtra(CURRENT_POSITION_ARG, currentPosition);
        context.sendBroadcast(intent);
    }

    /**
     * Broadcasts volume value.
     *
     * @param volume
     */
    public void volume(@FloatRange(from = 0f, to = 1.0) float volume) {
        Intent intent = new Intent(ACTION_VOLUME_CHANGE);
        intent.putExtra(AudioMediaService.VOLUME_VALUE_ARG, volume);
        context.sendBroadcast(intent);
    }
}
