package com.xuchengpu.myproject.myproject.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemVideoPlayer extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private VideoView videoview;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystetime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwichePlayer;
    private LinearLayout llBottom;
    private TextView tvCurrenttime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwichScreen;
    private Utils utils;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case  PROGRESS:
                    int currentPosition=videoview.getCurrentPosition();
                    tvCurrenttime.setText(utils.stringForTime(currentPosition));
                    seekbarVideo.setProgress(currentPosition);
                    tvSystetime.setText(getSystemTime());
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
            }
        }


    };

    private String getSystemTime() {
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        String systemTime=format.format(new Date());
        return systemTime;
    }

    private final  static int PROGRESS=0;
    private MyBatterReceiver receive;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-09 18:25:50 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystetime = (TextView)findViewById( R.id.tv_systetime );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwichePlayer = (Button)findViewById( R.id.btn_swiche_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrenttime = (TextView)findViewById( R.id.tv_currenttime );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnPre = (Button)findViewById( R.id.btn_pre );
        btnStartPause = (Button)findViewById( R.id.btn_start_pause );
        btnNext = (Button)findViewById( R.id.btn_next );
        btnSwichScreen = (Button)findViewById( R.id.btn_swich_screen );

        btnVoice.setOnClickListener( this );
        btnSwichePlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnPre.setOnClickListener( this );
        btnStartPause.setOnClickListener( this );
        btnNext.setOnClickListener( this );
        btnSwichScreen.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-01-09 18:25:50 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnVoice ) {
            // Handle clicks for btnVoice
        } else if ( v == btnSwichePlayer ) {
            // Handle clicks for btnSwichePlayer
        } else if ( v == btnExit ) {
            finish();
            // Handle clicks for btnExit
        } else if ( v == btnPre ) {
            // Handle clicks for btnPre
        } else if ( v == btnStartPause ) {
            // Handle clicks for btnStartPause
            startAndPause();
        } else if ( v == btnNext ) {
            // Handle clicks for btnNext
        } else if ( v == btnSwichScreen ) {
            // Handle clicks for btnSwichScreen
        }
    }

    private void startAndPause() {
        if(videoview.isPlaying()) {
            videoview.pause();
            btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        }else{
            videoview.start();
            btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        findViews();
        initData();
        getData();
        setData();
        setLinstener();
    }

    private void initData() {
        utils=new Utils();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receive=new MyBatterReceiver();
        registerReceiver(receive,filter);
    }

    private void setLinstener() {
        videoview.setOnCompletionListener(new MyOnCompletionListener());
        videoview.setOnErrorListener(new MyOnErrorListener());
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        seekbarVideo.setOnSeekBarChangeListener(this);
       // videoview.setMediaController(new MediaController(this));
    }

    private void setData() {
        videoview.setVideoURI(uri);
    }

    private void getData() {
        uri = getIntent().getData();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            seekBar.setProgress(progress);
            videoview.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            int duration=videoview.getDuration();
            tvDuration.setText(utils.stringForTime(duration));
            seekbarVideo.setMax(duration);
            handler.sendEmptyMessage(PROGRESS);
            videoview.start();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this, "播放出错", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(SystemVideoPlayer.this, "播放完毕", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if(handler!=null) {
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
        super.onDestroy();
    }

    private class MyBatterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level=intent.getIntExtra("level",0);
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if(level<=0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if(level<=10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if(level<=20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if(level<=40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if(level<=60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level<=80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if(level<=100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else{
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
        
    }
}
