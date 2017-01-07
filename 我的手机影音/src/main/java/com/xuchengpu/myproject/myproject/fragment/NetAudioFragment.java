package com.xuchengpu.myproject.myproject.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xuchengpu.myproject.myproject.base.BaseFragment;

/**
 * Created by 许成谱 on 2017/1/6.
 */

public class NetAudioFragment extends BaseFragment {
    public TextView tv;
    @Override
    public View initView() {
         tv=new TextView(context);
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(25);
        return tv;
    }

    @Override
    public void initData() {
        super.initData();
        tv.setText("网络音乐");

    }

    @Override
    public void refresh() {
        super.refresh();
        tv.setText("网络音乐刷新");
    }
}
