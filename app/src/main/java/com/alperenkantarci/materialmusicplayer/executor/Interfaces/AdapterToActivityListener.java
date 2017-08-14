package com.alperenkantarci.materialmusicplayer.executor.Interfaces;

/**
 * Created by Alperen on 21/6/17.
 */

public interface AdapterToActivityListener {
    public void onTrackLongPress(int c, long songId, boolean songAdded);
    public void onFirstTrackLongPress();
}
