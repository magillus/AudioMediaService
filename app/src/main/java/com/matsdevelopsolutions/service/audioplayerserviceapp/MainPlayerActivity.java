package com.matsdevelopsolutions.service.audioplayerserviceapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.matsdevelopsolutions.service.audiomediaservicelib.IntentGenerator;
import com.matsdevelopsolutions.service.audiomediaservicelib.MediaInfo;
import com.matsdevelopsolutions.service.audiomediaservicelib.MediaPlayerState;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaBufferProgressBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaInfoBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaPositionBroadcastReceiver;
import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.PlayerStateBroadcastReceiver;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainPlayerActivity extends AppCompatActivity {


    @Bind(R.id.media_status)
    TextView mediaStatus;
    @Bind(R.id.stream_name)
    TextView streamName;
    @Bind(R.id.source_spinner)
    AppCompatSpinner sourceSpinner;
    private MediaInfoBroadcastReceiver mediaInfoBroadcastReceiver = new MediaInfoBroadcastReceiver() {
        @Override
        public void onMediaInfoChanged(@Nullable MediaInfo mediaInfo) {
            streamName.setText(mediaInfo.title);
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
            updateState(playerState);
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

    public void startPlayback(String path, String title) {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.artUri = "https://pmcdeadline2.files.wordpress.com/2014/08/bbc-logo.jpg?w=970";
        mediaInfo.streamUrl = path;
        mediaInfo.description = "BBC radio";
        mediaInfo.title = title;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        ButterKnife.bind(this);

        String[] cols = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.TITLE};
        final Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                cols,
                null, null, null
        );

        final SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.audio_item,
                c, new String[]{MediaStore.Audio.Media.DISPLAY_NAME}, new int[]{R.id.audio_item_title});

//
//        String[] files = new String[0];
//        try {
//            files = getResources().getAssets().list("audio");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        final List<Map<String, String>> data = new ArrayList<>();
//        for (String filePath : files) {
//            Map<String, String> map = new HashMap<>();
//            File file = new File("audio/" + filePath);
//            map.put("PATH", "audio/" + filePath);
//            map.put("NAME", file.getName());
//            data.add(map);
//        }
//
//        SpinnerAdapter adapter = new SimpleAdapter(this, data, R.layout.audio_item,
//                new String[]{"NAME"}, new int[]{R.id.audio_item_title});
        sourceSpinner.setAdapter(cursorAdapter);

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                c.moveToPosition(position);

                String url = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                startPlayback(url, title);
                //startPlayback(data.get(position).get("PATH"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                stop();
            }
        });
    }

    private void updateState(MediaPlayerState playerState) {
        mediaStatus.setText(playerState.toString());
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

    private void playStream(MediaInfo mediaInfo) {
        Intent playIntent = IntentGenerator.createPlayIntent(this, mediaInfo);
        startService(playIntent);
    }
}
