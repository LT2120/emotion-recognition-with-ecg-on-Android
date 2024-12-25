package com.example.ecg_vr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecg_vr.myTools.MediaUtil;
import com.example.ecg_vr.myTools.VibrateUtil;
import com.example.ecg_vr.myTools.WaveUtil;
import com.jpeng.jpspringmenu.SpringMenu;
import com.leaf.library.StatusBarUtil;

public class SettingActivity extends AppCompatActivity {

    private SharedPreferences spf;
    private SharedPreferences.Editor edit;
    private boolean mySwitch,myType;
    private Switch s1;
    private CheckBox c2,c3;
    private String account;
    public Uri alert;

    public void onBackPressed() {
        //Intent intent=new Intent(this,MainActivity.class);
        //startActivity(intent);
        this.finish();

    }

    public void init(){
        s1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);


        Intent intent=getIntent();
        account=intent.getStringExtra("account");
        spf=getSharedPreferences(account,MODE_PRIVATE);
        edit = spf.edit();

        MediaUtil.stopRing();
        MediaUtil.stopRing();

        mySwitch=spf.getBoolean("vibrateSwitch",true);
        myType=spf.getBoolean("isShort",true);
        String mid = spf.getString("ring",RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString());
        alert = Uri.parse(mid);

        s1.setChecked(mySwitch);
        if(mySwitch){
            c2.setChecked(!myType);
            c3.setChecked(myType);


            c2.setEnabled(true);
            c2.setFocusable(true);
            c3.setEnabled(true);
            c3.setFocusable(true);
        }else{
            c2.setChecked(!myType);
            c3.setChecked(myType);

            s1.setChecked(false);
            c2.setEnabled(false);
            c2.setFocusable(false);
            c3.setEnabled(false);
            c3.setFocusable(false);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏

        //将toolbar同状态栏合为一体
        StatusBarUtil.setTransparentForWindow(this);
        //这里要根据背景图的整体色调是亮色还是暗色来进行选择状态栏字体的色调
        //设置状态栏的字体颜色为亮色
        StatusBarUtil.setLightMode(this);
        //设置状态栏的字体颜色为暗色
        //StatusBarUtil.setDarkMode(this);

        init();

        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s1.isChecked()){
                    edit.putBoolean("vibrateSwitch",true);

                    c2.setChecked(!myType);
                    c3.setChecked(myType);
                    c2.setEnabled(true);
                    c2.setFocusable(true);
                    c3.setEnabled(true);
                    c3.setFocusable(true);

                }else{
                    edit.putBoolean("vibrateSwitch",false);

                    c2.setEnabled(false);
                    c2.setFocusable(false);
                    c3.setEnabled(false);
                    c3.setFocusable(false);
                }
                edit.commit();

            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(c2.isChecked()){
                    edit.putBoolean("isShort",false);
                    myType=false;

                }else{
                    edit.putBoolean("isShort",true);
                    myType=true;

                }
                edit.commit();
                c2.setChecked(!myType);
                c3.setChecked(myType);
            }
        });

        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!c3.isChecked()){
                    edit.putBoolean("isShort",false);
                    myType=false;

                }else{
                    edit.putBoolean("isShort",true);
                    myType=true;

                }
                edit.commit();
                c2.setChecked(!myType);
                c3.setChecked(myType);
            }
        });
    }

    public void ringSetting(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置通知铃声");
        if (alert != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(alert.toString()));//将已经勾选过的铃声传递给系统铃声界面进行显示
        }
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);//获取用户选择的铃声数据
            alert = pickedUri;
            if(pickedUri==null){
                //Toast.makeText(this,"null",Toast.LENGTH_SHORT).show();
                edit.putString("ring","null");
                edit.commit();
            }else{

            //Toast.makeText(this,""+pickedUri.toString()+"code: "+requestCode,Toast.LENGTH_SHORT).show();


                edit.putString("ring",alert.toString());
                edit.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
