package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.LargeTest;

import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaBufferProgressBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaInfoBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaPositionBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.PlayerStateBroadcastReceiver;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AudioMediaServiceTest extends AndroidTestCase {

    MockContext context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        context = new MockContext();
    }

    @LargeTest
    public void testnullMediaInfoBroadcastTestCase() {
        MediaInfoBroadcastReceiver mediaInfoBroadcastReceiver = new MediaInfoBroadcastReceiver() {
            @Override
            public void onMediaInfoChanged(@Nullable MediaInfo mediaInfo) {
                assertEquals(mediaInfo, null);
            }
        };
        mediaInfoBroadcastReceiver.onReceive(context, new Intent(MediaInfoBroadcastReceiver.getIntentFilter().getAction(0)));
    }

    @LargeTest
    public void testmediaInfoBroadcastTestCase() {
        final MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.title = "Test title";
        mediaInfo.description = "Test description for media Item";
        mediaInfo.streamUrl = "http://test.stream.url";
        mediaInfo.artUri = "http://test.audio.art.url";
        MediaInfoBroadcastReceiver mediaInfoBroadcastReceiver = new MediaInfoBroadcastReceiver() {
            @Override
            public void onMediaInfoChanged(@Nullable MediaInfo receivedMediaInfo) {
                assertEquals(receivedMediaInfo.title, mediaInfo.title);
                assertEquals(receivedMediaInfo.description, mediaInfo.description);
                assertEquals(receivedMediaInfo.streamUrl, mediaInfo.streamUrl);
                assertEquals(receivedMediaInfo.artUri, mediaInfo.artUri);

            }
        };
        Intent intent = new Intent(MediaInfoBroadcastReceiver.getIntentFilter().getAction(0));
        intent.putExtra(IntentBroadcaster.MEDIA_INFO_ARG, mediaInfo);
        mediaInfoBroadcastReceiver.onReceive(context, intent);
    }

    @LargeTest
    public void testMediaBufferProgressBroadcastTestCase() {
        MediaBufferProgressBroadcastReceiver receiver = new MediaBufferProgressBroadcastReceiver() {
            @Override
            public void onBufferProgressChanged(@IntRange(from = 0, to = 100) int progress) {
                assertEquals(progress, 30);
            }
        };
        Intent intent30 = new Intent(MediaInfoBroadcastReceiver.getIntentFilter().getAction(0));
        intent30.putExtra(IntentBroadcaster.BUFFER_PROGRESS_ARG, 30);

        receiver.onReceive(context, intent30);
    }

    @LargeTest
    public void testMediaPositionBroacastTestCase() {
        MediaPositionBroadcastReceiver receiver = new MediaPositionBroadcastReceiver() {
            @Override
            public void onCurrentPositionChanged(int currentPosition) {
                assertEquals(currentPosition, 482);
            }
        };
        Intent intent482 = new Intent(MediaPositionBroadcastReceiver.getIntentFilter().getAction(0));
        intent482.putExtra(IntentBroadcaster.CURRENT_POSITION_ARG, 482);
        receiver.onReceive(context, intent482);
    }

    @LargeTest
    public void testPlayerStateBroadacstTestCase() {
        PlayerStateBroadcastReceiver receiver = new PlayerStateBroadcastReceiver() {
            @Override
            protected void onPlayerStateChanged(MediaPlayerState playerState) {
                assertEquals(MediaPlayerState.PREPARING, playerState);
            }
        };
        Intent intent = new Intent(PlayerStateBroadcastReceiver.getIntentFilter().getAction(0));
        MediaPlayerState state = MediaPlayerState.PREPARING;
        intent.putExtra(IntentBroadcaster.MEDIA_STATUS_ARG, state);
        receiver.onReceive(context, intent);
    }
}