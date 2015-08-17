package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Audio service that keep instance of MediaPlayer to play a audio stream.
 * Interaction with AudioMediaService happens through Intent calls.
 * Notification branding and action flags are also passed through Intent calls.
 */
public class AudioMediaService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener {

    /**
     * Intent action to start playback of media.
     * Required Extras:
     * <li>{SOURCE_URL_ARG} - Media Url - (optional if already media source is loaded) - plays a media stream</li>
     * <li>{SOURCE_TITLE_ARG} - Media title - (optional if notification is not used)</li>
     * Optional parameter:
     * <li>{SOURCE_DESC_ARG} - Media description</li>
     * <li>{SOURCE_ART_URI_ARG} - Media art URI - local file or url</li>
     * <li>{NOTIFICATION_FLAG_ARG} - Show notification flag -  - will show player notification - requires title, icon/art</li>
     */
    public static final String ACTION_PLAY = PACKAGE_NAME + "AudioMediaService.PLAY";
    /**
     * Intent action to pause playback of media.
     * Note: On live stream it will stop playback.
     */
    public static final String ACTION_PAUSE = PACKAGE_NAME + "AudioMediaService.PAUSE";
    /**
     * Intent to stop playback and release media resources - it will also hide notification if showing.
     */
    public static final String ACTION_STOP = PACKAGE_NAME + "AudioMediaService.STOP";
    /**
     * Intent to seek non-live stream to a given position
     * Required Extras:
     * <li>{SEEK_POSITION_ARG} - position in secons to media stream seek to</li>
     */
    public static final String ACTION_SEEK = PACKAGE_NAME + "AudioMediaService.SEEK_TO";
    /**
     * Intent to update style of notification bar.
     * Extras:
     * <li>{NOTIFICATION_STYLE_ARG} - style type for notification</li>
     * <li>{NOTIFICATION_BACKGROUND_ARG} - color of the background</li>
     * <li>{NOTIFICATION_CONFIG_FLAG_ARG} - flags to enable/disable features on notification</li>
     */
    public static final String ACTION_NOTIFICATION_STYLE = PACKAGE_NAME + "AudioMediaService.NOTIFICATION_STYLE";
    private static final String PACKAGE_NAME = "com.matsdevelopsolutions.service.audiomediaservicelib.";
    /**
     * Media source url extras name.
     */
    private static final String SOURCE_URL_ARG = "SOURCE_URL_ARG";
    /**
     * Media source title extras name.
     */
    private static final String SOURCE_TITLE_ARG = "SOURCE_TITLE_ARG";
    /**
     * Media source short description extras name.
     */
    private static final String SOURCE_DESC_ARG = "SOURCE_DESC_ARG";
    /**
     * Media source art/icon URI extras name.
     */
    private static final String SOURCE_ART_URI_ARG = "SOURCE_ART_URI_ARG";
    /**
     * Media source seek position in seconds extras name.
     */
    private static final String SEEK_POSITION_ARG = "SEEK_POSITION_ARG";

    /**
     * Notification style extras name.
     */
    private static final String NOTIFICATION_STYLE_ARG = "NOTIFICATION_STYLE_ARG";
    /**
     * Notification configuration flag extras name.
     */
    private static final String NOTIFICATION_CONFIG_FLAG_ARG = "NOTIFICATION_CONFIG_FLAG_ARG";

    /**
     * Notification style normal - Notification style flag.
     */
    private static final String FLAG_NOTIFICATION_STYLE_NORMAL = "FLAG_NOTIFICATION_STYLE_NORMAL";
    /**
     * Notification style compact - Notification style flag.
     */
    private static final String FLAG_NOTIFICATION_STYLE_COMPACT = "FLAG_NOTIFICATION_STYLE_COMPACT";

    /**
     * Hide notification - Notification configuration flag.
     */
    private static final int FLAG_NOTIFICATION_HIDE = 0;
    /**
     * Show notification - Notification configuration flag.
     */
    private static final int FLAG_NOTIFICATION_SHOW = 0b1;
    /**
     * Show play/pause as toggle - Notification configuration flag.
     */
    private static final int FLAG_NOTIFICATION_SHOW_BUTTON_PLAY_TOGGLE = 0b10;
    /**
     * Show stop/close button - Notification configuration flag.
     */
    private static final int FLAG_NOTIFICATION_SHOW_BUTTON_STOP = 0b100;
    /**
     * Use pallette colors based on art's image - Notification configuration flag.
     */
    private static final int FLAG_NOTIFICATION_PALLETE_BACKGROUND = 0b1000;

