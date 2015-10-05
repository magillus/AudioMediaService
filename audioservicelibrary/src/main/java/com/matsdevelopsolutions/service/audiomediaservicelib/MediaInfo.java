package com.matsdevelopsolutions.service.audiomediaservicelib;


import java.io.Serializable;

/**
 * Media Info class stores details about media stream.
 */
public class MediaInfo implements Serializable {
    /**
     * Title of the media stream.
     */
    public String title;
    /**
     * Description of the media stream.
     */
    public String description;
    /**
     * Icon/Art image uri.
     */
    public String artUri;
    /**
     * Media stream url.
     */
    public String streamUrl;

    MediaInfo() {
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private final MediaInfo mediaInfo;

        public Builder(MediaInfo mediaInfo) {
            this.mediaInfo = mediaInfo;
        }

        public Builder() {
            mediaInfo = new MediaInfo();
        }

        public MediaInfo build() {
            return mediaInfo;
        }

        public Builder setTitle(final String title) {
            mediaInfo.title = title;
            return this;
        }

        public Builder setDescription(final String description) {
            mediaInfo.description = description;
            return this;
        }

        public Builder setArtUri(final String artUri) {
            mediaInfo.artUri = artUri;
            return this;
        }

        public Builder setStreamUrl(final String streamUrl) {
            mediaInfo.streamUrl = streamUrl;
            return this;
        }
    }
}
