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
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.bean.MediaItem;
import com.xuchengpu.myproject.myproject.utils.Utils;
import com.xuchengpu.myproject.myproject.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ArrayList<MediaItem> video;
    private static boolean isShowVideoController = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    int currentPosition = videoview.getCurrentPosition();
                    tvCurrenttime.setText(utils.stringForTime(currentPosition));
                    seekbarVideo.setProgress(currentPosition);
                    tvSystetime.setText(getSystemTime());
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDECONTROLLER:
                    if (isShowVideoController) {
                        hideVideoController();
                    }

                    break;
            }
        }
    };
    private int position;
    private static final int HIDECONTROLLER = 1;
    private int screenWidth;
    private int screeHeight;
    private boolean isFullScreen = false;
    private  static  final  int SCREEN_FULL=0;
    private static  final int SCREEN_DEFULT=1;
    private int videoWidth=0;
    private int videoHeight=0;

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String systemTime = format.format(new Date());
        return systemTime;
    }

    private final static int PROGRESS = 0;
    private MyBatterReceiver receive;
    private GestureDetector detector;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-09 18:25:50 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystetime = (TextView) findViewById(R.id.tv_systetime);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwichePlayer = (Button) findViewById(R.id.btn_swiche_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrenttime = (TextView) findViewById(R.id.tv_currenttime);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwichScreen = (Button) findViewById(R.id.btn_swich_screen);

        btnVoice.setOnClickListener(this);
        btnSwichePlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwichScreen.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-01-09 18:25:50 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
        } else if (v == btnSwichePlayer) {
            // Handle clicks for btnSwichePlayer
        } else if (v == btnExit) {
            finish();
            // Handle clicks for btnExit
        } else if (v == btnPre) {
            setPreVideo();
            // Handle clicks for btnPre
        } else if (v == btnStartPause) {
            // Handle clicks for btnStartPause
            startAndPause();
        } else if (v == btnNext) {
            // Handle clicks for btnNext
            setNextVideo();
        } else if (v == btnSwichScreen) {
            if(isFullScreen) {
                setVideoType(SCREEN_DEFULT);
            }else {
                setVideoType(SCREEN_FULL);
            }
            // Handle clicks for btnSwichScreen
        }
    }


    private void startAndPause() {
        if (videoview.isPlaying()) {
            videoview.pause();
            btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
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
        utils = new Utils();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receive = new MyBatterReceiver();
        registerReceiver(receive, filter);
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                startAndPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(isFullScreen) {
                    setVideoType(SCREEN_DEFULT);
                }else {
                    setVideoType(SCREEN_FULL);
                }

                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowVideoController) {
                    hideVideoController();
                    handler.removeMessages(HIDECONTROLLER);
                } else {
                    showVideoController();
                    handler.removeMessages(HIDECONTROLLER);
                    handler.sendEmptyMessageDelayed(HIDECONTROLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        //得到屏幕的宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        //得到屏幕参数类
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        //屏幕的宽和高
        screenWidth = outMetrics.widthPixels;
        screeHeight = outMetrics.heightPixels;
    }

    private void showVideoController() {
        isShowVideoController = true;
        llTop.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);
    }

    private void hideVideoController() {
        isShowVideoController = false;
        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
    }

    private void setLinstener() {
        videoview.setOnCompletionListener(new MyOnCompletionListener());
        videoview.setOnErrorListener(new MyOnErrorListener());
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        seekbarVideo.setOnSeekBarChangeListener(this);
        // videoview.setMediaController(new MediaController(this));
    }

    private void setData() {
        if (video != null && video.size() > 0) {
            MediaItem mediaItem = video.get(position);
            videoview.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
        } else {
            videoview.setVideoURI(uri);
            tvName.setText(uri.toString());
        }
        checkButtonStatus();
    }

    private void getData() {
        uri = getIntent().getData();
        video = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            seekBar.setProgress(progress);
            videoview.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeMessages(HIDECONTROLLER);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.sendEmptyMessageDelayed(HIDECONTROLLER, 4000);

    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth=mp.getVideoWidth();
            videoHeight=mp.getVideoHeight();
            setVideoType(SCREEN_DEFULT);

            int duration = videoview.getDuration();
            tvDuration.setText(utils.stringForTime(duration));
            seekbarVideo.setMax(duration);
            handler.sendEmptyMessage(PROGRESS);
            videoview.start();



        }
    }

    private void setVideoType(int  type) {
        switch (type) {
            case SCREEN_FULL:
                videoview.setVideoSize(screenWidth,screeHeight);
                isFullScreen=true;
                btnSwichScreen.setBackgroundResource(R.drawable.btn_screen_default_selector);
                break;
            case SCREEN_DEFULT:
                isFullScreen=false;
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                int width = screenWidth;
                int height = screeHeight;

                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width,height);

                btnSwichScreen.setBackgroundResource(R.drawable.btn_screen_full_selector);

                break;
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
           /* Toast.makeText(SystemVideoPlayer.this, "播放完毕", Toast.LENGTH_SHORT).show();
            finish();*/
            setNextVideo();
        }
    }

    private void setPreVideo() {
        if (video != null && video.size() > 0) {
            position--;
            if (position > 0) {
                MediaItem media = video.get(position);
                videoview.setVideoPath(media.getData());
                tvName.setText(media.getName());
                checkButtonStatus();

            } else {
                position = 0;
                finish();
            }

        } else if (uri != null) {
            finish();
        }
    }

    private void setNextVideo() {
        if (video != null && video.size() > 0) {
            position++;
            if (position < video.size()) {
                MediaItem media = video.get(position);
                videoview.setVideoPath(media.getData());
                tvName.setText(media.getName());
                checkButtonStatus();

            } else {
                position = video.size() - 1;
                finish();
            }

        } else if (uri != null) {
            finish();
        }
    }

    private void checkButtonStatus() {
        setButtonEnable(true);
        if (video != null && video.size() > 0) {
            if (position == 0) {
                btnPre.setEnabled(false);
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            }
            if (position == video.size() - 1) {
                btnNext.setEnabled(false);
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
            }

        } else if (uri != null) {
            setButtonEnable(false);
        }

    }

    private void setButtonEnable(boolean b) {
        if (b) {
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPre.setEnabled(b);
        btnNext.setEnabled(b);

    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }

    private class MyBatterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
