package com.xuchengpu.myproject.myproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.base.BaseFragment;
import com.xuchengpu.myproject.myproject.fragment.LocalAudioFragment;
import com.xuchengpu.myproject.myproject.fragment.LocalVideoFragment;
import com.xuchengpu.myproject.myproject.fragment.NetAudioFragment;
import com.xuchengpu.myproject.myproject.fragment.NetVideoFragment;

import java.util.ArrayList;
import java.util.List;

import static com.xuchengpu.myproject.R.id.rb_local_video;

/**
 * Created by 许成谱 on 2017/1/6.
 */
public class MainActivity extends AppCompatActivity {
    private List<BaseFragment> fragments;
    private RadioGroup rg_main;
    private Fragment tempFragment;
    private int position;
    private FrameLayout fl_main_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_mian);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);

        initFragment();
        initListener();

    }

    private void initListener() {

        //rg_main.check(rb_local_video);

        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case rb_local_video:
                        position = 0;

                        break;
                    case R.id.rb_local_audio:
                        position = 1;

                        break;
                    case R.id.rb_net_audio:
                        position = 2;

                        break;
                    case R.id.rb_net_video:
                        position = 3;

                        break;
                }
                Fragment currentFragment = fragments.get(position);
                toCurrentFragment(currentFragment);
            }
        });
        rg_main.check(rb_local_video);//会调用监听方法 才会显示本地视频四个字 所以只能放后边


    }

    private void toCurrentFragment(Fragment currentFragment) {

        if (tempFragment != currentFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (tempFragment != null) {
                ft.hide(tempFragment);
                if (!currentFragment.isAdded()) {
                    ft.add(R.id.fl_main_content, currentFragment);
                }
            } else {
                ft.add(R.id.fl_main_content, currentFragment);
            }
            ft.show(currentFragment);
            ft.commit();
            tempFragment = currentFragment;
        }


    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoFragment());
        fragments.add(new LocalAudioFragment());
        fragments.add(new NetAudioFragment());
        fragments.add(new NetVideoFragment());
    }

}
