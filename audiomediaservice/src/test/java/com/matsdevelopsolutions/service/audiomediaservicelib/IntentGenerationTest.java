package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class IntentGenerationTest {

    @Test
    public void generatePlayIntentTest() throws Exception {
        Intent playIntent = IntentGenerator.createPlayIntent();
        assertEquals(playIntent.getAction(), AudioMediaService.ACTION_PLAY);

    }

    @Test
    public void generatePlayIntentFromMediaInfoTest() {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.streamUrl = "http://stream.com/test";
        mediaInfo.artUri = "http://testUrlart";
        mediaInfo.description = "Test stream for UI Testing";
        mediaInfo.title = "Test 1";
        Intent playIntent = IntentGenerator.createPlayIntent(mediaInfo);
        assertEquals(playIntent.getAction(), AudioMediaService.ACTION_PLAY);

        assertEquals(playIntent.getStringExtra(AudioMediaService.SOURCE_TITLE_ARG), mediaInfo.title);
        assertEquals(playIntent.getStringExtra(AudioMediaService.SOURCE_URL_ARG), mediaInfo.streamUrl);
        assertEquals(playIntent.getStringExtra(AudioMediaService.SOURCE_ART_URI_ARG), mediaInfo.artUri);
        assertEquals(playIntent.getStringExtra(AudioMediaService.SOURCE_DESC_ARG), mediaInfo.description);
    }

    @Test
    public void generateMuteToggleIntentTest() {
        Intent intent = IntentGenerator.createToggleMuteIntent();
        assertEquals(intent.getAction(), AudioMediaService.ACTION_MUTE_TOGGLE);
    }

    @Test
    public void generatePlayToggleIntentTest() {
        Intent intent = IntentGenerator.createPlayToggleIntent();
        assertEquals(intent.getAction(), AudioMediaService.ACTION_PLAY_TOGGLE);
    }

    @Before
    private void before() {

    }


}