    /**
     * Logging tag.
     */
    private static final String TAG = AudioMediaService.class.getSimpleName();
    /**
     * Media player instance.
     */
    private MediaPlayer mediaPlayer;
    /**
     * Media player state
     */
    private MediaPlayerState playerState;

    private NotificationManager notificationManager;
    private String mediaStreamUrl;

    protected void setPlayerState(MediaPlayerState state) {
        this.playerState = state;
        // broadcast state change Intent
    }

    public AudioMediaService() {
        initMediaPlayer();
        notificationManager = new NotificationManager();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Communication with service allowed only through Intents. Use IntentGenerator");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        switch (action) {
            case ACTION_PLAY:
                String newUrl = fetchStringParameter(intent, SOURCE_URL_ARG);
                if (newUrl.equalsIgnoreCase(mediaStreamUrl)) {
                    // same stream
                    start();
                } else {
                    // enable autoplay and
                }
                // fetch extra data

                return START_CONTINUATION_MASK;

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setPlayerState(MediaPlayerState.COMPLETE);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // report error
        // broadcast error from player
        setPlayerState(MediaPlayerState.ERROR);
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        // no change on player state
        // broadcast seek complete intent
    }

    /**
     * Safe reset of media Player instance.
     */
    protected void reset(boolean force) {
        if (!playerAtStates(MediaPlayerState.END, MediaPlayerState.ERROR)) {
            mediaPlayer.reset();
        } else if (force) {
            release();
            initMediaPlayer();
        }
    }

    /**
     * Safe set data source for media player.
     *
     * @param url
     * @param force if true, it forces to set data source, if state is not IDLE it will reset media player.
     */
    protected void setDataSource(String url, boolean force) {
        if (playerAtStates(MediaPlayerState.IDLE)) {
            try {
                mediaPlayer.setDataSource(this, Uri.parse(url));
            } catch (IOException e) {
                Log.w(TAG, String.format("Error setting data source, url = %s", url), e);
            }
        } else if (force) {
            reset(true);
            setDataSource(url, false);
        }
    }

    /**
     * Safe release of media player instance.
     */
    protected void release() {
        mediaPlayer.release();
        setPlayerState(MediaPlayerState.END);
    }

    /**
     * Safe prepare method for media player.
     */
    protected void prepare() {
        if (playerAtStates(MediaPlayerState.INITIALIZED, MediaPlayerState.STOPPED)) {
            try {
                mediaPlayer.prepare();
                setPlayerState(MediaPlayerState.PREPARED);
            } catch (IOException e) {
                Log.w(TAG, "Prepare of data source is not possible.", e);
            }
        }
    }

    /**
     * Safe seek to media player method.
     *
     * @param pos
     */
    protected void seekTo(int pos) {
        if (playerAtStates(MediaPlayerState.STARTED, MediaPlayerState.COMPLETE,
                MediaPlayerState.PREPARED, MediaPlayerState.PAUSED)) {
            mediaPlayer.seekTo(pos);
        }
    }

    /**
     * Safe start playback MediaPlayer method.
     */
    protected void start() {
        if (playerAtStates(MediaPlayerState.PREPARED, MediaPlayerState.STARTED,
                MediaPlayerState.PAUSED, MediaPlayerState.COMPLETE)) {
            mediaPlayer.start();
            setPlayerState(MediaPlayerState.STARTED);
        }
    }

    /**
     * Safe stop playback MediaPlayer method.
     */
    protected void stop() {
        if (playerAtStates(MediaPlayerState.STARTED, MediaPlayerState.COMPLETE,
                MediaPlayerState.STOPPED, MediaPlayerState.PREPARED, MediaPlayerState.PAUSED)) {
            mediaPlayer.stop();
            setPlayerState(MediaPlayerState.STOPPED);
        }
    }

    private String fetchStringParameter(Intent intent, String argName) {
        if (intent.hasExtra(argName)) {
            return intent.getStringExtra(argName);
        }
        return null;
    }

    private void initMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.reset();
    }

    /**
     * Checks if player is in listed states.
     *
     * @param states State array
     * @return true if state is currently supported, false if not.
     */
    private boolean playerAtStates(MediaPlayerState... states) {
        for (MediaPlayerState state : states) {
            if (state == playerState) {
                return true;
            }
        }
        return false;
    }
}
