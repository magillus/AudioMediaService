package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Context;

/**
 * Class that manages notification and its styling/configuration.
 */
public class NotificationManager {

    private Context context;
    private String currentStyle;
    private int currentFlags;
    private MediaInfo mediaInfo;

    public String getCurrentStyle() {
        return currentStyle;
    }

    public int getCurrentFlags() {
        return currentFlags;
    }

    /**
     * Creates instance of {NotificationManager}.
     *
     * @param context
     */
    public NotificationManager(final Context context) {
        this.context = context;
    }

    /**
     * Updates notification style
     *
     * @param style      - style name
     * @param configFlag - configuration flags
     * @param mediaInfo
     */
    public void updateStyle(final String style, final int configFlag, MediaInfo mediaInfo) {
        this.currentFlags = configFlag;
        this.currentStyle = style;
        this.mediaInfo = mediaInfo;
        updateNotification();
    }

    public void updateMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
        updateNotification();
    }

    private void updateNotification() {

    }

}
