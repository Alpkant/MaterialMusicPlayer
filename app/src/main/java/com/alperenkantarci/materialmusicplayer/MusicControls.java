package com.alperenkantarci.materialmusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Alperen Kantarci on 4.08.2017.
 */

// TODO(1) MAIN LISTE DÖNÜŞTE RUNNABLE KAPAT
// TODO(2) SETTINGSE THEME EKLE
// TODO(3) ALBUM ARTLARI ALMANIN YOLUNU BUL

public class MusicControls extends AppCompatActivity{

    boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.alperenkantarci.materialmusicplayer.PlayNewAudio";


    ArrayList<Audio> audioList;
    int audioIndex;
    MediaPlayerService mediaPlayer;
    SeekBar seekBar;
    TextView titleTextView;
    TextView albumTextView;
    TextView artistTextView;
    TextView currentSecondTextView;
    TextView maxSecondTextView;
    ImageButton previousSong,nextSong,stopPlay;
    boolean startStop=true;
    Runnable myRunnable;
    int duration;
    final Handler mHandler = new Handler();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.play_list,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                mHandler.removeCallbacks(myRunnable);
                return true;

            case R.id.action_list:
                mHandler.removeCallbacks(myRunnable);
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_song);
        Log.e("onCreate","onCreate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        albumTextView = (TextView) findViewById(R.id.albumTextView);
        artistTextView = (TextView) findViewById(R.id.artistTextView);
        currentSecondTextView = (TextView) findViewById(R.id.currentSecond);
        maxSecondTextView = (TextView) findViewById(R.id.maxSecond);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        previousSong = (ImageButton) findViewById(R.id.previousSong);
        nextSong = (ImageButton) findViewById(R.id.nextSong);
        stopPlay = (ImageButton) findViewById(R.id.stopPlay);



        Intent comingIntent = getIntent();
        Bundle comingBundle = comingIntent.getExtras();
        StorageUtil storage = new StorageUtil(getApplicationContext());
        audioList = storage.loadAudio();
        if(serviceBound)
            mediaPlayer.stopMusic();

        playAudio(audioIndex);

        try {
            audioIndex = (Integer) comingBundle.get("audioIndex");
            titleTextView.setText(audioList.get(audioIndex).getTitle());
            artistTextView.setText(audioList.get(audioIndex).getArtist());
            albumTextView.setText(audioList.get(audioIndex).getAlbum());
            duration  = audioList.get(audioIndex).getDuration();
            maxSecondTextView.setText(millisecToTime(duration));
            seekBar.setMax(duration);

            myRunnable = new Runnable() {

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
                        Log.e("MediaPlayer null","MediaPlayer null");
                    }

                    if(mCurrentPosition >= duration){
                        //mHandler.removeCallbacks(this);
                        mediaPlayer.skipToNext();
                        updateMetaData();
                    }else{
                        mHandler.postDelayed(this, 1000);
                    }

                }
            };

            this.runOnUiThread(myRunnable);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean userChanged) {
                    if (userChanged && mediaPlayer.isPlaying()){
                        mediaPlayer.seekToMusic(progress);
                        seekBar.setProgress(progress);
                        currentSecondTextView.setText(millisecToTime(progress));
                    }else if(userChanged){
                        mediaPlayer.resumeMusic();
                        mediaPlayer.seekToMusic(progress);
                        currentSecondTextView.setText(millisecToTime(progress));
                        mediaPlayer.pauseMusic();
                    }
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

        View.OnClickListener controlListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.previousSong:
                        audioIndex= mediaPlayer.skipToPrev();
                        updateMetaData();
                        stopPlay.setBackground(getDrawable(R.drawable.ic_pause_circle_outline_black_48dp));
                        startStop=true;
                        break;

                    case R.id.nextSong:
                        audioIndex = mediaPlayer.skipToNex();
                        updateMetaData();
                        stopPlay.setBackground(getDrawable(R.drawable.ic_pause_circle_outline_black_48dp));
                        startStop=true;
                        break;

                    case R.id.stopPlay:
                        if(!startStop){
                            stopPlay.animate().rotationY(0).start();
                            stopPlay.setBackground(getDrawable(R.drawable.ic_pause_circle_outline_black_48dp));
                            startStop=true;
                            mediaPlayer.resumeMusic();
                        }else{
                            stopPlay.animate().rotationY(360).start();
                            stopPlay.setBackground(getDrawable(R.drawable.ic_play_circle_outline_black_48dp));
                            startStop=false;
                            mediaPlayer.pauseMusic();
                        }
                        break;
                }
            }
        };

        nextSong.setOnClickListener(controlListener);
        previousSong.setOnClickListener(controlListener);
        stopPlay.setOnClickListener(controlListener);


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
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(myRunnable);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy","onDestroy");
       /* if (serviceBound) {
            unbindService(serviceConnection);
            mediaPlayer.stopSelf();
            }*/
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

    private void updateMetaData(){
        titleTextView.setText(audioList.get(audioIndex).getTitle());
        artistTextView.setText(audioList.get(audioIndex).getArtist());
        albumTextView.setText(audioList.get(audioIndex).getAlbum());
        duration = audioList.get(audioIndex).getDuration();
        seekBar.setMax(duration);
        maxSecondTextView.setText(millisecToTime(duration));
    }


}
