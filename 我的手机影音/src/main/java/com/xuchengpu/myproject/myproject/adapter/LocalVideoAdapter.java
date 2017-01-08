package com.xuchengpu.myproject.myproject.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuchengpu.myproject.R;
import com.xuchengpu.myproject.myproject.bean.MediaItem;
import com.xuchengpu.myproject.myproject.utils.Utils;

import java.util.List;

/**
 * Created by 许成谱 on 2017/1/8.
 */

public class LocalVideoAdapter extends BaseAdapter {
    private Context context;
    private List<MediaItem> datas;
    private Utils utils=new Utils();

    public LocalVideoAdapter(Context context, List<MediaItem> datas) {
        this.context = context;
        this.datas = datas;
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

        holder.iv_icon.setImageResource(R.drawable.video_default_icon);
        holder.tv_name.setText(media.getName());
        holder.tv_duration.setText(utils.stringForTime((int) media.getDuration()));
        holder.tv_size.setText(Formatter.formatFileSize(context,media.getSize()));

        return convertView;
    }
    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;

    }
}
