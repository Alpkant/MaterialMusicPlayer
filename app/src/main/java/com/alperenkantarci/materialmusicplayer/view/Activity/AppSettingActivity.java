package com.alperenkantarci.materialmusicplayer.view.Activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.alperenkantarci.materialmusicplayer.R;
import com.alperenkantarci.materialmusicplayer.component.AppConstants;
import com.alperenkantarci.materialmusicplayer.component.SharedPreferenceSingelton;
import com.alperenkantarci.materialmusicplayer.component.ThemeSelector;
import com.alperenkantarci.materialmusicplayer.executor.RecycleViewAdapters.FoldersAdapter;
import com.alperenkantarci.materialmusicplayer.model.Pojo.PlaylistSelect;
import com.bumptech.glide.Glide;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.util.ArrayList;

public class AppSettingActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    private Dialog dialog;
    private EditText min;
    //private static EqualizerPresetListener equalizerPresetListener;
    private RadioGroup radioButtonGroup;
    SharedPreferenceSingelton sharedPreferenceSingelton;
    CoordinatorLayout back;
    Switch pro;
    private TextView short_time;
    private int previous_set;
    private HoloCircleSeekBar seekBar;
    RelativeLayout theme_dialog;

    private ArrayList<PlaylistSelect> folders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceSingelton = new SharedPreferenceSingelton();

        new ThemeSelector().setAppTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init();


    }

    public int getLayoutId() {
        return R.layout.activity_app_setting;
    }

    private void init() {
        // Toolbar
        ImageView background = (ImageView) findViewById(R.id.back);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                AppSettingActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


    }

    public void changeTheme(View v) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.theme_select_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ViewPager viewpager = (ViewPager) dialog.findViewById(R.id.view_pager);
        theme_dialog = (RelativeLayout) dialog.findViewById(R.id.theme_dialog);

        viewpager.setClipToPadding(false);
        viewpager.setPadding(40,0,70,0);
        viewpager.setPageMargin(20);
        viewpager.addOnPageChangeListener(viewPagerPageChangeListener);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewpager.setAdapter(myViewPagerAdapter);
        try{
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }catch (NullPointerException e){}
        dialog.show();

    }


    public void openSleepDialog(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.sleep_timer_dialog);
        try{
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }catch (NullPointerException e){}
        dialog.show();

        final CardView fifteen = (CardView) dialog.findViewById(R.id.fifteen);
        final CardView thirty = (CardView) dialog.findViewById(R.id.thirty);
        final CardView fortyfive = (CardView) dialog.findViewById(R.id.fortyfive);
        final CardView sixty = (CardView) dialog.findViewById(R.id.sixty);
        min = (EditText) dialog.findViewById(R.id.minutes);
        final Button done = (Button) dialog.findViewById(R.id.done);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == fifteen.getId())
                    setTimer(15);
                else if (v.getId() == thirty.getId())
                    setTimer(30);
                else if (v.getId() == fortyfive.getId())
                    setTimer(45);
                else if (v.getId() == sixty.getId())
                    setTimer(60);
                else if (v.getId() == done.getId()) {
                    String minutes = min.getText().toString();
                    if (minutes.equalsIgnoreCase("")) {
                        Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                    } else if (minutes.equalsIgnoreCase("0")) {
                        Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                    } else {
                        int m = Integer.parseInt(minutes);
                        setTimer(m);
                    }
                } else if (v.getId() == cancel.getId())
                    dialog.dismiss();
            }
        };
        fifteen.setOnClickListener(clickListener);
        thirty.setOnClickListener(clickListener);
        fortyfive.setOnClickListener(clickListener);
        sixty.setOnClickListener(clickListener);
        done.setOnClickListener(clickListener);
        cancel.setOnClickListener(clickListener);

    }

    void setTimer(int minutes) {
        long d = System.currentTimeMillis() + (minutes * 60 * 1000);
        Intent intent = new Intent("Stop");
        PendingIntent pi = PendingIntent.getBroadcast(AppSettingActivity.this, 5, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, d, pi);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, d, pi);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, d, pi);
        dialog.dismiss();
    }

    public void shareAppLink(View v) {
        String message = "https://play.google.com/store/apps/details?id=com.alperenkantarci.materialmusicplayer";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share via.."));
    }

    public void rateApp(View v) {
        final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
            startActivity(rateAppIntent);
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void openFolderDialog(View view) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.folder_select);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        try{
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }catch (NullPointerException e){}
        dialog.show();

        Button done = (Button) dialog.findViewById(R.id.done);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        RecyclerView recyclerView;
        FoldersAdapter foldersAdapter;
        recyclerView = (RecyclerView) dialog.findViewById(R.id.foldersList);

        folders=getFolderNames();

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foldersAdapter = new FoldersAdapter(this, folders, recyclerView);
        recyclerView.setAdapter(foldersAdapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unselected="";
                int x=0;
                for(int i=0;i<folders.size();i++){
                    if(!folders.get(i).isSelected()){
                        x++;
                        unselected+=folders.get(i).getName()+",";
                    }
                }
                if(x==folders.size()){
                    Toast.makeText(AppSettingActivity.this, "Cannot unselect All Folders", Toast.LENGTH_SHORT).show();
                }else {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "SkipFolders", unselected);
                    dialog.dismiss();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public ArrayList<PlaylistSelect> getFolderNames() {
        ArrayList<PlaylistSelect> folders=new ArrayList<>();
        ArrayList<String> names=new ArrayList<>();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    int lastSlash=path.lastIndexOf('/');
                    int secondLastSlash=0;
                    for(int i=lastSlash-1;i>=0;i--){
                        if(path.charAt(i)=='/'){
                            secondLastSlash=i;
                            break;
                        }
                    }
                    String folder=path.substring(secondLastSlash+1, lastSlash);
                    if(!names.contains(folder))
                    names.add(folder);
            }
            while (cursor.moveToNext());
        }
        for (String s:names){
            folders.add(new PlaylistSelect(s,true));
        }

        return folders;
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);
            CardView cardView = (CardView) itemView.findViewById(R.id.card);
            cardView.setCardBackgroundColor(getResources().getColor(AppConstants.backgroundColors[position]));

            TextView textView = (TextView) itemView.findViewById(R.id.text);
            textView.setText(AppConstants.texts[position]);
            textView.setTextColor(getResources().getColor(AppConstants.textColors[position]));
            ImageView tick = (ImageView) itemView.findViewById(R.id.tick);

            Button button = (Button) itemView.findViewById(R.id.button);



            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 0);
                    } else if (position == 1) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 1);
                    }
                    dialog.dismiss();
                    // recreate();
                    finish();
                    Intent intent = IntentCompat.makeMainActivity(new ComponentName(
                            AppSettingActivity.this, MainActivity.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });


            container.addView(itemView);

            return itemView;
        }

        @Override
        public int getCount() {
            return AppConstants.backgroundColors.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < AppConstants.backgroundColors.length; i++) {
                dialog.findViewById(AppConstants.buttonId[i]).setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
            }
            dialog.findViewById(AppConstants.buttonId[position]).setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        }
    }

