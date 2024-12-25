
package com.example.ecg_vr.myTools;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class MediaUtil {

    private static MediaPlayer mMediaPlayer;

    //开始播放
    public static void playRing(Context context,Uri myUri){
        try {
            if(myUri==null){
                //用于获取手机默认铃声的Uri
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(context, alert);

                Toast.makeText(context,""+myUri,Toast.LENGTH_SHORT).show();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                //告诉mediaPlayer播放的是铃声流
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }else{

                mMediaPlayer = MediaPlayer.create(context,Uri.parse(myUri.toString()));
                //mMediaPlayer.setDataSource(context, myUri);
                mMediaPlayer.setLooping(true);
            }
            mMediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //停止播放
    public static void stopRing(){
        if (mMediaPlayer!=null){
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer=null;
            }
        }
    }

}