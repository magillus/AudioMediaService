package com.perlak.mateusz.service.audiomediaservicelib;

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

    private static final String PACKAGE_NAME = "com.perlak.mateusz.service.audiomediaservicelib.";
    public static final String ACTION_PLAY = PACKAGE_NAME + "AudioMediaService.PLAY";

    private static final String SOURCE_URL_ARG = "SOURCE_URL_ARG";

    private static final String TAG = AudioMediaService.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private MediaPlayerState playerState;
    private String mediaStreamUrl;

    public AudioMediaService() {
        initMediaPlayer();
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

    protected void setPlayerState(MediaPlayerState state) {
        this.playerState = state;
        // broadcast state change Intent
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
}
