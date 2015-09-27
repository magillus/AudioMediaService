package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.FloatRange;

/**
 * Helper class that creates intents for {AudioMediaService}.
 */
public final class IntentGenerator {

    /**
     * Creates intent with given arguments
     *
     * @param context context
     * @param url              media url (required)
     * @param title            media title (required)
     * @param description      media short description
     * @param autoplay         true if autoplay is on
     * @param resumePlayback   resumes playback of audio from last position.
     * @param notificationFlag notification flag.
     * @return play intent
     */
    public static Intent createPlayIntent(Context context, final String url, final String title,
                                          final String description, final String artUrl,
                                          final boolean autoplay, final boolean resumePlayback,
                                          final int notificationFlag, final String notificationStyle) {
        Intent intent = new Intent(AudioMediaService.ACTION_PLAY);
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        intent.putExtra(AudioMediaService.SOURCE_URL_ARG, url);
        intent.putExtra(AudioMediaService.SOURCE_TITLE_ARG, title);
        intent.putExtra(AudioMediaService.SOURCE_DESC_ARG, description);
        intent.putExtra(AudioMediaService.SOURCE_ART_URI_ARG, artUrl);
        intent.putExtra(AudioMediaService.AUTO_PLAY_ARG, autoplay);
        intent.putExtra(AudioMediaService.RESUME_PLAY_ARG, resumePlayback);
        intent.putExtra(AudioMediaService.NOTIFICATION_CONFIG_FLAG_ARG, notificationFlag);
        intent.putExtra(AudioMediaService.NOTIFICATION_STYLE_ARG, notificationStyle);
        return intent;
    }

    /**
     * Creates notification update intent
     *
     * @param context context
     * @param notificationStyle notification style, value of: todo
     * @param notificationFlag  notification configuration flag
     * @return notification update intent.
     */
    public static Intent createNotificationUpdateIntent(Context context, final String notificationStyle, final int notificationFlag) {
        Intent notificationIntent = new Intent(AudioMediaService.ACTION_NOTIFICATION_STYLE);
        notificationIntent.setComponent(new ComponentName(context, AudioMediaService.class));
        notificationIntent.putExtra(AudioMediaService.NOTIFICATION_STYLE_ARG, notificationStyle);
        notificationIntent.putExtra(AudioMediaService.NOTIFICATION_CONFIG_FLAG_ARG, notificationFlag);
        return notificationIntent;
    }

    /**
     * Creates play intent of currently loaded media.
     *
     * @param context context
     * @return play intent
     */
    public static Intent createPlayIntent(Context context) {
        Intent intent = new Intent(AudioMediaService.ACTION_PLAY);
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        return intent;
    }

    /**
     * Creates default play intent for mediaInfo.
     *
     * @param context context
     * @param mediaInfo
     * @param resumePlayback if playback resumes from previous location -per url
     * @return
     */
    public static Intent createPlayIntent(Context context, MediaInfo mediaInfo, boolean resumePlayback) {
        return createPlayIntent(context, mediaInfo.streamUrl, mediaInfo.title,
                mediaInfo.description, mediaInfo.artUri, true, resumePlayback,
                AudioMediaService.DEFAULT_NOTIFICATION_FLAG, AudioMediaService.FLAG_NOTIFICATION_STYLE_NORMAL);
    }

    /**
     * Creates media player stop intent.
     *
     * @param context context
     * @return stop intent
     */
    public static Intent createStopIntent(Context context) {
        Intent intent = new Intent(AudioMediaService.ACTION_STOP);
        ;
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        return intent;
    }

    /**
     * Creates media player pause intent.
     *
     * @param context context
     * @return pause intent
     */
    public static Intent createPauseIntent(Context context) {
        Intent intent = new Intent(AudioMediaService.ACTION_PAUSE);
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        return intent;
    }

    /**
     * Creates media player play/pause toggle intent.
     *
     * @param context context
     * @return intent
     */
    public static Intent createPlayToggleIntent(Context context) {
        Intent intent = new Intent(AudioMediaService.ACTION_PLAY_TOGGLE);
        ;
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        return intent;
    }

    /**
     * Creates media player seek intent.
     *
     * @param context context
     * @param position seek position in miliseconds
     * @return seek intent.
     */
    public static Intent createSeekIntent(Context context, final int position) {
        Intent intent = new Intent(AudioMediaService.ACTION_SEEK);
        intent.putExtra(AudioMediaService.SEEK_POSITION_ARG, position);
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        return intent;
    }

    /**
     * Creates media player seek by delta intent.
     *
     * @param context   context
     * @param seekDelta seek delta position in miliseconds
     * @return seek by intent
     */
    public static Intent createSeekByIntent(Context context, final int seekDelta) {
        Intent intent = new Intent(AudioMediaService.ACTION_SEEK_BY);
        intent.putExtra(AudioMediaService.SEEK_POSITION_DELTA_ARG, seekDelta);
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        return intent;
    }

    /**
     * Creates media player mute toggle intent.s
     *
     * @param context context
     * @return
     */
    public static Intent createToggleMuteIntent(Context context) {
        Intent intent = new Intent(AudioMediaService.ACTION_MUTE_TOGGLE);
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        return intent;
    }

    /**
     * Creates change volume intent.
     *
     * @param context context
     * @param volume volume value from 0 to 1.0
     * @return volume change intent
     */
    public static Intent createChangeVolumeIntent(final Context context, @FloatRange(from = 0, to = 1f) final float volume) {
        Intent intent = new Intent(AudioMediaService.ACTION_CHANGE_VOLUME);
        intent.setComponent(new ComponentName(context, AudioMediaService.class));
        intent.putExtra(AudioMediaService.VOLUME_VALUE_ARG, volume);
        return intent;
    }

}
