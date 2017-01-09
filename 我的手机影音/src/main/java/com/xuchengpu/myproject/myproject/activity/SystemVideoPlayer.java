package com.xuchengpu.myproject.myproject.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.xuchengpu.myproject.R;

public class SystemVideoPlayer extends Activity {
    private VideoView videoview;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        videoview = (VideoView)findViewById(R.id.videoview);
        getData();
        //设置视频加载的监听        setData();
        setLinstener();


    }

    private void setLinstener() {
        videoview.setOnCompletionListener(new MyOnCompletionListener());
        videoview.setOnErrorListener(new MyOnErrorListener());
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        videoview.setMediaController(new MediaController(this));
    }

    private void setData() {
        videoview.setVideoURI(uri);
    }

    private void getData() {
        uri=getIntent().getData();
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {            Toast.makeText(SystemVideoPlayer.this,"播放出错",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(SystemVideoPlayer.this,"播放完毕",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
