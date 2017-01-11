package com.xuchengpu.myproject.myproject.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.activity.SystemAudioPlayer;
import com.xuchengpu.myproject.myproject.adapter.LocalVideoAdapter;
import com.xuchengpu.myproject.myproject.base.BaseFragment;
import com.xuchengpu.myproject.myproject.bean.MediaItem;

import java.util.ArrayList;

/**
 * Created by 许成谱 on 2017/1/6.
 */

public class LocalAudioFragment extends BaseFragment {
    private ListView listview;
    private TextView tv_no_media;
    private ArrayList<MediaItem> audio;
    private LocalVideoAdapter adapter;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(audio !=null&& audio.size()>0) {

                adapter=new LocalVideoAdapter(context, audio,false);
                listview.setAdapter(adapter);
                tv_no_media.setVisibility(View.GONE);
            }else{
                tv_no_media.setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    public View initView() {
        View view=View.inflate(context, R.layout.fragment_local_video,null);
        listview= (ListView) view.findViewById(R.id.listview);
        tv_no_media= (TextView) view.findViewById(R.id.tv_no_media);
        listview.setOnItemClickListener(new myOnItemClickListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        getLocalAudioData();
    }


    private void getLocalAudioData() {
        new Thread(){
            public void run(){
                audio =new ArrayList<MediaItem>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor=resolver.query(uri,objs,null,null,null);
                if(cursor!=null) {
                    while (cursor.moveToNext()){
                        MediaItem mediaitem=new MediaItem();
                        String name=cursor.getString(0);
                        long size=cursor.getLong(1);
                        long duration=cursor.getLong(2);
                        String data=cursor.getString(3);
                        String artist=cursor.getString(4);
                        mediaitem.setName(name);
                        mediaitem.setArtist(artist);
                        mediaitem.setData(data);
                        mediaitem.setDuration(duration);
                        mediaitem.setSize(size);
                        audio.add(mediaitem);
                    }
                    cursor.close();

                }
                handler.sendEmptyMessage(0);


            }
        }.start();
    }

    private class myOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem media= audio.get(position);
            //1.调起系统的播放器播放视频--隐式意图
//            Intent intent=new Intent();
//            intent.setDataAndType(Uri.parse(media.getData()),"audio/*");
//            startActivity(intent);
            //2.调起自定义播放器
          /*  Intent intent=new Intent(context,SystemVideoPlayer.class);
            intent.setDataAndType(Uri.parse(media.getData()),"audio*//*");
            startActivity(intent);*/
            Intent intent=new Intent(context,SystemAudioPlayer.class);
           /* Bundle bundle=new Bundle();
            bundle.putSerializable("audiolist", audio);
            //Log.e("tag","audio=="+audio);
            intent.putExtras(bundle);*/
            intent.putExtra("position",position);
            startActivity(intent);

        }
    }
}
