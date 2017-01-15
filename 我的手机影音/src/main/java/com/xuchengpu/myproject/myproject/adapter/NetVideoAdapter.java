package com.xuchengpu.myproject.myproject.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.bean.MediaItem;
import com.xuchengpu.myproject.myproject.utils.Utils;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by 许成谱 on 2017/1/8.
 */

public class NetVideoAdapter extends BaseAdapter {
    private ImageOptions imageOptions;
    private Context context;
    private ArrayList<MediaItem> datas;
    private Utils utils=new Utils();


    public NetVideoAdapter(Context context, ArrayList<MediaItem> datas) {
        this.context = context;
        this.datas = datas;

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.video_default)//加载过程中的默认图片
                .setFailureDrawableId(R.drawable.video_default)//就挨着出错的图片
                .build();

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            convertView=View.inflate(context, R.layout.item_local_video,null);
            holder=new ViewHolder();
            holder.iv_icon= (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_duration= (TextView) convertView.findViewById(R.id.tv_duration);
            holder.tv_size= (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        MediaItem media=datas.get(position);


        holder.tv_name.setText(media.getName());
        holder.tv_duration.setText(media.getDuration()+"秒");
        holder.tv_size.setText(media.getDesc());

        x.image().bind(holder.iv_icon,media.getImageUrl(),imageOptions);


        return convertView;
    }
    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;

    }
}
