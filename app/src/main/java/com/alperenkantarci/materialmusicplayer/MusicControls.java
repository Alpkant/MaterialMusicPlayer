package com.alperenkantarci.materialmusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Alperen Kantarci on 4.08.2017.
 */

// TODO(2) ADJUST SEEKBAR AND IMPLEMENT TO SONG
// TODO(3) ADD BUTTON IMAGES (SHUFFLE VE RANDOMU SONA BIRAK)AND IMPLEMENT THEIR METHODS
// TODO(4) SONG LISTE DÖNÜŞÜ EKLE
// TODO(5) ALBUM ARTLARI ALMANIN YOLUNU BUL

public class MusicControls extends AppCompatActivity{

    boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.alperenkantarci.materialmusicplayer.PlayNewAudio";


    ArrayList<Audio> audioList;
    int audioIndex;
    MediaPlayerService mediaPlayer;
    SeekBar seekBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_song);

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView albumTextView = (TextView) findViewById(R.id.albumTextView);
        TextView artistTextView = (TextView) findViewById(R.id.artistTextView);
        final TextView currentSecondTextView = (TextView) findViewById(R.id.currentSecond);
        TextView maxSecondTextView = (TextView) findViewById(R.id.maxSecond);
        seekBar = (SeekBar) findViewById(R.id.seekBar);


        Intent comingIntent = getIntent();
        Bundle comingBundle = comingIntent.getExtras();
        StorageUtil storage = new StorageUtil(getApplicationContext());
        audioList = storage.loadAudio();
        playAudio(audioIndex);

        try {
            audioIndex = (Integer) comingBundle.get("audioIndex");
            titleTextView.setText(audioList.get(audioIndex).getTitle());
            artistTextView.setText(audioList.get(audioIndex).getArtist());
            albumTextView.setText(audioList.get(audioIndex).getAlbum());
            final int duration = audioList.get(audioIndex).getDuration();
            maxSecondTextView.setText(millisecToTime(duration));
            seekBar.setMax(duration);
            final Handler mHandler = new Handler();

            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    int mCurrentPosition = 0;
                    if(mediaPlayer != null){
                        mCurrentPosition = mediaPlayer.getCurrentTime();
                        if (mCurrentPosition == duration)
                            Log.e("CURRENT", String.valueOf(mCurrentPosition));
                        currentSecondTextView.setText(millisecToTime(mCurrentPosition));
                        seekBar.setProgress(mCurrentPosition);

                    }else{
                        Log.e("COULDNT'","COULDNT");
                    }

                    if(mCurrentPosition >= duration){
                        mHandler.removeCallbacks(this);
                    }else{
                        mHandler.postDelayed(this, 1000);
                    }

                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean userChanged) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });



        }catch (NullPointerException e){
            Log.e("index error","index error");
        }




        Log.e("CONTROL PAGE","CONTROLPAGE");
    }







    String millisecToTime(int millisec) {
        int sec = millisec/1000;
        int second = sec % 60;
        int minute = sec / 60;
        if (minute >= 60) {
            int hour = minute / 60;
            minute %= 60;
            return hour + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
        }
        return minute + ":" + (second < 10 ? "0" + second : second);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            mediaPlayer.stopSelf();
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) iBinder;
            mediaPlayer = binder.getService();
            serviceBound = true;
            Log.i("Service Bound", "SERVICE BOUNDED");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };


    private void playAudio(int audioIndex) {
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }



}
