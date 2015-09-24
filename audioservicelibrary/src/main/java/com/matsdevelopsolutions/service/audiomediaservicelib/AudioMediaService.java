package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

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
     * {@link #SOURCE_URL_ARG} - Media Url - (optional if already media source is loaded) - plays a media stream
     * {@link #SOURCE_TITLE_ARG} - Media title - (optional if notification is not used)
     * Optional parameter:
     * {@link #SOURCE_DESC_ARG} - Media description
     * {@link #SOURCE_ART_URI_ARG} - Media art URI - local file or url
     * {@link #NOTIFICATION_STYLE_ARG} - Show notification flag -  - will show player notification - requires title, icon/art
     */
    public static final String ACTION_PLAY = PACKAGE_NAME + "AudioMediaService.PLAY";
    /**
     * Intent action to pause playback of media.
     * Note: On live stream it will stop playback.
     */
    public static final String ACTION_PAUSE = PACKAGE_NAME + "AudioMediaService.PAUSE";
    /**
     * Intent action to stop playback and release media resources - it will also hide notification if showing.
     */
    public static final String ACTION_STOP = PACKAGE_NAME + "AudioMediaService.STOP";
    /**
     * Intent action to seek non-live stream to a given position
     * Required Extras:
     * {@link #SEEK_POSITION_ARG} - position in seconds to media stream seek to
     */
    public static final String ACTION_SEEK = PACKAGE_NAME + "AudioMediaService.SEEK_TO";
    /**
     * Intent action to update style of notification bar.
     * Extras:
     * {@link #NOTIFICATION_STYLE_ARG} - style type for notification
     * todo- color of the background
     * {@link #NOTIFICATION_CONFIG_FLAG_ARG} - flags to enable/disable features on notification
     */
    public static final String ACTION_NOTIFICATION_STYLE = PACKAGE_NAME + "AudioMediaService.NOTIFICATION_STYLE";
    /**
     * Intent action to play/pause toggle.
     */
    public static final String ACTION_PLAY_TOGGLE = PACKAGE_NAME + "AudioMediaService.PLAY_TOGGLE";
    /**
     * Intent action to mute toggle.
     */
    public static final String ACTION_MUTE_TOGGLE = PACKAGE_NAME + "AudioMediaService.MUTE_TOGGLE";
    /**
     * Intent action name to change volume.
     */
    public static final String ACTION_CHANGE_VOLUME = PACKAGE_NAME + "AudioMediaService.CHANGE_VOLUME";
    /**
     * Media player volume argument.
     */
    public static final String VOLUME_VALUE_ARG = "VOLUME_VALUE_ARG";
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
     * Flag to mark to resume play from last location.
     */
    static final String RESUME_PLAY_ARG = "RESUME_PLAY_ARG";
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
     * Use palette colors based on art's image - Notification configuration flag.
     */
    static final int FLAG_NOTIFICATION_PALETTE_BACKGROUND = 0b1000;
    /**
     * Default flags for notifications
     */
    static final int DEFAULT_NOTIFICATION_FLAG = FLAG_NOTIFICATION_SHOW + FLAG_NOTIFICATION_SHOW_BUTTON_PLAY_TOGGLE + FLAG_NOTIFICATION_PALETTE_BACKGROUND;
    /**
     * Logging tag.
     */
    private static final String TAG = AudioMediaService.class.getSimpleName();

    /**
     * Default position update timespan.
     */
    private static final int POSITION_UPDATE_TIMESPAN_DEFAULT = 500; //ms
    private static final float VOLUME_MUTED = 0f;
    private static final long STOP_DELAY_TIMER = 60 * 1000;
    /**
     * Media player instance.
     */
    private MediaPlayer mediaPlayer;
    /**
     * Media player state
     */
    private MediaPlayerState playerState;
    private final Runnable stopService = new Runnable() {
        @Override
        public void run() {
            if (playerNotAtStates(MediaPlayerState.PAUSED, MediaPlayerState.STARTED)) {
                Log.d(TAG, "After stop timeout closing AudioMediaService.");
                stopSelf();
            }
        }
    };
    /**
     * Notification manager instance.
     */
    private NotificationHelper notificationManager;
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
    private Handler updatesHandler;
    private volatile boolean isPositionUpdateActive = false;
    private float previousVolume = 0f;
    private MediaProgressPreferences mediaProgressPreferences;
    private final Runnable positionUpdate = new Runnable() {
        @Override
        public void run() {
            if (intentBroadcaster != null && mediaPlayer != null
                    && playerAtStates(MediaPlayerState.PAUSED, MediaPlayerState.STARTED, MediaPlayerState.STOPPED)) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                Log.v(TAG, String.format("Media position change : %s", currentPosition));
                if (mediaInfo != null) {
                    mediaProgressPreferences.putProgress(mediaInfo.streamUrl, currentPosition);
                }
                intentBroadcaster.currentPosition(currentPosition);
                if (isPositionUpdateActive) {
                    updatePositionBroadcast();
                }
            }
        }
    };
    private int startPlaybackPosition = 0;

    /**
     * Creates instance of {AudioMediaService}.
     */
    public AudioMediaService() {
    }

    /**
     * Gets volume value
     *
     * @return
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Sets audio volume
     *
     * @param volume
     */
    public void setVolume(float volume) {
        try {
            if (volume == this.volume) {
                return;
            }
            this.previousVolume = this.volume;
            Log.v(TAG, String.format("Volume change from %f to %f", this.previousVolume, volume));
            this.volume = volume;
            mediaPlayer.setVolume(volume, volume);
        } finally {
            intentBroadcaster.volume(getVolume());
        }
    }

    /**
     * Sets state of the media player.
     *
     * @param state state of media player
     */
    protected synchronized void setPlayerState(MediaPlayerState state) {
        setPlayerState(state, true);
    }

    /**
     * Prepares value for wifi stream lock.s
     */
    @Override
    public void onCreate() {
        super.onCreate();

        initMediaPlayer();
        // todo: initialize only when notification is enabled - it does by default.
        notificationManager = new NotificationHelper(this);
        intentBroadcaster = new IntentBroadcaster(this);
        mediaProgressPreferences = new MediaProgressPreferences(this);

        updatesHandler = new Handler();
        wifiStreamLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "Audio Stream Lock");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "Closing Audio Media Service.");
        release();
        notificationManager.clear();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Communication with service allowed only through Intents. Use MediaService to generate intents.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String action = intent.getAction();
        if (action == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        try {
            Log.d(TAG, String.format("onStartCommand, action = %s", action));
            switch (action) {
                case ACTION_PLAY:
                    // parse arguments and optionals
                    String newUrl = fetchStringParameter(intent, SOURCE_URL_ARG);
                    boolean loadAndPlay = !(mediaInfo == null || !newUrl.equalsIgnoreCase(mediaInfo.streamUrl));
                    if (fetchBooleanParameter(intent, RESUME_PLAY_ARG, false)) {
                        startPlaybackPosition = mediaProgressPreferences.getProgress(newUrl);
                    }
                    updateMediaInfoFromIntent(intent);
                    if (newUrl != null) {
                        if (loadAndPlay) {
                            // same stream - just start
                            start();// force start or load data again
                        } else {
                            // enable autoplay and load
                            autoplay = fetchBooleanParameter(intent, AUTO_PLAY_ARG, true);
                            setDataSource(mediaInfo.streamUrl, true);
                            prepare();
                            // check for notification details in intent
                            String style = fetchStringParameter(intent, NOTIFICATION_STYLE_ARG, notificationManager.getCurrentStyle());
                            int flag = fetchIntParameter(intent, NOTIFICATION_CONFIG_FLAG_ARG, notificationManager.getCurrentFlags());
                            notificationManager.updateStyle(style, flag, mediaInfo);
                        }
                    } else {
                        Log.w(TAG, "playback url is empty");
                        if (mediaInfo != null && mediaInfo.streamUrl != null) {
                            start();
                        }
                    }
                    break;
                case ACTION_PLAY_TOGGLE:
                    if (playerState == MediaPlayerState.STARTED) {
                        pause();
                    } else if (playerState == MediaPlayerState.STOPPED) {
                        autoplay = true;
                        prepare();
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
                    break;
                case ACTION_MUTE_TOGGLE:
                    toggleVolume();
                    break;
                case ACTION_CHANGE_VOLUME:
                    float newVolume = fetchFloatParameter(intent, VOLUME_VALUE_ARG, getVolume());
                    setVolume(newVolume);
                default:
                    return START_CONTINUATION_MASK;

            }
        } catch (Exception ex) {
            Log.e(TAG, String.format("Error processing command from intent action: %s ", action), ex);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setPlayerState(MediaPlayerState.COMPLETE);
        delayStop();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // report error
        // broadcast error from player
        setPlayerState(MediaPlayerState.ERROR);
        releaseWifiLock();
        loseAudioFocus();
        stopPositionUpdateBroadcast();
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
        int currentPosition = mp.getCurrentPosition();
        mediaProgressPreferences.putProgress(mediaInfo.streamUrl, currentPosition);
        intentBroadcaster.currentPosition(currentPosition);
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
                seekTo(startPlaybackPosition);
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
        Log.d(TAG, String.format("Player state change from %s to %s", this.playerState.toString(), state.toString()));
        this.playerState = state;
        if (broadcast) {
            intentBroadcaster.stateChange(state);
        }
        notificationManager.updateNotification(state);
    }

    /**
     * Safe reset of media Player instance.
     */
    protected void reset(final boolean force) {
        if (playerNotAtStates(MediaPlayerState.END, MediaPlayerState.ERROR)) {
            Log.v(TAG, String.format("Reset player (forced = %s", String.valueOf(force)));
            mediaPlayer.reset();
            loseAudioFocus();
            releaseWifiLock();
            stopPositionUpdateBroadcast();
        } else if (force) {
            release();
            initMediaPlayer();
        }
        delayStop();
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
                Log.d(TAG, String.format("Sets data source url = %s", url));
                mediaPlayer.setDataSource(this, Uri.fromFile(new File(url)));
                setPlayerState(MediaPlayerState.INITIALIZED);
            } catch (Exception e) {
                Log.w(TAG, String.format("Error setting data source, url = %s", url), e);
                setPlayerState(MediaPlayerState.ERROR);
            }
        } else if (force) {
            reset(true);
            setDataSource(url, false);
            if (autoplay) {
                prepare();
            }
        }
    }

    /**
     * Safe release of media player instance.
     */
    protected void release() {
        Log.v(TAG, "Release player");
        mediaPlayer.release();
        loseAudioFocus();
        releaseWifiLock();
        setPlayerState(MediaPlayerState.END);
        stopPositionUpdateBroadcast();
        stopSelf();
    }

    /**
     * Safe prepare method for media player.
     */
    protected void prepare() {
        if (playerAtStates(MediaPlayerState.INITIALIZED, MediaPlayerState.STOPPED)) {
            Log.v(TAG, "Prepares player");
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
            startPlaybackPosition = 0;
            mediaPlayer.start();
            autoplay = false;
            getAudioFocus();
            acquireWifiLock();
            setPlayerState(MediaPlayerState.STARTED);
            updatePositionBroadcast();
        } else {
            // force
            reset(true);
            setDataSource(mediaInfo.streamUrl, true);
        }
    }

    /**
     * Safe pause playback MediaPlayer method.
     */
    protected void pause() {
        if (playerAtStates(MediaPlayerState.STARTED)) {
            if (mediaInfo != null) {
                mediaProgressPreferences.putProgress(mediaInfo.streamUrl, mediaPlayer.getCurrentPosition());
            }
            mediaPlayer.pause();
            loseAudioFocus();
            releaseWifiLock();
            setPlayerState(MediaPlayerState.PAUSED);
            stopPositionUpdateBroadcast();
        }
    }

    /**
     * Safe stop playback MediaPlayer method.
     */
    protected void stop() {
        if (playerAtStates(MediaPlayerState.STARTED, MediaPlayerState.COMPLETE,
                MediaPlayerState.STOPPED, MediaPlayerState.PREPARED, MediaPlayerState.PAUSED)) {
            if (mediaInfo != null) {
                mediaProgressPreferences.putProgress(mediaInfo.streamUrl, mediaPlayer.getCurrentPosition());
            }
            mediaPlayer.stop();
            loseAudioFocus();
            releaseWifiLock();
            setPlayerState(MediaPlayerState.STOPPED);
            stopPositionUpdateBroadcast();
            delayStop();
        }
    }

    private void delayStop() {
        Log.d(TAG, "Delayed stop service called");
        // 1min timeout to self close
        updatesHandler.postDelayed(stopService, STOP_DELAY_TIMER);
    }

    private void toggleVolume() {
        if (previousVolume != VOLUME_MUTED) {
            setVolume(VOLUME_MUTED);
        } else {
            setVolume(previousVolume);
        }
    }

    private void stopPositionUpdateBroadcast() {
        isPositionUpdateActive = false;
    }

    private void updatePositionBroadcast() {
        int positionUpdateTimespan = POSITION_UPDATE_TIMESPAN_DEFAULT;
        updatesHandler.postDelayed(positionUpdate, positionUpdateTimespan);
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

    /**
     * Updates media information if changed on notification.
     *
     * @param intent
     */
    private void updateMediaInfoFromIntent(Intent intent) {
        String title = fetchStringParameter(intent, SOURCE_TITLE_ARG);
        String description = fetchStringParameter(intent, SOURCE_DESC_ARG);
        String artUriString = fetchStringParameter(intent, SOURCE_ART_URI_ARG);
        String newUrl = fetchStringParameter(intent, SOURCE_URL_ARG);

        boolean changes = false;
        if (mediaInfo == null) {
            mediaInfo = new MediaInfo();
            changes = true;
        } else {
            hasValueChanged(newUrl, mediaInfo.streamUrl);
            changes |= hasValueChanged(title, mediaInfo.title);
            changes |= hasValueChanged(description, mediaInfo.description);
            changes |= hasValueChanged(artUriString, mediaInfo.artUri);
        }
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
     * Fetches float argument from intent extras if exists.
     *
     * @param intent       intent
     * @param argName      argument name
     * @param defaultValue default Value
     * @return returns value or default if doesn't exists
     */
    private float fetchFloatParameter(Intent intent, String argName, float defaultValue) {
        if (intent.hasExtra(argName)) {
            return intent.getFloatExtra(argName, defaultValue);
        }
        return defaultValue;
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
    private synchronized void initMediaPlayer() {
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
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        playerState = MediaPlayerState.IDLE;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

    }

    /**
     * Checks if player is in listed states.
     *
     * @param states State array
     * @return true if state is currently supported, false if not.
     */
    private boolean playerAtStates(final MediaPlayerState... states) {
        StringBuilder expectedState = new StringBuilder("allowed states: [");
        for (MediaPlayerState state : states) {
            expectedState.append(state.toString());
            expectedState.append(", ");
            if (state == playerState) {
                expectedState.append("]");
                Log.v(TAG, String.format("Matched expected state %s -> %s", playerState.toString(), expectedState.toString()));
                return true;
            }
        }
        expectedState.append("]");
        Log.i(TAG, String.format("Player is not in expected state. state = %s, %s", playerState.toString(), expectedState.toString()));
        return false;
    }

    private boolean playerNotAtStates(final MediaPlayerState... states) {
        StringBuilder notExpectedState = new StringBuilder("not allowed states: [");
        boolean gotNotExpectedState = false;
        for (MediaPlayerState state : states) {
            notExpectedState.append(state.toString());
            notExpectedState.append(", ");
            if (state != playerState) {
                notExpectedState.append("]");
                Log.v(TAG, String.format("Matched not expected state %s -> %s", playerState.toString(), notExpectedState.toString()));
                gotNotExpectedState = true;
            }
        }
        notExpectedState.append("]");
        if (!gotNotExpectedState) {
            Log.i(TAG, String.format("Player is in expected state. state = %s, %s", playerState.toString(), notExpectedState.toString()));
        }
        return !gotNotExpectedState;
    }

}
