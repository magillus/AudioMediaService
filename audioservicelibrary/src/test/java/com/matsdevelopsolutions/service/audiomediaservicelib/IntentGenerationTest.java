package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class IntentGenerationTest {

    Context context;

    @Before
    public void startup() {
        context = RuntimeEnvironment.application;
    }

    @Test
    public void generatePlayIntentTest() throws Exception {
        Intent playIntent = IntentGenerator.createPlayIntent(context);
        assertEquals(playIntent.getComponent().getPackageName(), context.getPackageName());
        assertEquals(playIntent.getAction(), AudioMediaService.ACTION_PLAY);

    }

    @Test
    public void generatePlayIntentFromMediaInfoTest() {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.streamUrl = "http://stream.com/test";
        mediaInfo.artUri = "http://testUrlart";
        mediaInfo.description = "Test stream for UI Testing";
        mediaInfo.title = "Test 1";
        Intent playIntent = IntentGenerator.createPlayIntent(context, mediaInfo, false);
        assertEquals(playIntent.getComponent().getPackageName(), context.getPackageName());

        assertEquals(playIntent.getAction(), AudioMediaService.ACTION_PLAY);

        assertEquals(playIntent.getStringExtra(AudioMediaService.SOURCE_TITLE_ARG), mediaInfo.title);
        assertEquals(playIntent.getStringExtra(AudioMediaService.SOURCE_URL_ARG), mediaInfo.streamUrl);
        assertEquals(playIntent.getStringExtra(AudioMediaService.SOURCE_ART_URI_ARG), mediaInfo.artUri);
        assertEquals(playIntent.getStringExtra(AudioMediaService.SOURCE_DESC_ARG), mediaInfo.description);
    }

    @Test
    public void generateMuteToggleIntentTest() {
        Intent intent = IntentGenerator.createToggleMuteIntent(context);
        assertEquals(intent.getComponent().getPackageName(), context.getPackageName());
        assertEquals(intent.getAction(), AudioMediaService.ACTION_MUTE_TOGGLE);
    }

    @Test
    public void generatePlayToggleIntentTest() {
        Intent intent = IntentGenerator.createPlayToggleIntent(context);
        assertEquals(intent.getComponent().getPackageName(), context.getPackageName());
        assertEquals(intent.getAction(), AudioMediaService.ACTION_PLAY_TOGGLE);
    }

    @Test
    public void generateNotificationUpdateIntent() {
        Intent intent = IntentGenerator.createNotificationUpdateIntent(context,
                AudioMediaService.FLAG_NOTIFICATION_STYLE_COMPACT,
                AudioMediaService.FLAG_NOTIFICATION_PALETTE_BACKGROUND | AudioMediaService.FLAG_NOTIFICATION_SHOW | AudioMediaService.FLAG_NOTIFICATION_SHOW_BUTTON_STOP);

        assertEquals(intent.getComponent().getPackageName(), context.getPackageName());
        assertEquals(intent.getAction(), AudioMediaService.ACTION_NOTIFICATION_STYLE);
        assertEquals(intent.getStringExtra(AudioMediaService.NOTIFICATION_STYLE_ARG), AudioMediaService.FLAG_NOTIFICATION_STYLE_COMPACT);
        int flagValue = intent.getIntExtra(AudioMediaService.NOTIFICATION_CONFIG_FLAG_ARG, 0);
        assertEquals(flagValue, AudioMediaService.FLAG_NOTIFICATION_PALETTE_BACKGROUND | AudioMediaService.FLAG_NOTIFICATION_SHOW | AudioMediaService.FLAG_NOTIFICATION_SHOW_BUTTON_STOP);
    }

    @Test
    public void generateSeekIntent() {
        Intent intent = IntentGenerator.createSeekIntent(context, 30);
        assertEquals(intent.getComponent().getPackageName(), context.getPackageName());
        assertEquals(intent.getAction(), AudioMediaService.ACTION_SEEK);
        assertEquals(intent.getIntExtra(AudioMediaService.SEEK_POSITION_ARG, 0), 30);
    }

    @Test
    public void generateSeekByIntent() {
        Intent intent = IntentGenerator.createSeekByIntent(context, -15);
        assertEquals(intent.getComponent().getPackageName(), context.getPackageName());
        assertEquals(intent.getAction(), AudioMediaService.ACTION_SEEK_BY);
        assertEquals(intent.getIntExtra(AudioMediaService.SEEK_POSITION_DELTA_ARG, 0), -15);
    }

    @Test
    public void generateChangeVolumeIntent() {
        Intent intent = IntentGenerator.createChangeVolumeIntent(context, 0.6f);
        assertEquals(intent.getComponent().getPackageName(), context.getPackageName());
        assertEquals(intent.getAction(), AudioMediaService.ACTION_CHANGE_VOLUME);
        assertEquals(intent.getFloatExtra(AudioMediaService.VOLUME_VALUE_ARG, 0.1f), 0.6f, 0.5f);
    }

}