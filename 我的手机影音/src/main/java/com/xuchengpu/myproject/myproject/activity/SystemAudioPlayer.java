package com.xuchengpu.myproject.myproject.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xuchengpu.myproject.IMusicPlayerService;
import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.bean.MediaItem;
import com.xuchengpu.myproject.myproject.service.MusicPlayerService;
import com.xuchengpu.myproject.myproject.utils.CacheUtils;
import com.xuchengpu.myproject.myproject.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 *
 */
public class SystemAudioPlayer extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnSwichLyric;
    private IMusicPlayerService service;
    private int position;
    private  int playMode=0;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if (service != null) {
                if(!notification) {
                    try {
                        service.openAudio(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else{
                    showViewData(null);
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private MyReceiver receiver;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    try {
                        int currentposition = service.getCurrentPosition();
                        tvTime.setText(utils.stringForTime(currentposition) + "/" + utils.stringForTime(service.getDuration()));
                        seekbarAudio.setProgress(currentposition);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }

        }
    };
    private static final int PROGRESS = 1;
    private Utils utils;
    private boolean notification;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-11 18:54:55 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_audio_player);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnSwichLyric = (Button) findViewById(R.id.btn_swich_lyric);

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnSwichLyric.setOnClickListener(this);
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();

    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-01-11 18:54:55 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */



    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            // Handle clicks for btnAudioPlaymode
            changePlayMode();
        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnAudioStartPause) {
            try {
                if (service.isPlaying()) {
                    service.pause();
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                } else {
                    service.start();
                    //按钮状态-设置暂停
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnAudioStartPause
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnSwichLyric) {

            // Handle clicks for btnSwichLyric
        }
    }

    private void changePlayMode() {
        try {
            playMode=service.getPlayMode();
            if (playMode == MusicPlayerService.REPEAT_NOMAL) {
                playMode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
                playMode = MusicPlayerService.REPEAT_ALL;
            } else if (playMode == MusicPlayerService.REPEAT_ALL) {
                playMode = MusicPlayerService.REPEAT_NOMAL;
            }else {
                playMode = MusicPlayerService.REPEAT_ALL;
            }
            service.setPlayMode(playMode);
            checkButtonStatu();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void checkButtonStatu() {

        try {
            playMode = service.getPlayMode();

            if (playMode == MusicPlayerService.REPEAT_NOMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            } else if (playMode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        startAndBindServide();

    }

    private void initData() {
        utils = new Utils();
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPEN_COMPLETE);
        registerReceiver(receiver, intentFilter);
        EventBus.getDefault().register(this);

    }

    private void getData() {
        notification=getIntent().getBooleanExtra("notification",false);
        if(!notification) {
            position = getIntent().getIntExtra("position", 0);
        }
        int palymode=0;
        palymode= CacheUtils.getPlayMode(this,"playmode");
        if(palymode!=-1) {
            playMode=palymode;
        }
    }

    private void startAndBindServide() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }


    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicPlayerService.OPEN_COMPLETE.equals(intent.getAction())) {

               // showViewData();
            }
        }
    }
    @Subscribe(threadMode=ThreadMode.MAIN)
    public  void showViewData(MediaItem mediaitem) {
        try {
            tvName.setText(service.getAudioName());
            tvArtist.setText(service.getArtistName());
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);


            checkButtonStatu();
            handler.sendEmptyMessage(PROGRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        if(receiver!=null) {
            unregisterReceiver(receiver);
            receiver=null;
        }
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
