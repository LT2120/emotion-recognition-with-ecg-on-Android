package com.example.ecg_vr;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecg_vr.R;
import com.example.ecg_vr.myTools.ClickIconEditText;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int RESULT_CODE_REGISTER = 0;
    private Button btnRegister;
    private EditText etAccount;
    private ClickIconEditText etPass,etPassConfirm;
    private Boolean Visuality = false;
    private String account;
    //private CheckBox cbAgree;
    @Override
    public void onBackPressed() {
        this.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getSupportActionBar().setTitle("注册");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏

        etAccount=findViewById(R.id.et_account);
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        if(account!=null){
            etAccount.setEnabled(false);
            etAccount.setFocusable(false);
            etAccount.setText(account);
        }
        //etPass=findViewById(R.id.et_password);
        //etPassConfirm=findViewById(R.id.et_password_Confirm);
        //cbAgree=findViewById(R.id.cb_agree);
        btnRegister=findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(this);

        etPassConfirm = findViewById(R.id.et_password_Confirm);
        etPass = findViewById(R.id.et_password);
        etPass.setOnIconClickListener(new ClickIconEditText.OnIconClickListener() {
            @Override
            public void onIconClickListener(ClickIconEditText view, int iconType) {
                switch (iconType) {
                    case ClickIconEditText.CLICK_LEFT_ICON:
                        //点击了左侧图标
                        break;
                    case ClickIconEditText.CLICK_RIGHT_ICON:
                        //点击了右侧图标
                        if (!Visuality) {

                            etPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            Drawable drawableLeft = getResources().getDrawable(R.drawable.ic_baseline_lock_24);
                            drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24);
                            drawableRight.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            etPass.setCompoundDrawables(drawableLeft, null, drawableRight, null);

                            Visuality = true;
                        } else {

                            etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            Drawable drawableLeft = getResources().getDrawable(R.drawable.ic_baseline_lock_24);
                            drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_baseline_visibility_24);
                            drawableRight.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            etPass.setCompoundDrawables(drawableLeft, null, drawableRight, null);
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
        etPassConfirm.setOnIconClickListener(new ClickIconEditText.OnIconClickListener() {
            @Override
            public void onIconClickListener(ClickIconEditText view, int iconType) {
                switch (iconType) {
                    case ClickIconEditText.CLICK_LEFT_ICON:
                        //点击了左侧图标
                        break;
                    case ClickIconEditText.CLICK_RIGHT_ICON:
                        //点击了右侧图标
                        if (!Visuality) {

                            etPassConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            Drawable drawableLeft = getResources().getDrawable(R.drawable.ic_baseline_lock_24);
                            drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24);
                            drawableRight.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            etPassConfirm.setCompoundDrawables(drawableLeft, null, drawableRight, null);

                            Visuality = true;
                        } else {

                            etPassConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            Drawable drawableLeft = getResources().getDrawable(R.drawable.ic_baseline_lock_24);
                            drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            Drawable drawableRight = getResources().getDrawable(R.drawable.ic_baseline_visibility_24);
                            drawableRight.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                            etPassConfirm.setCompoundDrawables(drawableLeft, null, drawableRight, null);
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

    }

    @Override
    public void onClick(View v) {
        String name=etAccount.getText().toString();
        String pass=etPass.getText().toString();
        String PassConfirm=etPassConfirm.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this,"用户名不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(pass)){
            Toast.makeText(RegisterActivity.this,"密码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.equals(pass,PassConfirm)){
            Toast.makeText(RegisterActivity.this,"密码不一致！",Toast.LENGTH_LONG).show();
            return;
        }
        /*if (!cbAgree.isChecked()){
            Toast.makeText(RegisterActivity.this,"请同意用户协议！",Toast.LENGTH_LONG).show();
            return;
        }*/


        //存储注册的用户名和密码
        //now change to multi account and record the lastest account
        SharedPreferences spf=getSharedPreferences("spfRecorid",MODE_PRIVATE);
        account = spf.getString("account","");
        SharedPreferences.Editor edit = spf.edit();
        edit.putString("account",name);
        edit.putString("password",pass);
        edit.putBoolean("isRemember",true);
        edit.commit();

        spf=getSharedPreferences(name,MODE_PRIVATE);
        edit = spf.edit();
        edit.putString("account",name);
        edit.putString("password",pass);
        edit.putBoolean("isRemember",true);
        edit.commit();
        //注册成功后，回到登录页面,数据回传
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putString("account",name);
        bundle.putString("password",pass);
        intent.putExtras(bundle);
        setResult(RESULT_CODE_REGISTER,intent);

        if(account.isEmpty()){
            Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(RegisterActivity.this,"修改成功！",Toast.LENGTH_LONG).show();
        }
        this.finish();
    }
}