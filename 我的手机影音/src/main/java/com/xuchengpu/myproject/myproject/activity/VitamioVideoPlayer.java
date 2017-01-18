package com.xuchengpu.myproject.myproject.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.bean.MediaItem;
import com.xuchengpu.myproject.myproject.utils.Utils;
import com.xuchengpu.myproject.myproject.view.VitamioVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class VitamioVideoPlayer extends Activity implements View.OnClickListener {
    private VitamioVideoView videoview;
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
    private LinearLayout ll_loading;
    private TextView tv_loading;
    private LinearLayout ll_buffer;
    private TextView tv_buffer;



    private Utils utils;
    private ArrayList<MediaItem> video;
    private AudioManager am;
    private static boolean isShowVideoController = true;
    private int preposition;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NET_SPEED:

                    String netSpeed=utils.showNetSpeed(VitamioVideoPlayer.this);
                    tv_loading.setText("正在加载中……"+netSpeed);
                    tv_buffer.setText("正在缓冲中……"+netSpeed);
                    handler.removeMessages(SHOW_NET_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_NET_SPEED,1000);
                    
                    break;
                case PROGRESS:
                    int currentPosition = (int) videoview.getCurrentPosition();
                    tvCurrenttime.setText(utils.stringForTime(currentPosition));
                    seekbarVideo.setProgress(currentPosition);
                    tvSystetime.setText(getSystemTime());

                    if(isUrl) {
                        int percentage = videoview.getBufferPercentage();
                        int secondprogress=percentage*seekbarVideo.getMax()/100;
                        seekbarVideo.setSecondaryProgress(secondprogress);
                    }

                    int buffer=currentPosition-preposition;
                    if(isUrl) {
                        if(buffer<500&&videoview.isPlaying()) {
                            ll_buffer.setVisibility(View.VISIBLE);
                        }else{
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }
                    preposition=currentPosition;

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
    private static final int PROGRESS = 0;
    private static final int HIDECONTROLLER = 1;
    private static final int SHOW_NET_SPEED = 2;

    private int screenWidth;
    private int screeHeight;
    private boolean isFullScreen = false;

    private  static  final  int SCREEN_FULL=0;
    private static  final int SCREEN_DEFULT=1;

    private int videoWidth=0;
    private int videoHeight=0;
    private int currentVolume;
    private int maxVollume;
    private boolean isMute=false;

    private MyBatterReceiver receive;
    private GestureDetector detector;
    private boolean isUrl;

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String systemTime = format.format(new Date());
        return systemTime;
    }
    private void findViews() {
        videoview = (VitamioVideoView) findViewById(R.id.VitamioVideoView);
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
        ll_loading = (LinearLayout)findViewById(R.id.ll_loading);
        tv_loading = (TextView)findViewById(R.id.tv_loading);
        ll_buffer = (LinearLayout)findViewById(R.id.ll_buffer);
        tv_buffer = (TextView)findViewById(R.id.tv_buffer);

        btnVoice.setOnClickListener(this);
        btnSwichePlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwichScreen.setOnClickListener(this);

        am= (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVollume=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbarVoice.setMax(maxVollume);
        seekbarVoice.setProgress(currentVolume);

        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);

        handler.sendEmptyMessage(SHOW_NET_SPEED);

    }
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
            isMute=!isMute;
            updateVoice(currentVolume);
        } else if (v == btnSwichePlayer) {
            // Handle clicks for btnSwichePlayer
            showSwichPlayerDialog();
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
    private void showSwichPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("当前播放使用万能播放器播放，当播放出现有视频有色块，播放效果不理想，请切换系统播放器播放");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemVideoPlayer();
            }
        });
        builder.show();
    }
    private void startSystemVideoPlayer() {
        if(videoview != null){
            videoview.stopPlayback();
        }

        Intent intent = new Intent(this,SystemVideoPlayer.class);

        if(video != null && video.size() >0){

            Bundle bundle = new Bundle();
            //列表数据
            bundle.putSerializable("videolist",video);
            intent.putExtras(bundle);
            //传递点击的位置
            intent.putExtra("position",position);

        }else if(uri != null){
            intent.setDataAndType(uri,"video/*");
        }

        startActivity(intent);

        finish();
    }

    private void updateVoice(int progress) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            //第一个参数：声音的类型
            //第二个参数：声音的值：0~15
            //第三个参数：1，显示系统调声音的；0，不显示
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
        }

        currentVolume = progress;

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
        setContentView(R.layout.activity_vitamio_video_player);
        Vitamio.isInitialized(this);
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
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        });
        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVoiceProgress(progress);
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
        });
        // videoview.setMediaController(new MediaController(this));
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what){
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            ll_buffer.setVisibility(View.VISIBLE);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            ll_buffer.setVisibility(View.GONE);
                            break;
                    }
                    return true;
                }
            });
        }*/
    }

    private void updateVoiceProgress(int progress) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
       // Log.e("tag","AudioManager.STREAM_MUSIC="+AudioManager.STREAM_MUSIC);
        //Log.e("tag","progress="+progress);
        seekbarVoice.setProgress(progress);
        if(progress<=0) {
            isMute=true;
        }else{
            isMute=false;
        }
        currentVolume=progress;

    }

    private void setData() {
        if (video != null && video.size() > 0) {
            MediaItem mediaItem = video.get(position);
            videoview.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            isUrl=utils.isUrl(mediaItem.getData());
        } else if (uri != null) {
            videoview.setVideoURI(uri);
            tvName.setText(uri.toString());
            isUrl=utils.isUrl(uri.toString());
        }
        checkButtonStatus();
    }

    private void getData() {
        uri = getIntent().getData();
        video = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        Log.e("tag","video="+video);
        position = getIntent().getIntExtra("position", 0);
    }



    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth=mp.getVideoWidth();
            videoHeight=mp.getVideoHeight();
            setVideoType(SCREEN_DEFULT);

            mp.setPlaybackSpeed(1.0f);

            seekbarVideo.setSecondaryProgress(0);
            ll_loading.setVisibility(View.GONE);


            int duration = (int) videoview.getDuration();
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
            showErrorDialog();


            return true;
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("播放器播放出错了，请检查视频是否损坏，或者网络中断");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
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
                ll_loading.setVisibility(View.VISIBLE);
                isUrl=utils.isUrl(media.getData());

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
                ll_loading.setVisibility(View.VISIBLE);
                isUrl=utils.isUrl(media.getData());

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
    private float starty;
    private int mVol;
    private  int touchRang=0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                starty=event.getY();
                touchRang=Math.min(screeHeight,screenWidth);
                mVol=am.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(HIDECONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE:
                float endy=event.getY();
                float distancey=starty-endy;
                float delta=distancey/touchRang*maxVollume;
                int volue = (int) Math.min(Math.max(mVol + delta,0),maxVollume);
                if(delta!=0) {
                    updateVoiceProgress(volue);
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDECONTROLLER,4000);
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case  KeyEvent.KEYCODE_VOLUME_DOWN:
                currentVolume--;
                updateVoiceProgress(currentVolume);
                handler.removeMessages(HIDECONTROLLER);
                handler.sendEmptyMessageDelayed(HIDECONTROLLER,4000);
                break;
            case  KeyEvent.KEYCODE_VOLUME_UP:
                currentVolume++;
                updateVoiceProgress(currentVolume);
                handler.removeMessages(HIDECONTROLLER);
                handler.sendEmptyMessageDelayed(HIDECONTROLLER,4000);

                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
