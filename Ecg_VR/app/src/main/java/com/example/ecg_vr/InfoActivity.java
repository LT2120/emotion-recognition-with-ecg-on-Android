package com.example.ecg_vr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ecg_vr.myTools.WaveUtil;
import com.jpeng.jpspringmenu.SpringMenu;
import com.leaf.library.StatusBarUtil;
import androidx.appcompat.app.ActionBar;

import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifImageView;

public class InfoActivity extends AppCompatActivity {

    private Toolbar tl_content;
    private String text1 = "       其中，对处于恶心",text2=" 惊恐",text3="极端消极情绪可以发出震动",text4="或铃声",text5="的警报。";
    private TextView mix;

    public String account="";
    private SharedPreferences spf;
    private SharedPreferences.Editor edit;

    public void onBackPressed() {
        //Intent intent=new Intent(this,MainActivity.class);
        //startActivity(intent);
        this.finish();

    }

    public void init(){

        Intent intent=getIntent();
        account=intent.getStringExtra("account");
        spf=getSharedPreferences(account,MODE_PRIVATE);
        edit = spf.edit();

        tl_content = (Toolbar) findViewById(R.id.tl_content);
        mix = findViewById(R.id.mixture);

        SpannableString spannableString = new SpannableString(mix.getText());
        Drawable drawable =getResources().getDrawable(R.drawable.cdisgust);
        drawable.setBounds(0,0,drawable.getMinimumWidth()/6,drawable.getMinimumHeight()/6);
        ImageSpan imageSpan = new ImageSpan(drawable);
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mix.append(text1);//这是图片之前那一段文字
        mix.append(spannableString);//这是图片显示
        mix.append(text2);//这是图片之后那段文字

        drawable =getResources().getDrawable(R.drawable.cnegtive);
        drawable.setBounds(0,0,drawable.getMinimumWidth()/6,drawable.getMinimumHeight()/6);
        imageSpan = new ImageSpan(drawable);
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mix.append(spannableString);//这是图片显示
        mix.append(text3);//这是图片之后那段文字

        drawable =getResources().getDrawable(R.mipmap.vibrate);
        drawable.setBounds(0,0,drawable.getMinimumWidth()/6,drawable.getMinimumHeight()/6);
        imageSpan = new ImageSpan(drawable);
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mix.append(spannableString);//这是图片显示
        mix.append(text4);//这是图片之后那段文字

        drawable =getResources().getDrawable(R.mipmap.ring);
        drawable.setBounds(0,0,drawable.getMinimumWidth()/6,drawable.getMinimumHeight()/6);
        imageSpan = new ImageSpan(drawable);
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mix.append(spannableString);//这是图片显示
        mix.append(text5);//这是图片之后那段文字

        //将toolbar同状态栏合为一体
        //StatusBarUtil.setTransparentForWindow(this);
        //和状态栏一起显示渐变色
        //StatusBarUtil.setGradientColor(this, tl_content);
        //把toolbar作为系统自带的Actionbar
        //setSupportActionBar(tl_content);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //getSupportActionBar().setTitle("首页");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏

        init();
    }
}
