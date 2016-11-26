package com.example.irina.mp3;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;



public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private ToggleButton playPause;
    private SeekBar seekBar;
    private TextView infoTextView;
    private Chronometer chronometerCurrent;
    private Chronometer chronometerTotal;
    private boolean play = true;
    private Handler handler = new Handler();
    private Utilities utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        getTags();
    }

    private void initViews (){
        playPause = (ToggleButton) findViewById(R.id.buttonPlayPause);
        mediaPlayer = MediaPlayer.create(this, R.raw.borderlands);
        seekBar = (SeekBar) findViewById(R.id.scrollingCurrentTrackSeekBar);
        seekBar.setProgress(0);
        seekBar.setMax(100);
        infoTextView = (TextView) findViewById(R.id.currentTrackInformationTextView);
        chronometerCurrent = (Chronometer) findViewById(R.id.currentTrackChronometer);
        chronometerTotal = (Chronometer) findViewById(R.id.totalTrackChronometer);
        utils = new Utilities();
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    private void getTags (){
        String filePath = "sdcard/download/borderlands.mp3";
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(filePath);
        String album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String trackName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        infoTextView.setText(artist + " - " + trackName + " - " + album);

    }

    public void setPlayPause (View view){
        if (play) {
            mediaPlayer.start();
            updateSeekBar();
        }
        else {
            mediaPlayer.pause();
        }
        play = !play;

    }

    public void updateSeekBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            chronometerTotal.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            chronometerCurrent.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Toast.makeText(this, "Пункт меню Settings", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_open:
                Toast.makeText(this, "Пункт меню Open", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_exit:
                this.finishAffinity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateSeekBar();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
