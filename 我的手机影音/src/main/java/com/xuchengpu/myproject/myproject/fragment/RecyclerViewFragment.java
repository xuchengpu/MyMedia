package com.xuchengpu.myproject.myproject.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.adapter.RecyclerViewAdapter;
import com.xuchengpu.myproject.myproject.base.BaseFragment;
import com.xuchengpu.myproject.myproject.bean.NetAudioBean;
import com.xuchengpu.myproject.myproject.utils.Constant;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 许成谱 on 2017/1/6.
 */

public class RecyclerViewFragment extends BaseFragment {
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.progressbar)
    ProgressBar progressbar;
    @InjectView(R.id.tv_nomedia)
    TextView tv_nomedia;
    private ArrayList<String> datas;
    private RecyclerViewAdapter MyAdapter;
    private List<NetAudioBean.ListBean> listDatas;


    @Override
    public View initView() {
       View view=View.inflate(context, R.layout.fragment_recycler_view,null);
        ButterKnife.inject(this,view);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params=new RequestParams(Constant.NET_AUDIO);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "网络音乐请求成功" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "网络音乐请求失败" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String result) {
        NetAudioBean netAudioBean=new Gson().fromJson(result,NetAudioBean.class);
        listDatas=netAudioBean.getList();
        Log.e("TAG","解决成功=="+listDatas.get(0).getText());
        if(listDatas!=null&&listDatas.size()>0) {
            tv_nomedia.setVisibility(View.GONE);
            MyAdapter=new RecyclerViewAdapter(context,listDatas);
            recyclerview.setAdapter(MyAdapter);
            recyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        }else{
            tv_nomedia.setVisibility(View.VISIBLE);
        }
        progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
}
