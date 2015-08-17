package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Intent;

/**
 * Helper class that creates intents for {AudioMediaService}.
 */
public final class IntentGenerator {

    /**
     * Creates intent with given arguments
     *
     * @param url              media url (required)
     * @param title            media title (required)
     * @param description      media short description
     * @param autoplay         true if autoplay is on
     * @param notificationFlag notofication flag.
     * @return play intent
     */
    public static Intent createPlayIntent(final String url, final String title,
                                          final String description, final boolean autoplay,
                                          final int notificationFlag, final String notificationStyle) {
        Intent intent = new Intent(AudioMediaService.ACTION_PLAY);
        intent.putExtra(AudioMediaService.SOURCE_URL_ARG, url);
        intent.putExtra(AudioMediaService.SOURCE_TITLE_ARG, title);
        intent.putExtra(AudioMediaService.SOURCE_DESC_ARG, description);
        intent.putExtra(AudioMediaService.AUTO_PLAY_ARG, autoplay);
        intent.putExtra(AudioMediaService.NOTIFICATION_CONFIG_FLAG_ARG, notificationFlag);
        intent.putExtra(AudioMediaService.NOTIFICATION_STYLE_ARG, notificationStyle);
        return intent;
    }

    /**
     * Creates notification update intent
     *
     * @param notificationStyle notification style, value of: todo
     * @param notificationFlag  notification configuraiton flag
     * @return notification update intent.
     */
    public static Intent createNotificationUpdateIntent(final String notificationStyle, final int notificationFlag) {
        Intent notificationIntent = new Intent(AudioMediaService.ACTION_NOTIFICATION_STYLE);
        notificationIntent.putExtra(AudioMediaService.NOTIFICATION_STYLE_ARG, notificationStyle);
        notificationIntent.putExtra(AudioMediaService.NOTIFICATION_CONFIG_FLAG_ARG, notificationFlag);
        return notificationIntent;
    }

    /**
     * Creates play intent of currently loaded media.
     *
     * @return play intent
     */
    public static Intent createPlayIntent() {
        return new Intent(AudioMediaService.ACTION_PLAY);
    }

    /**
     * Creates media player stop intent.
     *
     * @return stop intent
     */
    public static Intent createStopIntent() {
        return new Intent(AudioMediaService.ACTION_STOP);
    }

    /**
     * Creates media player pause intent.
     *
     * @return pause intent
     */
    public static Intent createPauseIntent() {
        return new Intent(AudioMediaService.ACTION_PAUSE);
    }

    /**
     * Creates media player play/pause toggle intent.
     *
     * @return intent
     */
    public static Intent createPlayToggleIntent() {
        return new Intent(AudioMediaService.ACTION_PLAY_TOGGLE);
    }

    /**
     * Creates media player seek intent.
     *
     * @param position seek position in seconds
     * @return seek intent.
     */
    public static Intent createSeekIntent(final int position) {
        Intent intent = new Intent(AudioMediaService.ACTION_SEEK);
        intent.putExtra(AudioMediaService.SEEK_POSITION_ARG, position);
        return intent;
    }
}
