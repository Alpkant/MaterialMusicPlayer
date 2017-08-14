package com.alperenkantarci.materialmusicplayer.executor;

import android.app.Application;
import android.content.Context;

import com.alperenkantarci.materialmusicplayer.model.Database.DBPlayer;


public class MyApplication extends Application {
    private MyApplication sInstance;

    private Context c;

    private DBPlayer mDatabase;

    public MyApplication getInstance() {
        return sInstance;
    }

    public MyApplication(Context c) {
        this.c = c;
    }

    public synchronized DBPlayer getWritableDatabase() {
        if (mDatabase == null) {
            mDatabase = new DBPlayer(c);
        }
        return mDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mDatabase = new DBPlayer(this);
    }

}
