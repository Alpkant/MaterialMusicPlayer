package com.alperenkantarci.materialmusicplayer.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.alperenkantarci.materialmusicplayer.R;



public class ThemeSelector {
    private SharedPreferenceSingelton sharedPreferenceSingelton;

    public int[] getThemeForSongAdapter(Context context){
        sharedPreferenceSingelton=new SharedPreferenceSingelton();
        int currentTheme=sharedPreferenceSingelton.getSavedInt(context, "Themes");
        int colors[]={0,0};
        switch (currentTheme){
            case 1:
                colors[0] = Color.rgb(58, 58, 71);
                colors[1] = Color.rgb(22, 22, 25);
                break;
            case 2:
                colors[0] = Color.rgb(163, 87, 66);
                colors[1] = Color.rgb(110, 60, 27);
                break;
            case 3:
                colors[0] = Color.rgb(96, 77, 89);
                colors[1] = Color.rgb(73, 59, 68);
                break;
            case 4:
                colors[0] = Color.rgb(242, 238, 220);
                colors[1] = Color.rgb(255, 253, 242);
                break;
            case 5:
                colors[0] = Color.rgb(81, 109, 137);
                colors[1] = Color.rgb(52, 73, 94);
                break;
            case 6:
                colors[0] = Color.rgb(58, 58, 71);
                colors[1] = Color.rgb(0, 119, 192);
                break;
            case 7:
                colors[0] = Color.rgb(242, 207, 169);
                colors[1] = Color.rgb(255, 233, 209);
                break;
            default:
                colors[0] = Color.LTGRAY;
                colors[1] = Color.WHITE;;
        }
        return colors;
    }
    public void setAppTheme(Activity activity){
        sharedPreferenceSingelton=new SharedPreferenceSingelton();
        int currentTheme=sharedPreferenceSingelton.getSavedInt(activity, "Themes");
        if (currentTheme == 1)
            activity.setTheme(R.style.AppTheme_Dark);
        else if (currentTheme == 2)
            activity.setTheme(R.style.AppTheme_Dark2);
        else if (currentTheme == 3)
            activity.setTheme(R.style.AppTheme_Dark3);
        else if (currentTheme == 4)
            activity.setTheme(R.style.AppTheme_Dark4);
        else if (currentTheme == 5)
            activity.setTheme(R.style.AppTheme_Dark5);
        else if (currentTheme == 6)
            activity.setTheme(R.style.AppTheme_Dark6);
        else if (currentTheme == 7)
            activity.setTheme(R.style.AppTheme_Dark7);
    }
}
