package com.matsdevelopsolutions.service.audiomediaservicelib;

/**
 * Enumeration represents Media Player state.
 */
public enum MediaPlayerState {
    /**
     * Idle state, ready for media URL.
     */
    IDLE,
    /**
     * Initialized media, ready to prepare.
     */
    INITIALIZED,
    /**
     * Media is prepared, ready to play or seek
     */
    PREPARED,
    /**
     * Media player is playing.
     */
    STARTED,
    /**
     * Media player is paused.
     */
    PAUSED,
    /**
     * Media player is stopped, can be prepared for playback again.
     */
    STOPPED,
    /**
     * Media player is preparing, not used in AudioMediaService, no async prepare call.
     */
    PREPARING,
    /**
     * Media player completed playback, can be restart or reset.
     */
    COMPLETE,
    /**
     * Media player error.
     */
    ERROR,
    /**
     * Media player is released and cannot be used again.
     */
    END
}
