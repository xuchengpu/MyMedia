package com.xuchengpu.myproject.myproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.activity.SystemVideoPlayer;
import com.xuchengpu.myproject.myproject.adapter.NetVideoAdapter;
import com.xuchengpu.myproject.myproject.base.BaseFragment;
import com.xuchengpu.myproject.myproject.bean.MediaItem;
import com.xuchengpu.myproject.myproject.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by 许成谱 on 2017/1/6.
 */

public class NetVideoFragment extends BaseFragment {
    private ArrayList<MediaItem> mediaItems;

    @ViewInject(R.id.listview)
    private ListView listview;

    @ViewInject(R.id.tv_no_media)
    private TextView tv_no_media;
    private NetVideoAdapter adapter;


    @Override
    public View initView() {
        View view =View.inflate(context, R.layout.fragment_net_video,null);
        x.view().inject(NetVideoFragment.this,view);
        listview.setOnItemClickListener(new myOnItemClickListener());
        return view;
    }
    private class myOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem media=mediaItems.get(position);
            Intent intent=new Intent(context,SystemVideoPlayer.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            startActivity(intent);

        }
    }


    @Override
    public void initData() {
        super.initData();
        getDataFromNet();

    }

    private void getDataFromNet() {
        RequestParams params=new RequestParams(Constant.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("tag","xUtils3联网请求成功=="+result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG","xUtils3请求失败了=="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String json) {
        mediaItems =parsedJson(json);
        if(mediaItems != null && mediaItems.size() >0){
            //有数据
            tv_no_media.setVisibility(View.GONE);
            adapter = new NetVideoAdapter(context,mediaItems);
            //设置适配器
            listview.setAdapter(adapter);

        }else{
            tv_no_media.setVisibility(View.VISIBLE);
        }

    }

    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems=new ArrayList<>();

        try {
            JSONObject jsonObject=new JSONObject(json);
            JSONArray jsonArray=jsonObject.getJSONArray("trailers");

            for(int i = 0; i < jsonArray.length(); i++) {
                MediaItem mediaItem=new MediaItem();
                mediaItems.add(mediaItem);
                JSONObject jsonObjectItem= (JSONObject) jsonArray.get(i);
                String name=jsonObjectItem.optString("movieName");
                mediaItem.setName(name);
                String desc = jsonObjectItem.optString("videoTitle");
                mediaItem.setDesc(desc);
                String url = jsonObjectItem.optString("url");
                mediaItem.setData(url);
                String hightUrl = jsonObjectItem.optString("hightUrl");
                mediaItem.setHeightUrl(hightUrl);
                String coverImg = jsonObjectItem.optString("coverImg");
                mediaItem.setImageUrl(coverImg);
                int videoLength = jsonObjectItem.optInt("videoLength");
                mediaItem.setDuration(videoLength);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return mediaItems;
    }

    @Override
    public void refresh() {
        super.refresh();

    }
}
