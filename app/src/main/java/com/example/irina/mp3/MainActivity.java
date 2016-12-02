package com.example.irina.mp3;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;



public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private ToggleButton playPause;
    private SeekBar seekBar;
    private TextView infoTextView;
    private Chronometer chronometerCurrent;
    private Chronometer chronometerTotal;
    private boolean play = true;
    private Handler handler = new Handler(); // Handler to update UI timer, progress bar etc,.
    private EditText filePathEditText;
    private Button newPathButton;

    private static final int SELECTED_SONG = 1;
    private String selectedSongPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        initViews();
        setMetadata();
    }

    private void initViews (){
        newPathButton = (Button) findViewById(R.id.newPathButton);
        newPathButton.setOnClickListener(oclNewPathButton);
        playPause = (ToggleButton) findViewById(R.id.buttonPlayPause);
        filePathEditText = (EditText) findViewById(R.id.filePathEditText);
        String filePathHardcode = "sdcard/download/borderlands.mp3";
        filePathEditText.setText(filePathHardcode);
        String filePath = filePathEditText.getText().toString();
        mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath));
        seekBar = (SeekBar) findViewById(R.id.scrollingCurrentTrackSeekBar);
        seekBar.setProgress(0);
        seekBar.setMax(100);
        infoTextView = (TextView) findViewById(R.id.currentTrackInformationTextView);
        chronometerCurrent = (Chronometer) findViewById(R.id.currentTrackChronometer);
        chronometerTotal = (Chronometer) findViewById(R.id.totalTrackChronometer);


        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

    }

    private void setMetadata (){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        String filePath = filePathEditText.getText().toString();
        mediaMetadataRetriever.setDataSource(filePath);
        String album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String trackName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        infoTextView.setText(artist + " - " + trackName + " - " + album);
    }

    public void updateTrack (){
        String filePath = filePathEditText.getText().toString();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setMetadata();
        Utils.DBG("Update track finished");
    }

    public void getNewSong(){
        Intent audioFileIntent = new Intent();
        audioFileIntent.setType("audio/mp3");
        audioFileIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(audioFileIntent, "Select mp3-file"), SELECTED_SONG);
        Utils.DBG("Get new song finished");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == SELECTED_SONG){
                Uri selectedSongUri = data.getData();
                selectedSongPath = getNewSongPath(selectedSongUri);
                filePathEditText.setText(selectedSongPath);
                Utils.DBG(selectedSongPath);
                updateTrack();
            }
        }
    }

    public String getNewSongPath (Uri uri){
        String [] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor  = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    View.OnClickListener oclNewPathButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getNewSong();
        }
    };

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
            chronometerTotal.setText("" + Utils.millisecondsIntoTimeFormat(totalDuration));
            chronometerCurrent.setText("" + Utils.millisecondsIntoTimeFormat(currentDuration));
            int progress = Utils.getCalculatedPercentage(totalDuration,currentDuration);
            seekBar.setProgress(progress);
            handler.postDelayed(this, 100); // Running this thread after 100 milliseconds
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
        int currentPosition = Utils.changedSeekProgressWithTimer(seekBar.getProgress(), totalDuration);
        mediaPlayer.seekTo(currentPosition); // forward or backward to certain seconds
        updateSeekBar(); // update timer progress again
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
