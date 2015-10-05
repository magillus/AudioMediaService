package com.matsdevelopsolutions.service.audiomediaservicelib;

/**
 * Media playback request for audio service
 */
public class PlayRequest {

    private MediaInfo mediaInfo;
    private boolean autoPlay;
    private long startPlaybackPosition;
    private NotificationConfiguration notificationConfiguration;

    public static class Builder {
        private final PlayRequest request;

        public Builder() {
            request = new PlayRequest();
        }

        public PlayRequest build() {
            return request;
        }

        public Builder setMediaInfo(final MediaInfo.Builder mediaInfoBuilder) {
            request.mediaInfo = mediaInfoBuilder.build();
            return this;
        }

        public Builder setAutoPlay(final boolean autoPlay) {
            request.autoPlay = autoPlay;
            return this;
        }

        public Builder setStartPlaybackPosition(final long position) {
            request.startPlaybackPosition = position;
            return this;
        }

        public Builder setNotificationConfiguration(NotificationConfiguration configuration) {
            request.notificationConfiguration = configuration;
            return this;
        }
    }
}
