package com.alperenkantarci.materialmusicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
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

    ArrayList<Audio> audioList;
    int audioIndex;
    MediaPlayerService mediaPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_song);

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView albumTextView = (TextView) findViewById(R.id.albumTextView);
        TextView artistTextView = (TextView) findViewById(R.id.artistTextView);
        TextView currentSecondTextView = (TextView) findViewById(R.id.currentSecond);
        TextView maxSecondTextView = (TextView) findViewById(R.id.maxSecond);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(99);


        Intent comingIntent = getIntent();
        Bundle comingBundle = comingIntent.getExtras();
        StorageUtil storage = new StorageUtil(getApplicationContext());
        audioList = storage.loadAudio();

        try {
            audioIndex = (Integer) comingBundle.get("audioIndex");
            titleTextView.setText(audioList.get(audioIndex).getTitle());
            artistTextView.setText(audioList.get(audioIndex).getArtist());
            albumTextView.setText(audioList.get(audioIndex).getAlbum());
            if(audioList.get(audioIndex).getDuration()!= -1){
                seekBar.setMax(audioList.get(audioIndex).getDuration());
                maxSecondTextView.setText(audioList.get(audioIndex).getDuration());
            }else{
                seekBar.setMax(audioList.get(audioIndex).getDuration());
                maxSecondTextView.setText(audioList.get(audioIndex).getDuration());

            }


        }catch (NullPointerException e){
            Log.e("index error","index error");
        }




        Log.e("CONTROL PAGE","CONTROLPAGE");
    }
}
