package com.example.irina.mp3;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.io.IOException;


public class ActMain extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ToggleButton playPause;
    private SeekBar seekBar;
    private TextView infoTextView;
    private Chronometer chronometerCurrent;
    private Chronometer chronometerTotal;
    private boolean play = true;
    private Handler handler = new Handler(); // Handler to update UI timer, progress bar etc,.
    private ImageView ivAlbumCover;
    private static final int SELECTED_SONG = 1;
    private String selectedSongPath;
    private Button openDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.act_main);
        initViews();
    }

    private void initViews (){
        playPause = (ToggleButton) findViewById(R.id.buttonPlayPause);
        seekBar = (SeekBar) findViewById(R.id.scrollingCurrentTrackSeekBar);
        seekBar.setProgress(0);
        seekBar.setMax(100);
        infoTextView = (TextView) findViewById(R.id.currentTrackInformationTextView);
        chronometerCurrent = (Chronometer) findViewById(R.id.currentTrackChronometer);
        chronometerTotal = (Chronometer) findViewById(R.id.totalTrackChronometer);
        seekBar.setOnSeekBarChangeListener(this);
        ivAlbumCover = (ImageView) findViewById(R.id.centerImageImageView);

        openDialog = (Button)findViewById(R.id.openDialogButton);
    }

    private void setMetadata () {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        String filePath = selectedSongPath;
        mediaMetadataRetriever.setDataSource(filePath);
        String album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String trackName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        infoTextView.setText(artist + " - " + trackName + " - " + album);
        setCover(mediaMetadataRetriever);
    }

    public boolean setCover (MediaMetadataRetriever mediaMetadataRetriever){
        byte [] albumCover;
        albumCover = mediaMetadataRetriever.getEmbeddedPicture();
        if (albumCover != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumCover, 0, albumCover.length);
            ivAlbumCover.setImageBitmap(bitmap);
            Utils.DBG("Cover");
            return true;
        }
        else {
            ivAlbumCover.setImageResource(R.drawable.background);
            Utils.DBG("No Cover");
            return false;
        }
    }

    public void updateTrack (){
        String filePath = selectedSongPath;
        mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath));
        mediaPlayer.setOnCompletionListener(this);

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
                Utils.DBG(selectedSongPath);
                updateTrack();
                long totalDuration = mediaPlayer.getDuration();
                chronometerTotal.setText("" + Utils.millisecondsIntoTimeFormat(totalDuration));
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
                //Toast.makeText(this, "Пункт меню Settings", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_open:
                //Toast.makeText(this, "Пункт меню Open", Toast.LENGTH_LONG).show();
                getNewSong();
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

    public void onOpenFileClick (View view){
        //OpenFileDialog fileDialog = new OpenFileDialog(this);
        //fileDialog.show();
        Intent intent = new Intent(this, ActFilesList.class);
        startActivity(intent);
    }

    public void onGoTestClick (View view){
        Intent testIntent = new Intent(this, ActTest.class);
        startActivity(testIntent);
    }

    @Override
    public void onClick(View v) {

    }
}
