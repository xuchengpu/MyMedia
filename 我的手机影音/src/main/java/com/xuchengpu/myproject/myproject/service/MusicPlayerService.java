package com.xuchengpu.myproject.myproject.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.xuchengpu.myproject.IMusicPlayerService;
import com.xuchengpu.myproject.myproject.bean.MediaItem;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 许成谱 on 2017/1/11.
 */

public class MusicPlayerService extends Service {
    private Uri uri;
    private ArrayList<MediaItem> audio;
    private int position;
    IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);

        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            service.setPlayMode(mode);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int postion) throws RemoteException {
            service.seekTo(postion);
        }
    };
    private boolean isLoaded = false;
    private MediaItem mediaitem;
    private MediaPlayer mediaPlayer;
    public static final String OPEN_COMPLETE = "open_complete";


    @Override
    public void onCreate() {
        super.onCreate();
        getAudioData();
        Log.e("tag", "oncreate");

    }

    private void getAudioData() {
        new Thread() {
            public void run() {
                audio = new ArrayList<MediaItem>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaitem = new MediaItem();
                        String name = cursor.getString(0);
                        long size = cursor.getLong(1);
                        long duration = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);
                        mediaitem.setName(name);
                        mediaitem.setArtist(artist);
                        mediaitem.setData(data);
                        mediaitem.setDuration(duration);
                        mediaitem.setSize(size);
                        audio.add(mediaitem);
                    }
                    cursor.close();
                }
                //音频加载完成
                isLoaded = true;
            }
        }.start();

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("tag", "onbind");
        return stub;
    }

    /**
     * 根据位置打开一个音频并且播放
     *
     * @param position
     */
    void openAudio(int position) {
        if (audio != null && audio.size() > 0) {
            mediaitem = audio.get(position);
            this.position = position;
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
                mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnErrorListener(new MyOnErrorListener());
            mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
            mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
            try {
                mediaPlayer.setDataSource(mediaitem.getData());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!isLoaded) {
            Toast.makeText(this, "没有加载完成", Toast.LENGTH_SHORT).show();
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            notifyChange(OPEN_COMPLETE);
            start();

        }
    }

    private void notifyChange(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);

    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();

        }
    }


    /**
     * 开始播放音频
     */
    void start() {
        mediaPlayer.start();
    }


    /**
     * 暂停
     */
    void pause() {
        mediaPlayer.pause();
    }


    /**
     * 得到歌曲的名称
     */
    String getAudioName() {
        if(mediaitem!=null) {
           return mediaitem.getName();
        }
        return "";
    }


    /**
     * 得到歌曲演唱者的名字
     */
    String getArtistName() {
        if(mediaitem!=null) {
            return mediaitem.getArtist();
        }
        return "";
    }

    ;

    /**
     * 得到歌曲的当前播放进度
     */
    int getCurrentPosition() {
        if(mediaPlayer!=null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    ;

    /**
     * 得到歌曲的当前总进度
     */
    int getDuration() {
        if(mediaPlayer!=null) {
            return mediaPlayer.getDuration();
        }
        return 0;

    }

    ;

    /**
     * 播放下一首歌曲
     */
    void next() {
    }

    ;

    /**
     * 播放上一首歌曲
     */
    void pre() {
    }

    ;

    /**
     * 得到播放模式
     */
    int getPlayMode() {
        return 0;
    }

    ;

    /**
     * 设置播放模式
     */
    void setPlayMode(int mode) {

    }


    /**
     * 是否在播放
     */
    boolean isPlaying() {

        return mediaPlayer.isPlaying();
    }


    /**
     * 根据传入的位置，播放
     */
    void seekTo(int postion) {
        mediaPlayer.seekTo(postion);

    }


}
