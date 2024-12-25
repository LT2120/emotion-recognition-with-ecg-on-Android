package com.example.ecg_vr;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import org.w3c.dom.Text;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserActivity extends AppCompatActivity {
    //相册请求码
    private static final int ALBUM_REQUEST_CODE = 1;
    //相机请求码
    private static final int CAMERA_REQUEST_CODE = 2;
    //剪裁请求码
    private static final int CROP_SMALL_PICTURE = 3;

    private ImageView hBack,hHead;
    private TextView user;
    private String account;

    public void onBackPressed() {
        //Intent intent=new Intent(this,MainActivity.class);
        //startActivity(intent);
        Intent intent = new Intent();
        this.setResult(2,intent);
        this.finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏

        Intent intent=getIntent();
        account=intent.getStringExtra("account");

        hBack=findViewById(R.id.h_back);
        hHead = findViewById(R.id.h_head);
        user = findViewById(R.id.user_name);


        user.setText(account);


        //设置背景磨砂效果
        Glide.with(this).load(R.drawable.head)
                .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                .into(hBack);
        //设置圆形图像
        Glide.with(this).load(R.drawable.head)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(hHead);

    }

    public void changePwd(View view){
        Intent intent = new Intent(this,RegisterActivity.class);
        intent.putExtra("account",account);
        startActivity(intent);
    }
    public void toSetting(View view){
        Intent intent = new Intent(this,SettingActivity.class);
        intent.putExtra("account",account);
        startActivityForResult(intent,0);
    }
    public void toHistory(View view){
        Intent intent = new Intent(this,HistoryActivity.class);
        intent.putExtra("account",account);
        startActivityForResult(intent,0);
    }

    public void ChangeHead(View view){
        getPicFromAlbm();
    }

    /**
     * 从相册获取图片
     */
    private void getPicFromAlbm() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            //调用相机后返回
            case CAMERA_REQUEST_CODE:

                break;
            //调用相册后返回
            case ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Log.i("here", "changingggggggggggggggggggggggggg!");
                    Uri uri = intent.getData();
                    Log.i("here", "chan" + R.drawable.head);
                    hBack.setImageURI(uri);
                    hHead.setImageURI(uri);
                    //设置背景磨砂效果
                    Glide.with(this).load(uri)
                            .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                            .into(hBack);
                    //设置圆形图像
                    Glide.with(this).load(uri)
                            .bitmapTransform(new CropCircleTransformation(this))
                            .into(hHead);
                    // 开始对图片进行裁剪处理
                    //startPhotoZoom(uri);
                }
                break;
            //调用剪裁后返回
            case CROP_SMALL_PICTURE:

                break;
        }

    }

}
