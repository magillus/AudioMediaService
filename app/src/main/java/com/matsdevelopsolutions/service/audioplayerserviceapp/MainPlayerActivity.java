package com.matsdevelopsolutions.service.audioplayerserviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.matsdevelopsolutions.service.audiomediaservicelib.IntentGenerator;
import com.matsdevelopsolutions.service.audiomediaservicelib.MediaInfo;
import com.matsdevelopsolutions.service.audiomediaservicelib.MediaPlayerState;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaBufferProgressBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaInfoBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaPositionBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.PlayerStateBroadcastReceiver;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainPlayerActivity extends AppCompatActivity {


    private MediaInfoBroadcastReceiver mediaInfoBroadcastReceiver = new MediaInfoBroadcastReceiver() {
        @Override
        public void onMediaInfoChanged(@Nullable MediaInfo mediaInfo) {

        }
    };
    private MediaPositionBroadcastReceiver mediaPositionBroadcastReceiver = new MediaPositionBroadcastReceiver() {
        @Override
        public void onCurrentPositionChanged(int currentPosition) {

        }
    };
    private PlayerStateBroadcastReceiver playerStateBroadcastReceiver = new PlayerStateBroadcastReceiver() {
        @Override
        protected void onPlayerStateChanged(MediaPlayerState playerState) {

        }
    };
    private MediaBufferProgressBroadcastReceiver mediaBufferProgressBroadcastReceiver = new MediaBufferProgressBroadcastReceiver() {
        @Override
        public void onBufferProgressChanged(@IntRange(from = 0, to = 100) int progress) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerServiceBroadcasters();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterServiceBroadcasters();
    }

    private void unregisterServiceBroadcasters() {
        unregisterReceiver(mediaInfoBroadcastReceiver);
        unregisterReceiver(mediaPositionBroadcastReceiver);
        unregisterReceiver(mediaBufferProgressBroadcastReceiver);
        unregisterReceiver(playerStateBroadcastReceiver);
    }

    /**
     * Initialize service broadcasters.
     */
    private void registerServiceBroadcasters() {
        MediaBufferProgressBroadcastReceiver.register(this, mediaBufferProgressBroadcastReceiver);
        PlayerStateBroadcastReceiver.register(this, playerStateBroadcastReceiver);
        MediaPositionBroadcastReceiver.register(this, mediaPositionBroadcastReceiver);
        MediaInfoBroadcastReceiver.register(this, mediaInfoBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        ButterKnife.bind(this);
    }

    private void playStream(MediaInfo mediaInfo) {
        Intent playIntent = IntentGenerator.createPlayIntent(this, mediaInfo);
        startService(playIntent);
    }

    @OnClick(R.id.button_start)
    public void startPlayback() {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.artUri = "https://pmcdeadline2.files.wordpress.com/2014/08/bbc-logo.jpg?w=970";
        mediaInfo.streamUrl = "http://vprbbc.streamguys.net/vprbbc24.mp3?1";
        mediaInfo.description = "BBC radio";
        mediaInfo.title = "BBC radio live";
        playStream(mediaInfo);
    }

    @OnClick(R.id.button_stop)
    public void stop() {
        startService(IntentGenerator.createStopIntent(this));
    }

    @OnClick(R.id.button_pause)
    public void pause() {
        startService(IntentGenerator.createPauseIntent(this));
    }

    @OnClick(R.id.button_mute)
    public void toggleMute() {
        startService(IntentGenerator.createToggleMuteIntent(this));
    }

    @OnClick(R.id.button_play)
    public void togglePlay() {
        startService(IntentGenerator.createPlayToggleIntent(this));
    }
}
