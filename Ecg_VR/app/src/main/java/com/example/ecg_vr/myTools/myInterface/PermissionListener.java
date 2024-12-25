package com.example.ecg_vr.myTools.myInterface;

import java.util.List;
/**
 * 权限申请结果监听者
 * 1、权限都授予了
 * 2、被拒绝的权限
 */
public interface PermissionListener {
    void onGranted();  //权限都授予了
    void onDenied(List<String> deniedPermissions);  //被拒绝的权限
}
