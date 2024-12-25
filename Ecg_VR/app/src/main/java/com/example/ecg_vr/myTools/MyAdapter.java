package com.example.ecg_vr.myTools;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecg_vr.MainActivity;
import com.example.ecg_vr.R;
import com.example.ecg_vr.UserActivity;

import java.io.IOException;

public class MyAdapter extends BaseAdapter {
    private MainActivity context;
    private ListBean[] mDatas;
    public boolean isVibrating = true,isMediaing = true;
    private VibrateUtil vi = new VibrateUtil();

    public MyAdapter(MainActivity context, ListBean[] mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_item,null);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_text);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_image);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(mDatas[position].getTitle());
        holder.iv.setImageResource(mDatas[position].getResource());

        /**
         * 菜单被单击
         */
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,mDatas[position].getTitle()+"被点击!!!",Toast.LENGTH_SHORT).show();
                switch (mDatas[position].getTitle()){
                    case "刷新":{
                        if(context.jud==1){
                            context.waveUtil.stop();
                        }
                        WaveUtil.i=0;

                        context.toRefresh();

                        break;
                    }

                    case "用户":{
                        //MainActivity mainActivity = new MainActivity();
                        //mainActivity.change2User();
                        //Intent intent = new Intent(parent.getContext(), UserActivity.class);
                        //intent.putExtra("account",context.account);
                        //parent.getContext().startActivity(intent);
                        context.toUser();

                        break;
                    }
                    case"蓝牙":{
                        context.toBt();

                        break;
                    }

                    case"vibrate":{

                        if(!context.vibrateJud){
                            break;
                        }

                        if(!vi.hasVibrator(context)){
                        Toast.makeText(context,"手机不支持震动",Toast.LENGTH_LONG).show();
                        break;
                    }
                        if (isVibrating) {//防止多次关闭抛出异常，这里加个参数判断一下
                            if(context.isShort){
                            vi.vibrate(context, new long[]{100, 200, 100, 200}, 0);
                            }else{
                                vi.vibrate(context, new long[]{100, 2000, 100, 2000}, 0);
                            }
                            isVibrating = false;

                        }else{

                            vi.vibrateCancle(context);
                            isVibrating = true;
                        }
                        break;
                    }

                    case"media":{
                        if(!context.alert.toString().equals("null")){

                            if(isMediaing){
                                //开启
                                MediaUtil.playRing(context,context.alert);
                                //MediaUtil.playRing(context);
                                isMediaing = false;
                            }else {
                                //关闭
                                MediaUtil.stopRing();
                                isMediaing = true;
                            }
                        }

                        break;
                    }

                    default:
                        break;
                }

            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView tv;
        ImageView iv;
    }

}
