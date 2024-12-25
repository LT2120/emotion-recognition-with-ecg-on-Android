package com.example.ecg_vr.myTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecg_vr.MainActivity;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class pytorch{
    //private float []Ecg = new float[]{59, 10, -7, -9, -6, -11, -11, -14, -15, -16, -15, -11, -14, -12, -9, -11, -10, -12, -15, -17, -11, -12, -11, -2, -1, 4, 8, 15, 21, 29, 41, 47, 52, 61, 57, 54, 49, 42, 33, 30, 25, 22, 19, 21, 16, 23, 15, 12, 9, 15, 16, 11, 11, 11, 12, 9, 10, 13, 12, 10, 13, 16, 18, 16, 20, 15, 16, 16, 21, 23, 25, 26, 26, 24, 28, 28, 25, 24, 22, 19, 18, 20, 22, 27, 20, 22, 28, 22, 20, 25, 26, 22, 27, 23, 25, 32, 35, 36, 37, 32, 26, 29, 30, 34, 27, 29, 27, 24, -21, 52, 182, 261, 198, 193, 119, 53, 26, 13, 5, 4, 4, 5, 3, 7, 3, 9, 9, 5, 2, 3, -2, 0, 10, 7, 7, 5, 8, 10, 13, 21, 28, 31, 42, 44, 48, 57, 71, 68, 73, 66, 51, 48, 37, 31, 24, 21, 21, 18, 13, 12, 13, 11, 10, 15, 13, 18, 13, 17, 16, 14, 16, 14, 18, 17, 18, 21, 15, 16, 19, 20, 21, 23, 20, 22, 18, 21, 17, 22, 24, 24, 23, 20, 25, 20, 17, 20, 22, 18, 22, 21, 21, 17, 15, 12, 18, 24, 16, 18, 13, 15, 15, 14, 17, 9, 14, 7, 10, 8, 11, 21, 26, 26, 15, 11, 18, 11, 14, 13, 18, -10, -47, 51, 239, 336, 223, 195, 100, 29, -1, -8, -13, -15, -18, -17, -20, -20, -23, -19, -22, -23, -21, -21, -17, -15, -22, -21, -19, -27, -26, -16, -17, -11, -10, -4, 7, 10, 14, 21, 24, 32, 33, 38, 30, 27, 20, 10, 9, 6, 7, 2, 4, -2, 1, 0, -3, -5, -6, -8, -9, -8, -7, -4, -5, -7, -4, -6, -4, -5, -3, -5, -6, 2, 0, -2, -4, 2, 4, 5, 6, 2, 3, 6, 7, 4, 6, 5, 6, 5, 9, 6, 5, 6, 6, 7, 4, 3, 6, 10, 7, 6, 10, 3, 5, 7, 11, 9, 9, 11, 6, 8, 5, 2, 8, 9, 2, 7, 10, 9, 11, 12, 16, 20, 22, 14, 15, 14, 11, 9, 7, 7, 7, -37, -11, 161, 339, 324, 206, 137, 51, 11, -2, -7, -13, -12, -15, -14, -15, -13, -16, -14, -16, -13, -6, -8, -13, -11, -13, -11, -7, -3, 1, 2, 7, 5, 9, 19, 30, 40, 46, 48, 53, 58, 61, 56, 53, 39, 32, 28, 23, 20, 16, 16, 14, 16, 20, 19, 19, 16, 22, 21, 21, 20, 19, 18, 19, 24, 27, 21, 19, 20, 20, 24, 25, 26, 29, 30, 25, 23, 26, 24, 20, 27, 26, 24, 28, 30, 27, 26, 28, 31, 32, 35, 32, 33, 36, 36, 28, 32, 31, 34, 36, 30, 32, 29, 28, 30, 39, 38, 38, 37, 32, 28, 30, 26, 20, 24, 38, 40, 38, 33, 27, 30, 24, 31, 33, 34, 29, -15, 9, 124, 227, 238, 192, 157, 69, 26, 10, 7, 6, -3, -2, -1, -2, -1, -3, -2, -4, -3, 0, -8, -3, -4, -2, 1, -2, 2, 1, 8, 9, 10, 16, 19, 24, 34, 39, 40, 46, 50, 48, 43, 41, 35, 29, 26, 21, 18, 16, 14, 9, 14, 13, 11, 13, 12, 14, 12, 14, 11, 10, 12, 12, 15, 19, 18, 20, 21, 24, 23, 24, 27, 32, 29, 24, 28, 22, 20, 24, 27, 22, 26, 24, 21, 21, 20, 24, 19, 25, 25, 17, 19, 20, 22, 22, 28, 22, 18, 15, 16, 22, 20, 14, 20, 22, 21, 24, 24, 25, 22, 25, 20, 19, 19, 19, 19, 14, 18, 22, 30, 35, 29, 24, 23, 18, 22, 18, 16, 22, -20, -15, 113, 281, 324, 223, 173, 74, 6, -4, -8, -8, -12, -9, -12, -13, -12, -10, -7, -14, -9, -6, -10, -13, -13, -13, -15, -12, -15, -16, -13, -8, -8, -10, -5, 3, 6, 10, 21, 22, 32, 30, 36, 28, 23, 17, 12, 9, 5, 4, 3, 1, 3, 5, 12, 1, 3, 1, 0, 1, -4, -2, 2, 0, -3, 2, 5, 4, 5, 8, 10, 11, 8, 6, 8, 10, 9, 12, 13, 8, 17, 15, 17, 12, 10, 16, 15, 15, 18, 22, 18, 18, 9, 14, 12, 14, 15, 15, 18, 15, 8, 12, 14, 10, 12, 14, 16, 13, 14, 17, 20, 15, 15, 18, 17, 16, 17, 18, 30, 31, 33, 22, 19, 23, 20, 22, 20, 27, 1, -44, 56, 245, 351, 205, 105, 38, 4, -5, -6, -12, -10, -12, -14, -18, -17, -15, -16, -15, -20, -18, -21, -23, -21, -24, -20, -23, -28, -24, -22, -16, -17, -18, -15, -6, -1, 1, 9, 12, 17, 18, 29, 20, 15, 8, -2, -1, -4, -8, -4, -6, -12, -10, -10, -7, -11, -8, -7, -10, -10, -13, -8, -9, 0, -7, -7, -8, -7, -6, -4, -3, -3, -2, -3, -3, -2, -3, 1, -2, 0, -6, -2, -5, 0, -1, -3, -5, -7, -8, -2, -1, 3, 2, 4, -2, 0, -6, 0, 0, -3, 0, 1, -5, -11, -4, -6, -6, -6, -7, -2, -5, -4, -2, -11, -9, 1, 7, 14, 11, 10, 4, 6, 6, 4, 9, 6, 9, -37, -16, 306, 318, 210, 151, 63, 14, 5, -5, -11, -10, -16, -19, -16, -14, -5, -16, -11, -15, -11, -11, -10, -12, -11, -12, -8, -9, -8, -4, -4, 2, 3, 7, 12, 19, 21, 30, 34, 35, 38, 39, 39, 27, 25, 15, 13, 8, 5, -1, 0, 0, 1, -4, -7, -6, 3, 4, 0, 2, 1, 3, 1, -1, 0, 4, 4, 3, 4, 5, 0, -1, 4, 4, 1, 2, 3, 1, -1, -3, -4, -5, 1, 0, -4, -4, 0, 3, -2, -2, -1, -4, -9, -6, 0, 3, -1, 1, 3, 0, 0, -1, 1, 4, -2, 0, -9, -7, -1, 8, 12, 6, 3, 1, -2, -5, -3, -5, -8, -2, -27, -41, 73, 205, 277, 158, 160, 9, -12, -16, -26, -25, -21, -19, -25, -22, -25, -25, -23, -22, -16, -15, -14, -15, -16, -13, -13, -11, -13, -11, -8, -3, 0, 7, 11, 12, 19, 31, 37, 43, 49, 46, 44, 33, 23, 13, 6, -9, -8, -12, -11, -17, -17, -21, -23, -21, -20, -22, -21, -20, -25, -19, -19, -17, -12, -6, -11, -10, -8, -10, -12, -9, -6, -9, -8, -8, -11, -13, -15, -12, -13, -12, -11, -12, -10, -7, -11, -13, -13, -15, -11, -10, -11, -4, -11, -13, -10, -14, -12, -9, -4, -8, -9, -11, -6, -7, -1, 2, 4, -4, -6, -7, -5, -3, -1, 3, 1, -31, -4, 155, 304, 290, 178, 130, 49, 5, -6, -11, -12, -20, -16, -19, -24, -25, -22, -20, -19, -24, -24, -21, -22, -24, -20, -18, -17, -20, -14, -11, -12, -8, -2, 2, 13, 18, 24, 31, 37, 43, 45, 38, 28, 21, 13, 5, 5, -3, -2, 0, 0, 2, 1, -1, -3, -12, -10, -5, -4, -12, -8, 1, 0, -1, 0, 5, 6, 6, 7, 5, 9, 6, 3, 2, 3, 5, 5, 7, 3, 3, 6, 11, 8, 6, 11, 9, 1, 5, 6, 3, 3, 5, 7, 6, 5, 6, 9, 5, 0, 1, 2, 0, 3, 10, 7, 9, 9, 5, 7, 8, 5, 2, 8, -3, -38, 42, 194, 292, 193, 171, 98, 31, 1, -11, -16, -18, -23, -19, -19, -14, -18, -20, -21, -24, -22, -21, -18, -20, -17};

    private int W_size = 1251;
    private static ArrayList<Float> input_pytorch = new ArrayList<>();
    public static boolean mutex = true, mutex2 = true, mutex3=true;
    private float[] scores = null;
    private Tensor outputTensor1 = null, outputTensor2 = null;
    private long size[] = new long[]{1,1,1250};
    private int index1 = 0, index2 = 0;
    private Context pContext = MainActivity.instance;
    private static Module module1 = null;
    private static Module module2 = null;
    private static Module module3 = null;

    public pytorch(){
        for(int i=0;i<W_size;i++){
            input_pytorch.add((float) 0);
        }

        try {
            // creating bitmap from packaged into app android asset 'image.jpg',
            // app/src/main/assets/image.jpg
            // loading serialized torchscript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
            //module = LiteModuleLoader.load(assetFilePath(this, "mobilenetV3large.ptl"));
            module1 = Module.load(assetFilePath(pContext, "bi_final_mobile_model_cnn.ptl"));
            module2 = Module.load(assetFilePath(pContext, "bi_final_mobile_model_valence.ptl"));
            module3 = Module.load(assetFilePath(pContext, "bi_final_mobile_model_arousal.ptl"));
        } catch (IOException e) {
            Log.e("PytorchHelloWorld", "Error reading assets", e);
        }


    }

    public void push(float e){
        if(mutex2){
        input_pytorch.remove(0);
        input_pytorch.add(e);
        }else{
            return;
        }
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    // 点击事件
    public int cnn() {
        mutex = false;
        if (mutex2 == false){
            mutex=true;
            return index2;
        }
        mutex2=false;
//        try {
//            Thread.sleep (10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        float[] Ecg = new float[1250];

//        for (int i = 0; i < 1250; i++) {
//            float mid = input_pytorch.get((i%1250));
//            Ecg[i] = mid;
//        }

        if(mutex3){
            mutex3 = false;
            int i =0;
            while (i < 1250){
                float mid = input_pytorch.get(i);
                Ecg[i] = mid;
                i = i+1;
            }
            mutex3 =true;
        }else {return index2;}
        final Tensor inputTensor = Tensor.fromBlob(Ecg, new long[]{1, 1250});
        //Toast.makeText(Pytorch.this,"ans:"+Ecg.toString(),Toast.LENGTH_LONG).show();
        // running the model
        outputTensor1 = module1.forward(IValue.from(inputTensor)).toTensor();
        outputTensor2 = module3.forward(IValue.from(outputTensor1)).toTensor();


        scores = outputTensor2.getDataAsFloatArray();
        Log.d("1", "cnn: " + scores[1] + "   " + scores[0]);

        index1 = 0;
        if (scores[1] > scores[0]) {
            index1 = 1;
        }

        outputTensor1 = module2.forward(IValue.from(outputTensor1)).toTensor();

        scores = outputTensor1.getDataAsFloatArray();
        Log.d("2", "cnn: " + scores[1] + "   " + scores[0]);
        index2 = 0;
        if (scores[1] > scores[0]) {
            if (index1 == 1) {
                index2 = 1;
            } else {
                index2 = 2;
            }

        } else {
            if (index1 == 1) {
                index2 = 0;
            } else {
                index2 = 3;
            }
        }
        //Log.d("index", "cnn: "+index2+"  " + index1);
        if(mutex == false)mutex =true;
        mutex2 = true;

//        MainActivity.instance.runOnUiThread(new Runnable() {
//            public void run() {
//                //MainActivity.instance.emo_Change(py.cnn(Ecg.get(emo_switch)));
//                MainActivity.instance.emo_Change(index2);
//            }
//        });

        return index2;
    }

    // 点击事件
    public int cnn(double[] data) {
        mutex = false;
        if (mutex2 == false){
            mutex=true;
            return index2;
        }
        mutex2=false;
//        try {
//            Thread.sleep (10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
        float[] Ecg = new float[1250];
        for (int i = 0; i < 1250; i++) {
            Ecg[i] = (float)data[i];
        }
        final Tensor inputTensor = Tensor.fromBlob(Ecg, new long[]{1, 1250});
        //Toast.makeText(Pytorch.this,"ans:"+Ecg.toString(),Toast.LENGTH_LONG).show();
        // running the model
        outputTensor1 = module1.forward(IValue.from(inputTensor)).toTensor();
        outputTensor2 = module3.forward(IValue.from(outputTensor1)).toTensor();


        scores = outputTensor2.getDataAsFloatArray();
        Log.d("1", "cnn: " + scores[1] + "   " + scores[0]);

        index1 = 0;
        if (scores[1] > scores[0]) {
            index1 = 1;
        }

        outputTensor1 = module2.forward(IValue.from(outputTensor1)).toTensor();

        scores = outputTensor1.getDataAsFloatArray();
        Log.d("2", "cnn: " + scores[1] + "   " + scores[0]);
        index2 = 0;
        if (scores[1] > scores[0]) {
            if (index1 == 1) {
                index2 = 1;
            } else {
                index2 = 2;
            }

        } else {
            if (index1 == 1) {
                index2 = 0;
            } else {
                index2 = 3;
            }
        }
        //Log.d("index", "cnn: "+index2+"  " + index1);
        if(mutex == false)mutex =true;
        mutex2 = true;

//        MainActivity.instance.runOnUiThread(new Runnable() {
//            public void run() {
//                //MainActivity.instance.emo_Change(py.cnn(Ecg.get(emo_switch)));
//                MainActivity.instance.emo_Change(index2);
//            }
//        });

        return index2;
    }
}
