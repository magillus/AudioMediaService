package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Audio service that keep instance of MediaPlayer to play a audio stream.
 * Interaction with AudioMediaService happens through Intent calls.
 * Notification branding and action flags are also passed through Intent calls.
 */
public class AudioMediaService extends Service
        implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnPreparedListener {

    /**
     * Action's name prepended with this package name.
     */
    public static final String PACKAGE_NAME = "com.matsdevelopsolutions.service.audiomediaservicelib.";

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
    /**
     * Intent to play/pause toggle.
     */
    public static final String ACTION_PLAY_TOGGLE = PACKAGE_NAME + "AudioMediaService.PLAY_TOGGLE";
    /**
     * Media source url extras name.
     */
    static final String SOURCE_URL_ARG = "SOURCE_URL_ARG";
    /**
     * Media source title extras name.
     */
    static final String SOURCE_TITLE_ARG = "SOURCE_TITLE_ARG";
    /**
     * Media source short description extras name.
     */
    static final String SOURCE_DESC_ARG = "SOURCE_DESC_ARG";
    /**
     * Media source art/icon URI extras name.
     */
    static final String SOURCE_ART_URI_ARG = "SOURCE_ART_URI_ARG";
    /**
     * Media source seek position in seconds extras name.
     */
    static final String SEEK_POSITION_ARG = "SEEK_POSITION_ARG";
    /**
     * Media auto play extra name.
     */
    static final String AUTO_PLAY_ARG = "AUTO_PLAY_ARG";
    /**
     * Notification style extras name.
     */
    static final String NOTIFICATION_STYLE_ARG = "NOTIFICATION_STYLE_ARG";
    /**
     * Notification configuration flag extras name.
     */
    static final String NOTIFICATION_CONFIG_FLAG_ARG = "NOTIFICATION_CONFIG_FLAG_ARG";
    /**
     * Notification style normal - Notification style flag.
     */
    static final String FLAG_NOTIFICATION_STYLE_NORMAL = "FLAG_NOTIFICATION_STYLE_NORMAL";
    /**
     * Notification style compact - Notification style flag.
     */
    static final String FLAG_NOTIFICATION_STYLE_COMPACT = "FLAG_NOTIFICATION_STYLE_COMPACT";
    /**
     * Hide notification - Notification configuration flag.
     */
    static final int FLAG_NOTIFICATION_HIDE = 0;
    /**
     * Show notification - Notification configuration flag.
     */
    static final int FLAG_NOTIFICATION_SHOW = 0b1;
    /**
     * Show play/pause as toggle - Notification configuration flag. (todo: flag might be always on and not needed)
     */
    static final int FLAG_NOTIFICATION_SHOW_BUTTON_PLAY_TOGGLE = 0b10;
    /**
     * Show stop/close button - Notification configuration flag.
     */
    static final int FLAG_NOTIFICATION_SHOW_BUTTON_STOP = 0b100;
    /**
     * Use pallette colors based on art's image - Notification configuration flag.
     */
    static final int FLAG_NOTIFICATION_PALLETE_BACKGROUND = 0b1000;
    static final int DEFAULT_NOTIFICATION_FLAG = FLAG_NOTIFICATION_SHOW + FLAG_NOTIFICATION_SHOW_BUTTON_PLAY_TOGGLE + FLAG_NOTIFICATION_PALLETE_BACKGROUND;
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

    /**
     * Notification manager instance.
     */
    private NotificationManager notificationManager;
    /**
     * Intent updates Broadcaster
     */
    private IntentBroadcaster intentBroadcaster;

    /**
     * Currently playing media info.
     */
    private MediaInfo mediaInfo;

    /**
     * Flag if video autoplays.
     */
    private boolean autoplay;
    private float volume;
    private WifiManager.WifiLock wifiStreamLock;

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        mediaPlayer.setVolume(volume, volume);
    }

    /**
     * Sets state of the media player.
     *
     * @param state state of media player
     */
    protected void setPlayerState(MediaPlayerState state) {
        setPlayerState(state, true);
    }

    /**
     * Creates instance of {AudioMediaService}.
     */
    public AudioMediaService() {
        initMediaPlayer();
        // todo: initialize only when notification is enabled - it does by default.
        notificationManager = new NotificationManager(this);
        intentBroadcaster = new IntentBroadcaster(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Communication with service allowed only through Intents. Use MediaService to generate intents.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        switch (action) {
            case ACTION_PLAY:
                // parse arguments and optionals
                String newUrl = fetchStringParameter(intent, SOURCE_URL_ARG);
                updateMediaInfoFromIntent(intent);
                if (newUrl.equalsIgnoreCase(mediaInfo.streamUrl)) {
                    // same stream - just start
                    start();
                } else {
                    // enable autoplay and load
                    autoplay = fetchBooleanParameter(intent, AUTO_PLAY_ARG, true);
                    setDataSource(mediaInfo.streamUrl, true);
                    // check for notification details in intent
                    String style = fetchStringParameter(intent, NOTIFICATION_STYLE_ARG, notificationManager.getCurrentStyle());
                    int flag = fetchIntParameter(intent, NOTIFICATION_CONFIG_FLAG_ARG, notificationManager.getCurrentFlags());
                    notificationManager.updateStyle(style, flag, mediaInfo);
                }
                break;
            case ACTION_PLAY_TOGGLE:
                if (playerState == MediaPlayerState.STARTED) {
                    pause();
                } else {
                    start();
                }
                break;
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_STOP:
                stop();
                break;
            case ACTION_SEEK:
                int position = fetchIntParameter(intent, SEEK_POSITION_ARG, 0);
                seekTo(position);
                break;
            case ACTION_NOTIFICATION_STYLE:
                String style = fetchStringParameter(intent, NOTIFICATION_STYLE_ARG);
                int flag = fetchIntParameter(intent, NOTIFICATION_CONFIG_FLAG_ARG, DEFAULT_NOTIFICATION_FLAG);
                updateMediaInfoFromIntent(intent);
                notificationManager.updateStyle(style, flag, mediaInfo);
            default:
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
        intentBroadcaster.currentPosition(mp.getCurrentPosition());
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                setVolume(1.0f);
                if (!mediaPlayer.isPlaying()) {
                    start();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mediaPlayer.isPlaying()) {
                    stop();
                }
                release();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                setVolume(0.1f);
                break;
        }
    }

    /**
     * Callback from media player that data source is prepared.
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (playerAtStates(MediaPlayerState.PREPARING)) {
            setPlayerState(MediaPlayerState.PREPARED);
            if (autoplay) {
                start();
            }
        }
    }

    /**
     * Sets state of the media player.
     *
     * @param state     state of media player
     * @param broadcast if true, the state change is broadcasted with intent.
     */
    protected void setPlayerState(MediaPlayerState state, boolean broadcast) {
        this.playerState = state;
        if (broadcast) {
            intentBroadcaster.stateChange(state);
        }
    }

    /**
     * Safe reset of media Player instance.
     */
    protected void reset(final boolean force) {
        if (!playerAtStates(MediaPlayerState.END, MediaPlayerState.ERROR)) {
            mediaPlayer.reset();
            loseAudioFocus();
            releaseWifiLock();
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
    protected void setDataSource(final String url, final boolean force) {
        if (playerAtStates(MediaPlayerState.IDLE)) {
            try {
                mediaPlayer.setDataSource(this, Uri.parse(url));
            } catch (IOException e) {
                Log.w(TAG, String.format("Error setting data source, url = %s", url), e);
            }
        } else if (force) {
            reset(true);
            setDataSource(url, false);
            if (autoplay) {
                prepare();
            }
            setPlayerState(MediaPlayerState.INITIALIZED);
        }
    }

    /**
     * Safe release of media player instance.
     */
    protected void release() {
        mediaPlayer.release();
        loseAudioFocus();
        releaseWifiLock();
        setPlayerState(MediaPlayerState.END);
    }

    /**
     * Safe prepare method for media player.
     */
    protected void prepare() {
        if (playerAtStates(MediaPlayerState.INITIALIZED, MediaPlayerState.STOPPED)) {
            mediaPlayer.prepareAsync();
            setPlayerState(MediaPlayerState.PREPARING);
        }
    }

    /**
     * Safe seek to media player method.
     *
     * @param pos
     */
    protected void seekTo(final int pos) {
        if (playerAtStates(MediaPlayerState.STARTED, MediaPlayerState.COMPLETE,
                MediaPlayerState.PREPARED, MediaPlayerState.PAUSED)) {
            if (pos > 0) {
                mediaPlayer.seekTo(pos);
            } else {
                mediaPlayer.seekTo(0);
            }
        }
    }

    /**
     * Safe start playback MediaPlayer method.
     */
    protected void start() {
        if (playerAtStates(MediaPlayerState.PREPARED, MediaPlayerState.STARTED,
                MediaPlayerState.PAUSED, MediaPlayerState.COMPLETE)) {
            mediaPlayer.start();
            getAudioFocus();
            acquireWifiLock();
            setPlayerState(MediaPlayerState.STARTED);
        }
    }

    /**
     * Safe pause playback MediaPlayer method.
     */
    protected void pause() {
        if (playerAtStates(MediaPlayerState.STARTED)) {
            mediaPlayer.pause();
            loseAudioFocus();
            releaseWifiLock();
            setPlayerState(MediaPlayerState.PAUSED);
        }
    }

    /**
     * Safe stop playback MediaPlayer method.
     */
    protected void stop() {
        if (playerAtStates(MediaPlayerState.STARTED, MediaPlayerState.COMPLETE,
                MediaPlayerState.STOPPED, MediaPlayerState.PREPARED, MediaPlayerState.PAUSED)) {
            mediaPlayer.stop();
            loseAudioFocus();
            releaseWifiLock();
            setPlayerState(MediaPlayerState.STOPPED);
        }
    }

    private void acquireWifiLock() {
        if (!wifiStreamLock.isHeld()) {
            wifiStreamLock.acquire();
        }
    }

    private void releaseWifiLock() {
        if (wifiStreamLock != null && wifiStreamLock.isHeld()) {
            wifiStreamLock.release();
        }
    }

    private void loseAudioFocus() {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
    }

    private void getAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // could not get audio focus.
            Log.w(TAG, "Error requesting audio focus : " + result);
        }
    }

    private void updateMediaInfoFromIntent(Intent intent) {
        String title = fetchStringParameter(intent, SOURCE_TITLE_ARG);
        String description = fetchStringParameter(intent, SOURCE_DESC_ARG);
        String artUriString = fetchStringParameter(intent, SOURCE_ART_URI_ARG);
        String newUrl = fetchStringParameter(intent, SOURCE_URL_ARG);
        boolean changes = false;
        changes |= hasValueChanged(newUrl, mediaInfo.streamUrl);
        changes |= hasValueChanged(title, mediaInfo.title);
        changes |= hasValueChanged(description, mediaInfo.description);
        changes |= hasValueChanged(artUriString, mediaInfo.artUri);
        if (changes) {
            mediaInfo.streamUrl = newUrl;
            mediaInfo.title = title;
            mediaInfo.description = description;
            mediaInfo.artUri = artUriString;
            notificationManager.updateMediaInfo(mediaInfo);
            intentBroadcaster.mediaInfoChanged(mediaInfo);
        }

    }

    private boolean hasValueChanged(String valueA, String valueB) {
        if (valueA == null && valueB == null) {
            return false;
        } else if (valueA != null) {
            return !valueA.equals(valueB);
        }
        return true;
    }

    /**
     * Fetches integer argument from intent extras if exists.
     * Returns default if doesn't exists.
     *
     * @param intent
     * @param argName
     * @param defaultValue
     * @return
     */
    private int fetchIntParameter(final Intent intent, final String argName, final int defaultValue) {
        if (intent.hasExtra(argName)) {
            return intent.getIntExtra(argName, defaultValue);
        }
        return defaultValue;
    }

    /**
     * Fetches String argument from intent extras if exists.
     * Returns null if doesn't exists.
     *
     * @param intent
     * @param argName
     * @return
     */
    @Nullable
    private String fetchStringParameter(final Intent intent, final String argName) {
        return fetchStringParameter(intent, argName, null);
    }

    /**
     * Fetches String argument from intent extra if exists.
     * Returns {defaultValue} if doesn't exists
     *
     * @param intent
     * @param argName
     * @param defaultValue
     * @return
     */
    @Nullable
    private String fetchStringParameter(final Intent intent, final String argName, final String defaultValue) {
        if (intent.hasExtra(argName)) {
            return intent.getStringExtra(argName);
        }
        return defaultValue;
    }

    /**
     * Fetches boolean argument from intent extras if exists.
     * Returns default if doesn't exists.
     *
     * @param intent
     * @param argName
     * @param defaultValue
     * @return
     */
    private boolean fetchBooleanParameter(final Intent intent, final String argName, final boolean defaultValue) {
        if (intent.hasExtra(argName)) {
            return intent.getBooleanExtra(argName, defaultValue);
        }
        return defaultValue;
    }

    /**
     * Initialize media player, releases previous isntance if was created.s
     */
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
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.reset();
    }

    /**
     * Checks if player is in listed states.
     *
     * @param states State array
     * @return true if state is currently supported, false if not.
     */
    private boolean playerAtStates(final MediaPlayerState... states) {
        for (MediaPlayerState state : states) {
            if (state == playerState) {
                return true;
            }
        }
        return false;
    }
}
