package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Class that manages notification and its styling/configuration.
 */
public class NotificationHelper {

    // randomize notification ID per applciation
    private static final int NOTIFICATION_ID = (int) System.currentTimeMillis();
    private Context context;
    private String currentStyle;
    private int currentFlags;
    private MediaInfo mediaInfo;
    private PendingIntent appPendingIntent;

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
    public NotificationHelper(final Context context) {
        this.context = context;
        // default app Intent
        Intent defaultApp = new Intent(Intent.ACTION_MAIN);
        defaultApp.setPackage(context.getPackageName());
        appPendingIntent = PendingIntent.getActivity(context, 0, defaultApp, PendingIntent.FLAG_ONE_SHOT);

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
    }

    public void updateMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }


    // todo find way to pass pending intent for opening main application

    /**
     * Updates notification based on state.
     *
     * @param state
     */
    public void updateNotification(MediaPlayerState state) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // buildNotification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(mediaInfo.title)
                .setContentTitle(mediaInfo.title)
                .setContentInfo(mediaInfo.description)
                .setSmallIcon(R.drawable.abc_btn_radio_material)
                .setContentTitle(mediaInfo.title);
        builder.setContentText(mediaInfo.description);
        // download art
        //builder.setLargeIcon(mediaInfo.)
        // buttons based on state
        switch (state) {
            case PAUSED:
            case COMPLETE:
                builder.addAction(android.R.drawable.ic_media_play, context.getString(R.string.label_play),
                        PendingIntent.getService(context, 0, IntentGenerator.createPlayToggleIntent(context), PendingIntent.FLAG_ONE_SHOT));
                break;
            case STARTED:
                builder.addAction(android.R.drawable.ic_media_pause, context.getString(R.string.label_pause),
                        PendingIntent.getService(context, 0, IntentGenerator.createPauseIntent(context), PendingIntent.FLAG_ONE_SHOT));
                break;
            default:
                notificationManager.cancel(NOTIFICATION_ID);
                return;
        }
        builder.addAction(android.R.drawable.ic_delete, context.getString(R.string.label_stop),
                PendingIntent.getService(context, 0, IntentGenerator.createStopIntent(context), PendingIntent.FLAG_ONE_SHOT));


        builder.setDeleteIntent(PendingIntent.getService(context, 0,
                IntentGenerator.createStopIntent(context),
                PendingIntent.FLAG_ONE_SHOT));

        if (appPendingIntent != null) {
            builder.setContentIntent(appPendingIntent);
        }

        Notification notification = builder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.
                notify(NOTIFICATION_ID, notification);
    }

    public void clear() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
