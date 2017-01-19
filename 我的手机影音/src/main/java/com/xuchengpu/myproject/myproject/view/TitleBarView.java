package com.xuchengpu.myproject.myproject.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.activity.SearchActivity;

/**
 * Created by 许成谱 on 2017/1/6.
 */

public class TitleBarView extends LinearLayout implements View.OnClickListener {
    private TextView tv_search;
    private RelativeLayout rl_game;
    private ImageView iv_record;
    private  Context mcontext;

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mcontext=context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rl_game= (RelativeLayout) findViewById(R.id.rl_game);
        iv_record = (ImageView)findViewById(R.id.iv_record);
        tv_search = (TextView)findViewById(R.id.tv_search);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
        tv_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.rl_game:
                Toast.makeText(mcontext,"游戏",Toast.LENGTH_SHORT).show();
                break;
            case  R.id.iv_record:
                Toast.makeText(mcontext,"记录",Toast.LENGTH_SHORT).show();
                break;
            case  R.id.tv_search:
                Toast.makeText(mcontext,"搜索",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mcontext, SearchActivity.class);
                mcontext.startActivity(intent);
                break;
        }
    }
}
