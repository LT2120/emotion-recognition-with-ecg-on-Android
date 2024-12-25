package com.example.ecg_vr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecg_vr.myTools.ClickIconEditText;

public class Activity_Splash_login extends AppCompatActivity {

    public static final int REQUEST_CODE_REGISTER = 1;
    private static final String TAG="tag";
    private Button btnLogin;
    private EditText etAccount;
    private ClickIconEditText etPassword;
    private CheckBox cbRemember;
    private String userName="a";
    private String pass="123";
    private boolean Visuality = false;
    @Override
    public void onBackPressed() {
        this.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        //getSupportActionBar().setTitle("登录");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏

        initView();
        initData();

        etPassword.setOnIconClickListener(new ClickIconEditText.OnIconClickListener() {
            @Override
            public void onIconClickListener(ClickIconEditText view, int iconType) {
                switch (iconType) {
                    case ClickIconEditText.CLICK_LEFT_ICON:
                        //点击了左侧图标
                        break;
                    case ClickIconEditText.CLICK_RIGHT_ICON:
                        //点击了右侧图标
                        if (!Visuality) {

                            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            Drawable drawableLeft = getResources().getDrawable(R.drawable.ic_baseline_lock_24);
                            drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24);
                            drawableRight.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            etPassword.setCompoundDrawables(drawableLeft, null, drawableRight, null);

                            Visuality = true;
                        } else {

                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            Drawable drawableLeft = getResources().getDrawable(R.drawable.ic_baseline_lock_24);
                            drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_baseline_visibility_24);
                            drawableRight.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            etPassword.setCompoundDrawables(drawableLeft, null, drawableRight, null);
                            Visuality = false;
                        }
                        break;
                    case ClickIconEditText.CLICK_TOP_ICON:
                        //点击了上边图标
                        break;
                    case ClickIconEditText.CLICK_BOTTOM_ICON:
                        //点击了下边图标
                        break;
                }
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断账号密码是否符合要求
                String account=etAccount.getText().toString();
                String password=etPassword.getText().toString();
                if (TextUtils.isEmpty(userName)){
                    Toast.makeText(Activity_Splash_login.this,"还没有注册账号！",Toast.LENGTH_LONG).show();
                    return;
                }
                //take account by name from naming file and check it
                SharedPreferences spf=getSharedPreferences(account,MODE_PRIVATE);
                String faccount=spf.getString("account","");
                String fpassword=spf.getString("password","");
                //更新用户名密码（注册的用户名密码）
                userName=faccount;
                pass=fpassword;

                if (TextUtils.equals(account,userName)){
                    if (TextUtils.equals(password,pass)){
                        Toast.makeText(Activity_Splash_login.this,"恭喜你，登录成功！",Toast.LENGTH_SHORT).show();
                        if (cbRemember.isChecked()){
                            spf=getSharedPreferences(account,MODE_PRIVATE);
                            SharedPreferences.Editor edit=spf.edit();
                            edit.putString("account",account);
                            edit.putString("password",password);
                            edit.putBoolean("isRemember",true);
                            edit.apply();

                            spf=getSharedPreferences("spfRecorid",MODE_PRIVATE);
                            edit = spf.edit();
                            edit.putString("account",account);
                            edit.putString("password",password);
                            edit.putBoolean("isRemember",true);
                            edit.commit();
                        }else {
                            spf=getSharedPreferences(account,MODE_PRIVATE);
                            SharedPreferences.Editor edit=spf.edit();
                            edit.putBoolean("isRemember",false);
                            edit.apply();

                            spf=getSharedPreferences("spfRecorid",MODE_PRIVATE);
                            edit=spf.edit();
                            edit.putBoolean("isRemember",false);
                            edit.apply();
                        }

                        //实现跳转
                        Intent intent=new Intent(Activity_Splash_login.this,MainActivity.class);
                        //传递用户名
                        intent.putExtra("account",account);
                        startActivity(intent);
                        //接收自己
                        Activity_Splash_login.this.finish();

                    }else {
                        Toast.makeText(Activity_Splash_login.this,"密码错误！",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(Activity_Splash_login.this,"用户名错误",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //记住密码（取出数据）
    private void initData() {
        SharedPreferences spf=getSharedPreferences("spfRecorid",MODE_PRIVATE);
        boolean isRemember=spf.getBoolean("isRemember",false);
        String account=spf.getString("account","");
        String password=spf.getString("password","");
        //更新用户名密码（注册的用户名密码）
        userName=account;
        pass=password;

        if (isRemember){
            etAccount.setText(account);
            etPassword.setText(password);
            cbRemember.setChecked(true);
        }

    }

    //初始化
    private void initView(){
        btnLogin=findViewById(R.id.btn_login);
        etAccount=findViewById(R.id.et_account);
        etPassword=findViewById(R.id.et_password);
        cbRemember=findViewById(R.id.cb_remember);
    }

    //还没有账号（跳转到注册）
    public void toRegister(View view) {
        Intent intent=new Intent(this,RegisterActivity.class);

        //startActivity(intent);
        startActivityForResult(intent,REQUEST_CODE_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_REGISTER&&resultCode==RegisterActivity.RESULT_CODE_REGISTER&&data!=null){
            Bundle extras=data.getExtras();
            //获取用户密码
            String account=extras.getString("account","");
            String password=extras.getString("password","");
            etAccount.setText(account);
            etPassword.setText(password);

            //更新用户名密码（注册的用户名密码）
            userName=account;
            pass=password;
        }
    }
}