package com.xuchengpu.startallvideoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openAll(View v) {
//1.调起系统的播放器播放视频--隐式意图
            Intent intent=new Intent();
            intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2016/12/14/mp4/161214234616222245.mp4"),"video/*");
            startActivity(intent);
    }
}
