package com.example.ecg_vr.myTools;

import java.util.ArrayList;
import java.util.Collections;

public class Medfilt {
    private static int filterSize = 37;//16 0-15 17-33 big?
    private static ArrayList<Double> list = new ArrayList<>();


    public Medfilt(){
        for(int i=0;i<filterSize;i++){
            list.add((double) 0);
        }
    }

    public void push(double e){
        list.remove(0);
        list.add(e);
    }

    public Double Medfilt(){
        ArrayList<Double> B=new ArrayList<Double>();
        B.addAll(list);

        Collections.sort(B);

        return B.get((int)filterSize/2);
    }
}
