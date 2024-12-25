package com.example.ecg_vr;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecg_vr.myTools.Medfilt;
import com.example.ecg_vr.myTools.MediaUtil;
import com.example.ecg_vr.myTools.VibrateUtil;
import com.example.ecg_vr.myTools.WaveShowView;
import com.example.ecg_vr.myTools.WaveUtil;
import com.facebook.rebound.SpringConfig;
import com.example.ecg_vr.myTools.ListBean;
import com.example.ecg_vr.myTools.MyAdapter;
import com.jpeng.jpspringmenu.MenuListener;
import com.jpeng.jpspringmenu.SpringMenu;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements MenuListener {

    private SharedPreferences spf;
    private SharedPreferences.Editor edit;
    public Boolean vibrateJud = true,isShort;

    private byte[] oneInput = new byte[]{-86, -86, -15, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int oneInputIndex = 0;
    private double[] data = new double[125];
    private int dataIndex = 0;
    private Medfilt filter = new Medfilt();

    private static WaveShowView waveVi;
    private SpringMenu menu;
    private ImageView LeftMenu,EcgIcon;
    public static WaveUtil waveUtil;
    private TextView tvContent,userName,change,bt,emoText;
    public int jud = 0,emo_C=2,sct=0;
    public String account="";
    public Uri alert;
    //private TextView textView;
    private static final int msgKey1 = 1;
    private TextView runningTime;
    private int Hour=0,Minute=0,Second=0;
    public static MainActivity instance;

    public boolean isVibrating = true,isMediaing = true;
    private VibrateUtil vi = new VibrateUtil();
    private AlertDialog dialog;
    private CountDownTimer countDownTimer;
    private int emo_switch=0,d_c=0;
    private int p_t=0,n_t=0,sc_t=0,si_t=0;
    private TextView positive_t,neutral_t,scared_t,sick_t;

    @Override
    public void onBackPressed() {
        if(menu.isOpened()){
            menu.closeMenu();
        }else{
            //Intent intent=new Intent(this,Activity_Splash_login.class);
            //startActivity(intent);
            //this.finish();
            System.exit(0);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void init(){
        instance = this;


        waveUtil = new WaveUtil();
        menu = new SpringMenu(this,R.layout.view_menu);
        //emo=findViewById(R.id.emotion);
        change=findViewById(R.id.changeIcon);
        waveVi = findViewById(R.id.waveview);
        EcgIcon = findViewById(R.id.ecg);
        bt = findViewById(R.id.bt);
        positive_t = findViewById(R.id.p_t);
        neutral_t = findViewById(R.id.n_t);
        scared_t = findViewById(R.id.sc_t);
        sick_t = findViewById(R.id.si_t);
        emoText = findViewById(R.id.emotionText);
        //GifImageView mygif = findViewById(R.id.gifImageView);//gif

        //mTime = (TextView) findViewById(R.id.mytime);
        runningTime = (TextView) findViewById(R.id.runningtime);

        ObjectAnimator moveY = ObjectAnimator.ofFloat(waveVi,"translationY",-1000f,0f);
        //沿x轴放大
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(waveVi, "scaleX", 3f, 1f);
        //沿y轴放大
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(waveVi, "scaleY", 3f, 1f);
        //透明动画
        ObjectAnimator animator = ObjectAnimator.ofFloat(waveVi, "alpha", 0f, 1f);
        animator.setDuration(1000);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(EcgIcon, "alpha", 0f, 1f);
        animator.setDuration(1000);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(menu, "alpha", 0f, 1f);
        animator.setDuration(1000);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(bt, "alpha", 0f, 1f);
        animator.setDuration(1000);
        AnimatorSet set = new AnimatorSet();
        //同时沿X,Y轴放大，且改变透明度，然后移动
        set.play(scaleXAnimator).with(moveY).with(scaleYAnimator).with(animator).with(animator1).with(animator2).with(animator3);
        //都设置3s，
        set.setDuration(1000);
        set.start();

        Intent intent=getIntent();
        account=intent.getStringExtra("account");

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(calendar.YEAR);
        int monthOfYear = calendar.get(calendar.MONTH)+1;
        int dayOfMonth = calendar.get(calendar.DAY_OF_MONTH);

        SharedPreferences spf1 = getSharedPreferences(account+"-"+year+"-"+monthOfYear+"-"+dayOfMonth,MODE_PRIVATE);
        if(spf1.getBoolean("isExist",false))
        {

            Log.d("data", "load : " + account + "-" + year + "-" + monthOfYear + "-" + dayOfMonth);
            sc_t = spf1.getInt("scared_time", 0);
            si_t = spf1.getInt("sick_time", 0);
            n_t = spf1.getInt("neutral_time", 0);
            p_t = spf1.getInt("positive_time", 0);
            Second = sc_t + si_t + n_t + p_t;

            if (Second > 60) {
                Minute = (int) (Second / 60);
                Second = Second % 60;
            }
            if (Minute > 60) {
                Hour = (int) (Minute / 60);
                Minute = Minute % 60;
            }
            if (Hour == 24) {
                Hour = 0;
            }
            runningTime.setText("0" + Hour + ":" + Minute + ":" + Second);

            scared_t.setText("惊恐情感时间:" + sc_t + "秒");
            positive_t.setText("积极情感时间:" + p_t + "秒");
            neutral_t.setText("平静情感时间:" + n_t + "秒");
            sick_t.setText("恶心情感时间:" + si_t + "秒");
        }

        spf=getSharedPreferences(account,MODE_PRIVATE);
        vibrateJud = spf.getBoolean("vibrateSwitch",true);
        isShort = spf.getBoolean("isShort",true);
        edit = spf.edit();

        String mid = spf.getString("ring",RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString());
        alert = Uri.parse(mid);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setTitle("首页");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏

        init();
        new TimeThread().start();

        /**
         * 设置弹性和摩擦
         * Tension弹性，20即可
         * Friction摩擦 数值越大打开关闭速度越慢0-100  3就好看
         * friction : 2f -4f 是合适值的范围,太小速度很快,弹性时间长,太大速度会很慢
         * friction的值一定不能为0,否则会无限反弹,根据rebound api的定义
         *
         */

        //menu.setMenuSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 3));
        //menu.setChildSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 5));

        //添加menu监听
        menu.setMenuListener(this);
        menu.setChildSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(5, 4));
        // 内容页变暗的效果
        menu.setFadeEnable(true);
        //面板在左边
        menu.setDirection(SpringMenu.DIRECTION_LEFT);
        // 允许菜单开始拖动的距离
        menu.setDragOffset(0.1f);
        ListBean[] listBeen = {
                new ListBean(R.mipmap.menu1, "蓝牙"),
//                new ListBean(R.mipmap.vibrate, "vibrate"),
//                new ListBean(R.mipmap.ring, "media"),
                new ListBean(R.drawable.ic_baseline_refresh_24, "刷新"),
                new ListBean(R.drawable.ic_baseline_account_circle_24, "用户")
        };
        MyAdapter adapter = new MyAdapter(this, listBeen);
        ListView listView = (ListView) menu.findViewById(R.id.test_listView);
        listView.setAdapter(adapter);

        //如果想单纯的打开的话，去掉下面的setDirection即可
        //左打开
        LeftMenu = findViewById(R.id.menu);
        LeftMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //面板在左边
                menu.setDirection(SpringMenu.DIRECTION_LEFT);
                //打开面板
                menu.openMenu();

            }
        });
        //关闭面板
        //返回键监听 判断+
        // menu.closeMenu();

        /*//右打开
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //面板在右边
                menu.setDirection(SpringMenu.DIRECTION_RIGHT);
                //打开面板
                menu.openMenu();

            }
        });
//        如果有滑块控件和菜单的一些冲突，你可以试试addignoreview忽略它们
//        menu.addIgnoredView(控件);

        //textView = findViewById(R.id.textView);*/

        tvContent=findViewById(R.id.bt);
        //为组件注册上下文菜单
        registerForContextMenu(tvContent);

        //tvContent.setText("欢迎你："+account);


        dialog = new AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert)
                .setTitle("警报！")
                .setMessage("在15秒后将向管理员汇报！\n点击我已知晓将取消该次汇报")
                .setNegativeButton("我已知晓", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                        if(!isMediaing){
                            //关闭
                            MediaUtil.stopRing();
                            isMediaing = true;
                        }
                        if (!isVibrating) {

                            vi.vibrateCancle(MainActivity.this);
                            isVibrating = true;
                        }
                        dialog.cancel();
                    }
                })
                .create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 防止出现两行title
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 15000;
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                final CharSequence negativeButtonText = "我已知晓";

                if(!MainActivity.this.alert.toString().equals("null")){

                    if(isMediaing){
                        //开启
                        MediaUtil.playRing(MainActivity.this,MainActivity.this.alert);
                        //MediaUtil.playRing(context);
                        isMediaing = false;
                    }
                }

                if(vibrateJud){
                if(vi.hasVibrator(MainActivity.this)){
                if (isVibrating) {//防止多次关闭抛出异常，这里加个参数判断一下
                    if(MainActivity.this.isShort){
                        vi.vibrate(MainActivity.this,new long[]{100, 200, 100, 200}, 0);
                    }else{
                        vi.vibrate(MainActivity.this, new long[]{100, 2000, 100, 2000}, 0);
                    }
                    isVibrating = false;

                }
                }
                }
                if(countDownTimer!=null) { countDownTimer.cancel();}
                countDownTimer = new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                negativeButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            if(!isMediaing){
                                //关闭
                                MediaUtil.stopRing();
                                isMediaing = true;
                            }
                            if (!isVibrating) {

                                vi.vibrateCancle(MainActivity.this);
                                isVibrating = true;
                            }
                            dialog.dismiss();
                        }
                    }
                }.start();
            }
        });


    }


    /**
     * 重写触摸方法
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return menu.dispatchTouchEvent(ev);
    }


    /**
     * 菜单打开回调
     */
    @Override
    public void onMenuOpen() {
        //暂停所有
        sct = 0;
        jud=0;
        waveUtil.stop();
        //Toast.makeText(this, "菜单已打开!!", Toast.LENGTH_SHORT).show();
        userName = (TextView) findViewById(R.id.User);
        //Toast.makeText(this,userName+"?",Toast.LENGTH_SHORT).show();
        userName.setText(account);
    }

    /**
     * 菜单关闭回调
     */
    @Override
    public void onMenuClose() {
        //Toast.makeText(this, "菜单已关闭!!!", Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * 当菜单正在被打开或者被关闭时,这个方法将会被回调(包含拖动弧度)
     * @value: 0f-2f,0f表示菜单关闭,2f则表示打开
     * @bouncing: 这个布尔值用来判断菜单是否在反弹状态
     * 当处于反弹状态时,这个值无限趋近于2f,否则就是0f
     */
    @Override
    public void onProgressUpdate(float value, boolean bouncing) {
    }


    public void change2User(){
        Intent intent = new Intent(this,UserActivity.class);
        intent.putExtra("account",account);
        startActivity(intent);
    }

    //菜单长按
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        switch (v.getId()){
            case R.id.bt:
                menu.setHeaderTitle("隐藏菜单（演示用）");
                menu.add(0,200,0,"平静情绪");
                menu.add(0,201,0,"积极情绪");
                menu.add(0,203,0,"恶心情绪");
                menu.add(0,204,0,"惊恐情绪");
                SubMenu submenu = menu.addSubMenu(0,205,0,"情绪变化较大的数据");
                submenu.add(0,210,0,"平静-积极");
                submenu.add(0,211,0,"积极-惊恐");
                submenu.add(0,212,0,"平静-恶心");
                menu.add(0,206,0,"保存");
            default:
                break;
        }
        super.onCreateContextMenu(menu,v,menuInfo);
    }
    //重写Activity的onContextItemSelected()
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 210://1
                d_c=1;
                emo_switch=2;
                startWave();
                break;
            case 211://2
                d_c=1;
                emo_switch=3;
                startWave();
                break;
            case 212://3
                d_c=1;
                emo_switch=1;
                startWave();
                break;
            case 213://4
                d_c=1;
                emo_switch=0;
                startWave();
                //Toast.makeText(MainActivity.this,"你选了4",Toast.LENGTH_LONG).show();
                break;
            case 200://neutral
                d_c=0;
                emo_switch=2;
                startWave();
                break;
            case 201://positive
                d_c=0;
                emo_switch=3;
                startWave();
                break;
            case 203://sick
                d_c=0;
                emo_switch=1;
                startWave();
                break;
            case 204://fear
                d_c=0;
                emo_switch=0;
                startWave();
                //Toast.makeText(MainActivity.this,"你选了4",Toast.LENGTH_LONG).show();
                break;
            case 206://save

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(calendar.YEAR);
                int monthOfYear = calendar.get(calendar.MONTH)+1;
                int dayOfMonth = calendar.get(calendar.DAY_OF_MONTH);

                SharedPreferences spf=getSharedPreferences(account+"-"+year+"-"+monthOfYear+"-"+dayOfMonth,MODE_PRIVATE);
                SharedPreferences.Editor edit = spf.edit();
                edit.putInt("scared_time",sc_t);
                edit.putInt("sick_time",si_t);
                edit.putInt("neutral_time",n_t);
                edit.putInt("positive_time",p_t);
                edit.putBoolean("isExist",true);
                edit.commit();

                Log.d("data", "save : "+account+"-"+year+"-"+monthOfYear+"-"+dayOfMonth);

                //Toast.makeText(MainActivity.this,"你选了4",Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void emo_Change(int emo_jud){
        //Log.d("emo", "emo_Change: "+emo_jud);
        if(emo_jud==0){
            emoText.setText("消极-惊恐");
            emoText.setTextColor(Color.parseColor("#FFFF3251"));
//            sc_t+=1;
//            scared_t.setText("惊恐情感时间:"+sc_t+"秒");
            //emo.setImageResource(R.drawable.negative_scared_man);
            emo_C=0;
        }else if(emo_jud==1){
            emoText.setText("积极");
            emoText.setTextColor(Color.parseColor("#1AFA29"));
//            p_t+=1;
//            positive_t.setText("积极情感时间:"+p_t+"秒");
            //emo.setImageResource(R.drawable.positive_man);
            emo_C=1;
        }else if(emo_jud==2){
            emoText.setText("平静");
            emoText.setTextColor(Color.parseColor("#FF018786"));
//            n_t+=1;
//            neutral_t.setText("平静情感时间:"+n_t+"秒");
            //emo.setImageResource(R.drawable.neutral_man);
            emo_C=2;
        }else{
            emoText.setText("消极- 恶心");
            emoText.setTextColor(Color.parseColor("#78BB62"));
//            si_t+=1;
//            sick_t.setText("恶心情感时间:"+si_t+"秒");
            //emo.setImageResource(R.drawable.negative_sick_man);
            emo_C=3;
        }
    }

    public void iconChange(View view){
        if(emo_C==0){
            emoText.setText("消极-惊恐");
            emoText.setTextColor(Color.parseColor("#FFFF3251"));
//            sc_t+=1;
//            scared_t.setText("惊恐情感时间:"+sc_t+"秒");
            //emo.setImageResource(R.drawable.negative_scared_man);
            emo_C=1;
        }else if(emo_C==1){
            emoText.setText("积极");
            emoText.setTextColor(Color.parseColor("#1AFA29"));
//            p_t+=1;
//            positive_t.setText("积极情感时间:"+p_t+"秒");
            //emo.setImageResource(R.drawable.positive_man);
            emo_C=2;
        }else if(emo_C==2){
            emoText.setText("平静");
            emoText.setTextColor(Color.parseColor("#FF018786"));
//            n_t+=1;
//            neutral_t.setText("平静情感时间:"+n_t+"秒");
            //emo.setImageResource(R.drawable.neutral_man);
            emo_C=3;
        }else{
            emoText.setText("消极- 恶心");
            emoText.setTextColor(Color.parseColor("#78BB62"));
//            si_t+=1;
//            sick_t.setText("恶心情感时间:"+si_t+"秒");
            //emo.setImageResource(R.drawable.negative_sick_man);
            emo_C=0;
        }
    }

    private void startWave(){
        if(jud==0){
            jud=1;

            if(d_c == 0){
                int i = waveUtil.showWaveData(waveVi,emo_switch);
            }
            else {
                int i = waveUtil.showWaveData_c(waveVi,emo_switch);
            }

            //Toast.makeText(getApplicationContext(),"in:"+i,Toast.LENGTH_LONG).show();
        }else {
            jud=0;
            waveUtil.stop();
        }
    }

    public void click(View view){
        startWave();

    }

    public void warning(View view) {
        if(countDownTimer!=null) { countDownTimer.cancel();}
        dialog.show();
    }

    public void setting(View view) {
        Intent intent = new Intent(this,InfoActivity.class);
        intent.putExtra("account",account);
        startActivity(intent);
        //startActivityForResult(intent,0);
    }

    public void toUser(){
        Intent intent = new Intent(this,UserActivity.class);
        intent.putExtra("account",account);
        //startActivity(intent);
        startActivityForResult(intent,0);
    }

    public void toBt(){
        Intent intent = new Intent(this,BtActivity.class);
        intent.putExtra("account",account);
        //startActivity(intent);
        startActivityForResult(intent,1);
    }

    public void toRefresh(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("account",account);
        this.finish();
        startActivity(intent);
        //startActivityForResult(intent,1);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case 0:{
                    String mid = spf.getString("ring", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString());
                    //Toast.makeText(this,"receive"+mid,Toast.LENGTH_SHORT).show();
                    alert = Uri.parse(mid);
                    vibrateJud = spf.getBoolean("vibrateSwitch", true);
                    isShort = spf.getBoolean("isShort", true);
                    break;
                }
                case 1:{

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void startCountTimer(View view){
        if(sct == 0){
            sct=1;
        }else if(sct == 1){
            sct = 0;
        }

    }

    public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }

    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    //mTime.setText(getTime());

                    if(sct == 1){
                        getTime();
                        runningTime.setText(countTime());
                    }

                    break;
                default:
                    break;
            }
        }
    };
    //获得当前年月日时分秒星期
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getTime(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));//时
        String mMinute = String.valueOf(c.get(Calendar.MINUTE));//分
        String mSecond = String.valueOf(c.get(Calendar.SECOND));//秒
        if(sct == 1){
            Second +=1;

            if(emo_C==0){
                sc_t+=1;
                scared_t.setText("惊恐情感时间:"+sc_t+"秒");
            }else if(emo_C==1){
                p_t+=1;
                positive_t.setText("积极情感时间:"+p_t+"秒");
            }else if(emo_C==2){
                n_t+=1;
                neutral_t.setText("平静情感时间:"+n_t+"秒");
            }else{
                si_t+=1;
                sick_t.setText("恶心情感时间:"+si_t+"秒");
            }
        }

        return mHour+":"+mMinute+":"+mSecond;
    }

    public String countTime(){
        if(Second==60){
            Minute+=1;
            Second=0;
        }
        if(Minute==60){
            Hour+=1;
            Minute=0;
        }
        if(Hour==24){
            Hour=0;
        }
        return "0"+Hour+":"+Minute+":"+Second;
    }

    public void Main_show1data(float data){
        //Toast.makeText(this,""+data,Toast.LENGTH_SHORT).show();

        waveUtil.showWave1data(waveVi,data);

    }

    public void Main_receiveData(byte []a){

          parseSampling(a);
//        String mid_str = bytes2HexString(a);
//        mid_str = hexString2String(mid_str);
//        bt.setText(mid_str);
    }

    public void Main_btText(String str){
        bt.setText(str);
    }

    //读数据转化
    private void parseSampling(byte[] input) {
        Log.d("Main", "parseSampling: dataingggggggggggggggggggggg!"+input.length);
        float[] ecgFilter= new float[25];
        float midEcg = 0;
        for (int i = 0; i < input.length; i++) {//byte to double

            if (oneInputIndex < 4) {//对齐头标签
                if (input[i] != oneInput[oneInputIndex]) {
                    //Log.d("main--", "没有匹配数据头！输入"+input[i]+"要匹配"+oneInput[oneInputIndex]);
                    oneInputIndex = 0;
                } else {
                    Log.d("main--", "匹配数据头成功");
                    oneInputIndex++;
                }

            } else if (oneInputIndex < 104) {
                oneInput[oneInputIndex] = input[i];
                oneInputIndex++;
            } else {//4-104共100 byte，转25 int
                oneInput[oneInputIndex] = input[i];
                oneInputIndex = 0;
                for(int j = 1; j <= 25; j++){//每4 byte转1 int 0.2s
                    int ecgData = convert(Arrays.copyOfRange(oneInput, 4*j, 4+4*j));
                    data[dataIndex] = (double) ecgData;
                    filter.push((double) ecgData);
                    double baseline=filter.Medfilt();
                    ecgFilter[j-1] = (float) (baseline-(double) ecgData);

                    //将0.008扩大为0.04，取最值
                    if(Math.abs(midEcg)<Math.abs(ecgFilter[j-1])){
                        midEcg = ecgFilter[j-1];
                   }
                    if(j%5==0){
                        //WaveUtil waveUtil = (WaveUtil) mContext.getApplicationContext();
                        MainActivity.instance.Main_show1data(midEcg);
                        bt.setText(String.valueOf(midEcg));
                        //waveUtil.showWave1data(waveShowView,midEcg);
                    }

                    dataIndex++;
                }
                Toast.makeText(this,"dataIndex.... "+dataIndex,Toast.LENGTH_SHORT).show();
                if (dataIndex >= 125) {//下标为0-124
//                    HR hr = new HR(data, sampleRate);
//                    hr.setWindow_size(segLength);
//                    hrPre = hr.getHR();
//                    freshAttr(10, (float) hrPre);
//                    post(10, new float[]{(float) hrPre});
//                    RR rr = new RR(data, sampleRate, segLength);
//                    try {
//                        rrPre = rr.getRR();hy
//                        freshAttr(25, (float) rrPre);
//                        post(25, new float[]{(float) rrPre});
//                    } catch (IOException e) {
//                        Logger.getInstance().error(TAG, e.toString());
//                    }
//                    Logger.getInstance().error(TAG, "HR = " + hrPre + " RR = " + rrPre);
//                    Logger.getInstance().error(TAG, "data: " + Arrays.toString(data));
                    //float[] processed = signalFilters.filter(data);
//                    float[] processed = new float[125];
//                    for (int j = 0; j < 125; j++)
//                        processed[j] = (float) data[j];
                    //post(7, processed);
                    Log.e("Main", "processed data: " + Arrays.toString(data));
                    //Logger.getInstance().error(TAG, "processed data: " + Arrays.toString(processed));
                    dataIndex = 0;
                }
            }
        }


    }

    private int convert(byte[] data) {
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            result <<= 8;
            result |= (data[i] & 0x000000ff);
        }

        return result;
    }

    public static String bytes2HexString(byte[] b, int length) {
        StringBuffer result = new StringBuffer();
        String hex;
        for (int i = 0; i < length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase()).append(" ");
        }
        return result.toString();
    }

    public static String bytes2HexString(byte[] b) {
        if (b == null) {
            return "";
        }
        String rs = "";
        int bl = b.length;
        byte bt;
        String bts = "";
        int btsl;
        for (int i = 0; i < bl; i++) {
            bt = b[i];
            bts = Integer.toHexString(bt);
            btsl = bts.length();
            if (btsl > 2) {
                bts = bts.substring(btsl - 2).toUpperCase();
            } else if (btsl == 1) {
                bts = "0" + bts.toUpperCase();
            } else {
                bts = bts.toUpperCase();
            }
            rs += bts;
        }
        return rs;
    }

    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),
                    16).byteValue();
        }
        return temp;
    }

    private String moreSecond(int second){
        if(second < 60){

            return second+"秒";
        }else if(second<3600){
            int mid = (int)((int)second/60);
            return mid + "分钟" + second%60 + "秒";
        }else {
            int mid1 = (int)((int)second/3600);
            int mid = (int)((int)(second%3600)/60);
            return mid1+"小时" +mid + "分钟" + second%60 + "秒";
        }
    }
}
