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
}
