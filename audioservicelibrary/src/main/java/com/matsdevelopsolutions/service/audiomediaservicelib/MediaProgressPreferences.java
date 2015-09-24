package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Class that stores and fetches media positions per url in SharedPreferences
 */
public class MediaProgressPreferences {

    private static final String SHARE_PREF_NAME = "MediaProgressPreferences";
    private static final String URL_KEY_PRE = "URL_KEY_";
    private final SharedPreferences sharedPreferences;

    /**
     * Creates instance of {@link MediaProgressPreferences}.
     *
     * @param context context
     */
    public MediaProgressPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Store playback progress to shared preferences
     *
     * @param url      playback url
     * @param position current progress in miliseconds
     */
    public void putProgress(String url, int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getKey(url), position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    /**
     * Gets playback progress stored for the url, 0 if none.
     *
     * @param url playback url
     * @return progress status in miliseconds
     */
    public int getProgress(String url) {
        return sharedPreferences.getInt(getKey(url), 0);
    }

    /**
     * Gets key of the shared preference
     *
     * @param url url to generate key for
     * @return key value
     */
    private String getKey(String url) {
        return URL_KEY_PRE + String.valueOf(url.hashCode());
    }
}
