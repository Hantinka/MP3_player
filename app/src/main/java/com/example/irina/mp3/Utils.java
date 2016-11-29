package com.example.irina.mp3;

import android.util.Log;

/**
 * Created by Irina on 26.11.2016.
 */
public class Utils {
    private final static String TAG = "myLogs";
    static void DBG (String logString){
        Log.d(TAG,logString);
    }

    static int getCalculatedPercentage (long full, long part){
        int calculatedPercentage;
        calculatedPercentage = (int) (part*100/full);
        return calculatedPercentage;
    }

    static String millisecondsIntoTimeFormat (long milliseconds){
        String convertedTimer = "";
        String formatSeconds = "";
        int hours;
        int minutes;
        int seconds;
        hours = (int) (milliseconds / (1000*60*60));
        minutes = (int) (milliseconds % (1000*60*60)) / (1000*60);
        seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60)) / 1000;
            if (hours > 0){
                convertedTimer = hours + ":";
            }
            if (seconds < 10){
                formatSeconds = "0" + seconds;
            }
            else {
                formatSeconds = "" + seconds;
            }
        convertedTimer = convertedTimer + minutes + ":" + formatSeconds;
        return convertedTimer;
    }

    static int changedSeekProgressWithTimer (int progressSeek, int full){
        int part = 0;
        full = (int) (full / 1000);
        part = (int) ((((double) progressSeek) / 100) * full);
        return part * 1000;
    }
}
