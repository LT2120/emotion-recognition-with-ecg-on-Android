package com.example.ecg_vr.myTools;

import android.content.Context;
import android.net.Uri;

import com.example.ecg_vr.MainActivity;
import com.example.ecg_vr.myTools.MediaUtil;
import com.example.ecg_vr.myTools.VibrateUtil;

public class AlarmingUtil {
    private VibrateUtil vi = new VibrateUtil();
    private MediaUtil me = new MediaUtil();

    public void start(Context context, long[] mode, int repeat, Uri alert){
        vi.vibrate(context,mode, repeat);
        me.playRing(context,alert);

    }

    public void stop(Context context){
        me.stopRing();
        vi.vibrateCancle(context);
    }

}
