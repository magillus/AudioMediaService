package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.app.Application;
import android.support.annotation.Nullable;
import android.test.ApplicationTestCase;

import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaInfoBroadcastReceiver;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AudioMediaServiceTest extends ApplicationTestCase<Application> {
    public AudioMediaServiceTest() {
        super(Application.class);
    }


    public void testnullMediaInfoBroadcastTest() {

        MediaInfoBroadcastReceiver.register(getContext(), new MediaInfoBroadcastReceiver() {
            @Override
            public void onMediaInfoChanged(@Nullable MediaInfo mediaInfo) {
                assertEquals(mediaInfo, null);
            }
        });
        IntentBroadcaster intentBroadcaster = new IntentBroadcaster(getContext());
        intentBroadcaster.mediaInfoChanged(null);

    }

    public void testmediaInfoBroadcastTest() {
        final MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.title = "Test title";
        mediaInfo.description = "Test description for media Item";
        mediaInfo.streamUrl = "http://test.stream.url";
        mediaInfo.artUri = "http://test.audio.art.url";

        MediaInfoBroadcastReceiver.register(getContext(), new MediaInfoBroadcastReceiver() {
            @Override
            public void onMediaInfoChanged(@Nullable MediaInfo broadcastMediaInfo) {
                assertEquals(broadcastMediaInfo.title, mediaInfo.title);
                assertEquals(broadcastMediaInfo.description, mediaInfo.description);
                assertEquals(broadcastMediaInfo.streamUrl, mediaInfo.streamUrl);
                assertEquals(broadcastMediaInfo.artUri, mediaInfo.artUri);
            }
        });
        IntentBroadcaster intentBroadcaster = new IntentBroadcaster(getContext());
        intentBroadcaster.mediaInfoChanged(mediaInfo);


    }


}