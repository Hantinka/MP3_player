package com.example.irina.mp3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private ImageButton buttonPlayPause;
    private boolean play = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonPlayPause = (ImageButton) findViewById(R.id.buttonPlayPause);
        buttonPlayPause.setImageResource(R.drawable.player_play_normal);
        buttonPlayPause.setOnClickListener(onClickButtonPlayPause);
    }
    View.OnClickListener onClickButtonPlayPause = new View.OnClickListener(){
    @Override
    public void onClick(View v){
        if (play){
            buttonPlayPause.setImageResource(R.drawable.player_pause_normal);
        }
        else {
            buttonPlayPause.setImageResource(R.drawable.player_play_normal);
        }
        play = !play;
    }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
