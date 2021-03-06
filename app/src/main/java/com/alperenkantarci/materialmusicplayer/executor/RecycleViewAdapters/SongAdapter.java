package com.alperenkantarci.materialmusicplayer.executor.RecycleViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alperenkantarci.materialmusicplayer.R;
import com.alperenkantarci.materialmusicplayer.component.CustomAnimation;
import com.alperenkantarci.materialmusicplayer.component.SharedPreferenceSingelton;
import com.alperenkantarci.materialmusicplayer.component.ThemeSelector;
import com.alperenkantarci.materialmusicplayer.executor.Interfaces.AdapterToActivityListener;
import com.alperenkantarci.materialmusicplayer.executor.Interfaces.MainListPlayingListener;
import com.alperenkantarci.materialmusicplayer.executor.MyApplication;
import com.alperenkantarci.materialmusicplayer.executor.PlaySongExec;
import com.alperenkantarci.materialmusicplayer.model.Pojo.Song;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter {

    public ArrayList<Song> songsList;
    private Context c;
    public int count = 0;
    private MainListPlayingListener mainListPlayingListener;
    private AdapterToActivityListener adapterToActivityListener;
    private int colorSelected = Color.LTGRAY;
    private int colorNormal = Color.WHITE;
    private Cursor dataCursor;
    private int textLimit = 27;

    public SongAdapter(Context context, RecyclerView recyclerView, Cursor cursor) {
        dataCursor = cursor;
        adapterToActivityListener = (AdapterToActivityListener) context;
        c = context;
        int colors[]=new ThemeSelector().getThemeForSongAdapter(c);
        colorSelected=colors[0];
        colorNormal=colors[1];
    }

    public SongAdapter getInstance() {
        return SongAdapter.this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_row, parent, false);
        return new SongViewHolder(view, c);
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        int short_time = new SharedPreferenceSingelton().getSavedInt(c, "Short_music_time");
        if (cursor != null && cursor.moveToFirst()) {
            songsList = new ArrayList<>();

            do {
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                if (duration > (short_time * 1000)) {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String imagepath = "content://media/external/audio/media/" + id + "/albumart";
                    if (title.length() > textLimit)
                        title = title.substring(0, textLimit) + "...";
                    if (artist == null)
                        artist = "Unknown";
                    if (artist.length() > textLimit)
                        artist = artist.substring(0, textLimit) + "...";
                    songsList.add(new Song(id, duration, title, artist, imagepath, false));
                }
            }
            while (cursor.moveToNext());
        }

        return oldCursor;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Song song = songsList.get(position);
        String name = song.getName();
        String artist = song.getArtist();
        String imagepath = song.getImagepath();

        ((SongViewHolder) holder).name.setText(name);

        ((SongViewHolder) holder).artist.setText(artist);

        Glide.with(c).load(imagepath)
                .dontAnimate()
                .centerCrop()
                .placeholder(R.drawable.dummy)
                .into(((SongViewHolder) holder).iv);
        if (new MyApplication(c).getWritableDatabase().isFavourite(song.getID())) {
            song.setFavourite(true);
            ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_liked_toolbar);
        } else {
            song.setFavourite(false);
            ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_like);
        }

        ((SongViewHolder) holder).songListCard.setCardBackgroundColor(song.isSelected() ? colorSelected : colorNormal);
        ((SongViewHolder) holder).song = song;


    }


    public void setMainListPlayingListener(MainListPlayingListener mainListPlayingListener) {
        this.mainListPlayingListener = mainListPlayingListener;
    }

    public void delete(int position) {
        songsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : songsList.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView iv;
        TextView name, artist;
        ImageButton like;
        CardView songListCard;
        private Context ctx;
        Song song;

        SongViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;
            iv = (ImageView) v.findViewById(R.id.album_art);
            name = (TextView) v.findViewById(R.id.name);
            artist = (TextView) v.findViewById(R.id.artist_mini);
            like = (ImageButton) v.findViewById(R.id.like);

            songListCard = (CardView) v.findViewById(R.id.song_list_card);
            like.setOnClickListener(this);
            songListCard.setOnClickListener(this);
            songListCard.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == like.getId()) {
                like.startAnimation(new CustomAnimation().likeAnimation(ctx));
                if (song.getFavourite()) {
                    new MyApplication(ctx).getWritableDatabase().deleteFavourite(song.getID());
                    like.setImageResource(R.drawable.ic_like);
                    song.setFavourite(false);
                } else {
                    new MyApplication(ctx).getWritableDatabase().insertFavourite(song.getID());
                    like.setImageResource(R.drawable.ic_liked_toolbar);
                    song.setFavourite(true);
                }
            } else if (v.getId() == songListCard.getId()) {
                if (count > 0 && count <= songsList.size()) {
                    if (song.isSelected()) {
                        song.setSelected(false);
                        count--;
                        adapterToActivityListener.onTrackLongPress(count, song.getID(), false);
                    } else {
                        song.setSelected(true);
                        count++;
                        adapterToActivityListener.onTrackLongPress(count, song.getID(), true);
                    }
                    songListCard.setCardBackgroundColor(song.isSelected() ? colorSelected : colorNormal);
                } else {
                    mainListPlayingListener.onPlayingFromTrackList();
                    new SharedPreferenceSingelton().saveAs(ctx, "Shuffle", false);
                    new PlaySongExec(ctx, getAdapterPosition()).startPlaying();
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (count == 0) {
                adapterToActivityListener.onFirstTrackLongPress();
            }

            if (song.isSelected()) {
                song.setSelected(false);
                count--;
                adapterToActivityListener.onTrackLongPress(count, song.getID(), false);
            } else {
                song.setSelected(true);
                count++;
                adapterToActivityListener.onTrackLongPress(count, song.getID(), true);
            }
            songListCard.setCardBackgroundColor(song.isSelected() ? colorSelected : colorNormal);
            return true;
        }

    }
}
