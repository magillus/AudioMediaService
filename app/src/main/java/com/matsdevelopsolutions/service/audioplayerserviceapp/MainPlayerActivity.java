package com.matsdevelopsolutions.service.audioplayerserviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.matsdevelopsolutions.service.audiomediaservicelib.IntentGenerator;
import com.matsdevelopsolutions.service.audiomediaservicelib.MediaInfo;

public class MainPlayerActivity extends AppCompatActivity {

    // add butterknife

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
    }

    private void playStream(MediaInfo mediaInfo) {
        Intent playIntent = IntentGenerator.createPlayIntent(mediaInfo);
        startService(playIntent);
    }

    private void stop() {
        startService(IntentGenerator.createStopIntent());
    }

    private void pause() {
        startService(IntentGenerator.createPauseIntent());
    }

    private void toggleMute() {
        startService(IntentGenerator.createToggleMuteIntent());
    }

    private void togglePlay() {
        startService(IntentGenerator.createPlayToggleIntent());
    }
}
