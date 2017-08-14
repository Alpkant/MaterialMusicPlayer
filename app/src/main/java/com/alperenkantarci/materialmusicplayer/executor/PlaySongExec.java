package com.alperenkantarci.materialmusicplayer.executor;

import android.app.Activity;
import android.content.Context;

import com.alperenkantarci.materialmusicplayer.model.MusicService;
import com.alperenkantarci.materialmusicplayer.view.Activity.MainActivity;


/**
 * Created by Alperen on 14/3/17.
 */

public class PlaySongExec {

    private Context ctx;
    private Activity mActivity;
    private int mPos;

    public PlaySongExec(Context context, int position) {
        this.ctx = context;
        this.mPos = position;
    }

    public void startPlaying() {
        MusicService musicService =((MainActivity)ctx).getMusicService();
        musicService.setSong(mPos);
        musicService.togglePlay();
    }
}
