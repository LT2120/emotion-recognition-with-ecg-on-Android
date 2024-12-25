package com.example.ecg_vr.myTools.myInterface;

import com.example.ecg_vr.myTools.BLEDevice;

public interface OnDeviceSearchListener {
    void onDeviceFound(BLEDevice bleDevice);  //搜索到设备
    void onDiscoveryOutTime(); //扫描超时
}
