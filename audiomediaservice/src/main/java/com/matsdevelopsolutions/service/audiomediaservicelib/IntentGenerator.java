package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Intent;
import android.support.annotation.FloatRange;

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
                                          final String description, final String artUrl,
                                          final boolean autoplay, final int notificationFlag, final String notificationStyle) {
        Intent intent = new Intent(AudioMediaService.ACTION_PLAY);
        intent.putExtra(AudioMediaService.SOURCE_URL_ARG, url);
        intent.putExtra(AudioMediaService.SOURCE_TITLE_ARG, title);
        intent.putExtra(AudioMediaService.SOURCE_DESC_ARG, description);
        intent.putExtra(AudioMediaService.SOURCE_ART_URI_ARG, artUrl);
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
     * Creates default play intent for mediaInfo.
     *
     * @param mediaInfo
     * @return
     */
    public static Intent createPlayIntent(MediaInfo mediaInfo) {
        return createPlayIntent(mediaInfo.streamUrl, mediaInfo.title,
                mediaInfo.description, mediaInfo.artUri, true,
                AudioMediaService.DEFAULT_NOTIFICATION_FLAG, AudioMediaService.FLAG_NOTIFICATION_STYLE_NORMAL);
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

    /**
     * Creates media player mute toggle intent.s
     *
     * @return
     */
    public static Intent createToggleMuteIntent() {
        return new Intent(AudioMediaService.ACTION_MUTE_TOGGLE);
    }

    /**
     * Creates change volume intent.
     *
     * @param volume volume value from 0 to 1.0
     * @return volume change intent
     */
    public static Intent createChangeVolumeIntent(@FloatRange(from = 0, to = 1f) final float volume) {
        Intent intent = new Intent(AudioMediaService.ACTION_CHANGE_VOLUME);
        intent.putExtra(AudioMediaService.VOLUME_VALUE_ARG, volume);
        return intent;
    }

}
