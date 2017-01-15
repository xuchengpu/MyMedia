package com.xuchengpu.myproject.myproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xuchengpu.myproject.myproject.bean.LyricBean;
import com.xuchengpu.myproject.myproject.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by 许成谱 on 2017/1/14.
 */

public class LyricShow extends TextView {
    private final Context context;
    private ArrayList<LyricBean> lyrics;
    private LyricBean lyric;
    private int index;
    private Paint paint;
    private Paint nopaint;
    private int height;
    private int width;
    private float textHeight;
    private float currentposition;

    public LyricShow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        textHeight = DensityUtil.dip2px(this.context,20);
        lyrics=new ArrayList();
       /* for (int i=0;i<500;i++){
            lyric=new LyricBean();
            lyric.setContent("我爱记歌词"+i);
            lyric.setTimepoint(1000*i);
            lyric.setSleeptime(2000);
            lyrics.add(lyric);
        }*/
        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(16);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        nopaint=new Paint();
        nopaint.setColor(Color.RED);
        nopaint.setTextSize(16);
        nopaint.setAntiAlias(true);
        nopaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lyrics!=null&&lyrics.size()>0) {

            String content=lyrics.get(index).getContent();
            canvas.drawText(content,width/2,height/2,nopaint);
            float preheight=height/2;
            for(int i = index-1; i >0 ; i--) {
              String precontent=lyrics.get(i).getContent();
                preheight=preheight-textHeight;
                if(preheight<0) {
                    break;
                }
                canvas.drawText(precontent,width/2,preheight,paint);
            }

            float nextheight=height/2;
            for(int i = index+1; i <lyrics.size() ; i++) {
              String nextcontent=lyrics.get(i).getContent();
                nextheight=nextheight+textHeight;
                if(nextheight>height) {
                    break;
                }
                canvas.drawText(nextcontent,width/2,nextheight,paint);
            }

        }else{
            canvas.drawText("没有找到歌词……",width/2,height/2,paint);
        }
    }

    public void setNextLyric(int currentposition) {
        this.currentposition=currentposition;
        if(lyrics!=null&&lyrics.size()>0) {
            for(int i = 1; i < lyrics.size(); i++) {
              if(lyrics.get(i).getTimepoint()>currentposition) {

                  if(lyrics.get(i-1).getTimepoint()<=currentposition) {
                      index=i-1;
                  }
              }else{
                  index=lyrics.size()-1;
              }
            }
        }
        invalidate();
    }
    public void setLyrics(ArrayList<LyricBean> lyricBeens) {
        this.lyrics = lyricBeens;
    }
}
