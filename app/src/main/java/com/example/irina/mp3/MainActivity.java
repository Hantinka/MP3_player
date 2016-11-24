package com.example.irina.mp3;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;



public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ToggleButton playPause;
    private SeekBar seekBar;
    private TextView infoTextView;
    private Chronometer chronometerStart;
    private Chronometer chronometerEnd;
    private boolean play = true;



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
        infoTextView = (TextView) findViewById(R.id.currentTrackInformationTextView);
        chronometerStart = (Chronometer) findViewById(R.id.startTrackChronometer);
        chronometerEnd = (Chronometer) findViewById(R.id.endTrackChronometer);

    }

    private void getTags (){
        String filePath = "sdcard/download/borderlands.mp3";
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(filePath);
        String album = mediaMetadataRetriever.extractMetadata(1);
        String trackName = mediaMetadataRetriever.extractMetadata(7);
        String artist = mediaMetadataRetriever.extractMetadata(2);
        infoTextView.setText(artist + " - " + trackName + " - " + album);
    }


    public void setPlayPause (View view){
        if (play) {
            mediaPlayer.start();
        }
        else {
            mediaPlayer.pause();
        }
        play = !play;
    }



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
}
