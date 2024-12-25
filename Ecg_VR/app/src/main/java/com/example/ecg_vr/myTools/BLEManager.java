package com.example.ecg_vr.myTools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.ecg_vr.BtActivity;
import com.example.ecg_vr.MainActivity;
import com.example.ecg_vr.myTools.myInterface.OnBleConnectListener;
import com.example.ecg_vr.myTools.myInterface.OnDeviceSearchListener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

public class BLEManager {
    private static final String TAG = "BLEManager";

    private static final long MAX_CONNECT_TIME = 10000;  //连接超时时间10s

    private byte[] oneInput = new byte[]{-86, -86, -15, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int oneInputIndex = 0;
    private double[] data = new double[125];
    private int dataIndex = 0;
    private Medfilt filter = new Medfilt();

    private Context mContext;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetooth4Adapter;
    private BluetoothGatt mBluetoothGatt;  //当前连接的gatt
    private String serviceUUID,readUUID,writeUUID;
    private BluetoothGattService bluetoothGattService;   //服务
    private BluetoothGattCharacteristic readCharacteristic;  //读特征
    private BluetoothGattCharacteristic writeCharacteristic; //写特征
    private OnDeviceSearchListener onDeviceSearchListener;  //设备扫描结果监听
    private OnBleConnectListener onBleConnectListener;   //连接监听
    private BluetoothDevice curConnDevice;  //当前连接的设备
    private boolean isConnectIng = false;  //是否正在连接中
    private BluetoothLeScanner mBleScanner;//更新的扫描方式

    private Handler mHandler = new Handler();

    public BLEManager(){

    }

    public boolean initBle(Context context){
        mContext = context;
        if(!checkBle(context)){
            return false;
        }else{
            return true;
        }
    }

    ////////////////////////////////////  扫描设备  ///////////////////////////////////////////////

    public Boolean findDevice_Discovery(){
        if(bluetooth4Adapter == null){
            Log.e(TAG,"startDiscoveryDevice-->bluetooth4Adapter == null");
            return false;
        }
        if(bluetooth4Adapter.isDiscovering()){
            bluetooth4Adapter.cancelDiscovery();
            return false;
        }else {
            return bluetooth4Adapter.startDiscovery();
        }
    }

    //扫描设备回调
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
            //在onLeScan()回调中尽量做少的操作，可以将扫描到的设备扔到另一个线程中处理
            if(bluetoothDevice == null)
                return;

            if(bluetoothDevice.getName() != null){
                //Log.d(TAG,bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
            }else{
                //Log.d(TAG,"null" + "-->" + bluetoothDevice.getAddress());
            }
            BLEDevice bleDevice = new BLEDevice(bluetoothDevice,rssi);
            if(onDeviceSearchListener != null){
                onDeviceSearchListener.onDeviceFound(bleDevice);  //扫描到设备回调
            }
        }
    };

    /**
     * 设置时间段 扫描设备
     * @param onDeviceSearchListener  设备扫描监听
     * @param scanTime  扫描时间
     */
    public void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener,long scanTime){
        if(bluetooth4Adapter == null){
            Log.e(TAG,"startDiscoveryDevice-->bluetooth4Adapter == null");
            return;
        }

        this.onDeviceSearchListener = onDeviceSearchListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG,"开始扫描设备");
            bluetooth4Adapter.startLeScan(leScanCallback);
            //mBleScanner.startScan(leScanCallback);//新方式，但麻烦

        }else{
            return;
        }

        //设定最长扫描时间
        mHandler.postDelayed(stopScanRunnable,scanTime);
    }

    private Runnable stopScanRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            if(onDeviceSearchListener != null){
                onDeviceSearchListener.onDiscoveryOutTime();  //扫描超时回调
            }
            //scanTime之后还没有扫描到设备，就停止扫描。
            stopDiscoveryDevice();
        }
    };

    //////////////////////////////////////  停止扫描  /////////////////////////////////////////////
    /**
     * 停止扫描
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopDiscoveryDevice(){
        mHandler.removeCallbacks(stopScanRunnable);

        if(bluetooth4Adapter == null){
            Log.e(TAG,"stopDiscoveryDevice-->bluetooth4Adapter == null");
            return;
        }

        if(leScanCallback == null){
            Log.e(TAG,"stopDiscoveryDevice-->leScanCallback == null");
            return;
        }

        Log.d(TAG,"停止扫描设备");
        bluetooth4Adapter.stopLeScan(leScanCallback);
    }


    /////////////////////////////////////  执行连接  //////////////////////////////////////////////
    //连接/通讯结果回调
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        //连接状态回调-连接成功/断开连接
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG,"status:" + status);
            Log.d(TAG,"newState:" + newState);

            switch(status){
                case BluetoothGatt.GATT_SUCCESS:
                    Log.w(TAG,"BluetoothGatt.GATT_SUCCESS");
                    break;
                case BluetoothGatt.GATT_FAILURE:
                    Log.w(TAG,"BluetoothGatt.GATT_FAILURE");
                    break;
                case BluetoothGatt.GATT_CONNECTION_CONGESTED:
                    Log.w(TAG,"BluetoothGatt.GATT_CONNECTION_CONGESTED");
                    break;
                case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
                    Log.w(TAG,"BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION");
                    break;
                case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                    Log.w(TAG,"BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION");
                    break;
                case BluetoothGatt.GATT_INVALID_OFFSET:
                    Log.w(TAG,"BluetoothGatt.GATT_INVALID_OFFSET");
                    break;
                case BluetoothGatt.GATT_READ_NOT_PERMITTED:
                    Log.w(TAG,"BluetoothGatt.GATT_READ_NOT_PERMITTED");
                    break;
                case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED:
                    Log.w(TAG,"BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED");
                    break;
            }

            BluetoothDevice bluetoothDevice = gatt.getDevice();
            Log.d(TAG,"连接的设备：" + bluetoothDevice.getName() + "  " + bluetoothDevice.getAddress());

            isConnectIng = false;
            //移除连接超时
            mHandler.removeCallbacks(connectOutTimeRunnable);

            if(newState == BluetoothGatt.STATE_CONNECTED){
                Log.w(TAG,"连接成功");
                //连接成功去发现服务
                gatt.discoverServices();
                //设置发现服务超时时间
                mHandler.postDelayed(serviceDiscoverOutTimeRunnable,MAX_CONNECT_TIME);

                if(onBleConnectListener != null){
                    onBleConnectListener.onConnectSuccess(gatt,bluetoothDevice,status);   //连接成功回调
                }
            }else if(newState == BluetoothGatt.STATE_DISCONNECTED) {
                //清空系统缓存
                //ClsUtils.refreshDeviceCache(gatt);
                try {
                    BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                    Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                    if (localMethod != null) {
                        boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();

                    }
                } catch (Exception localException) {
                    Log.i("Config", "An exception occured while refreshing device");
                }

                Log.e(TAG, "断开连接status:" + status);
                gatt.close();  //断开连接释放连接

                if(status == 133){
                    //无法连接
                    if(onBleConnectListener != null){
                        gatt.close();
                        onBleConnectListener.onConnectFailure(gatt,bluetoothDevice,"连接异常！",status);  //133连接异常 异常断开
                        Log.e(TAG,"连接失败status：" + status + "  " + bluetoothDevice.getAddress());
                    }
                }else if(status == 62){
                    //成功连接没有发现服务断开
                    if(onBleConnectListener != null){
                        gatt.close();
                        onBleConnectListener.onConnectFailure(gatt,bluetoothDevice,"连接成功服务未发现断开！",status); //62没有发现服务 异常断开
                        Log.e(TAG,"连接成功服务未发现断开status:" + status);
                    }

                }else if(status == 0){
                    if(onBleConnectListener != null){
                        onBleConnectListener.onDisConnectSuccess(gatt,bluetoothDevice,status); //0正常断开 回调
                    }
                }else if(status == 8){
                    //因为距离远或者电池无法供电断开连接
                    // 已经成功发现服务
                    if(onBleConnectListener != null){
                        onBleConnectListener.onDisConnectSuccess(gatt,bluetoothDevice,status); //8断电断开  回调
                    }
                }else if(status == 34){
                    if(onBleConnectListener != null){
                        onBleConnectListener.onDisConnectSuccess(gatt,bluetoothDevice,status); //34断开
                    }
                }else {
                    //其它断开连接
                    if(onBleConnectListener != null){
                        onBleConnectListener.onDisConnectSuccess(gatt,bluetoothDevice,status); //其它断开
                    }
                }
            }else if(newState == BluetoothGatt.STATE_CONNECTING){
                Log.d(TAG,"正在连接...");
                if(onBleConnectListener != null){
                    onBleConnectListener.onConnecting(gatt,bluetoothDevice);  //正在连接回调
                }
            }else if(newState == BluetoothGatt.STATE_DISCONNECTING){
                Log.d(TAG,"正在断开...");
                if(onBleConnectListener != null){
                    onBleConnectListener.onDisConnecting(gatt,bluetoothDevice); //正在断开回调
                }
            }
        }

        //发现服务
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //移除发现服务超时
            mHandler.removeCallbacks(serviceDiscoverOutTimeRunnable);
            Log.d(TAG,"移除发现服务超时");

            Log.d(TAG,"发现服务");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Gatt可通信连接成功，可以进入下一步");
            }

            //配置服务信息
            if(setupService(gatt,serviceUUID,readUUID)){//不再使用writecharacteristic
                if(onBleConnectListener != null){
                    onBleConnectListener.onServiceDiscoverySucceed(gatt,gatt.getDevice(),status);  //成功发现服务回调
                }
            }else{
                if(onBleConnectListener != null){
                    onBleConnectListener.onServiceDiscoveryFailed(gatt,gatt.getDevice(),"获取服务特征异常");  //发现服务失败回调
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG,"读status: " + status);
        }

        //向蓝牙设备写入数据结果回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG,"写status: " + status);
            //Logger.getInstance().error(TAG, "onCharacteristicWrite");
        }

        //读取蓝牙设备发出来的数据回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            //接收数据

            Log.w("TAG", "得到数据："+Arrays.toString(characteristic.getValue()));
            //parseSampling(characteristic.getValue());

            if(onBleConnectListener != null){//活动间通信？
                onBleConnectListener.onReceiveMessage(gatt,gatt.getDevice(),characteristic,characteristic.getValue());  //接收数据回调
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "status = " + status + " onDescriptorRead: " + Arrays.toString(descriptor.getValue()));
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.d(TAG,"onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.w(TAG,"读取RSSI值成功，RSSI值：" + rssi + ",status" + status);
                if(onBleConnectListener != null){
                    onBleConnectListener.onReadRssi(gatt,rssi,status);  //成功读取连接的信号强度回调
                }
            }else if(status == BluetoothGatt.GATT_FAILURE){
                Log.w(TAG,"读取RSSI值失败，status：" + status);
            }
        }

        //修改MTU值结果回调
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            ///设置mtu值，即bluetoothGatt.requestMtu()时触发，提示该操作是否成功
            if(status == BluetoothGatt.GATT_SUCCESS){  //设置MTU成功
                //MTU默认取的是23，当收到 onMtuChanged 后，会根据传递的值修改MTU，注意由于传输用掉3字节，因此传递的值需要减3。
                //mtu - 3
                Log.w(TAG,"设置MTU成功，新的MTU值：" + (mtu-3) + ",status" + status);
                if(onBleConnectListener != null){
                    onBleConnectListener.onMTUSetSuccess("设置后新的MTU值 = " + (mtu-3) + "   status = " + status,mtu - 3);  //MTU设置成功
                }

            }else if(status == BluetoothGatt.GATT_FAILURE){  //设置MTU失败
                Log.e(TAG,"设置MTU值失败：" + (mtu-3) + ",status" + status);
                if(onBleConnectListener != null){
                    onBleConnectListener.onMTUSetFailure("设置MTU值失败：" + (mtu-3) + "   status：" + status);  //MTU设置失败
                }
            }

        }
    };

    /**
     * 通过蓝牙设备连接
     * @param context  上下文
     * @param bluetoothDevice  蓝牙设备
     * @param outTime          连接超时时间
     * @param onBleConnectListener  蓝牙连接监听者
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothGatt connectBleDevice(Context context, BluetoothDevice bluetoothDevice, long outTime,String serviceUUID,String readUUID,OnBleConnectListener onBleConnectListener){
        if(bluetoothDevice == null){
            Log.e(TAG,"connectBleDevice()-->bluetoothDevice == null");
            return null;
        }
        if(isConnectIng){
            Log.e(TAG,"connectBleDevice()-->isConnectIng = true");
            return null;
        }
        this.serviceUUID = serviceUUID;
        this.readUUID = readUUID;
        //this.writeUUID = writeUUID;
        this.onBleConnectListener = onBleConnectListener;

        this.curConnDevice = bluetoothDevice;
        Log.d(TAG,"开始准备连接：" + bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
        //出现 BluetoothGatt.android.os.DeadObjectException 蓝牙没有打开
        try{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                mBluetoothGatt = bluetoothDevice.connectGatt(context,false,bluetoothGattCallback,BluetoothDevice.TRANSPORT_LE,BluetoothDevice.PHY_LE_1M_MASK);
            }else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                mBluetoothGatt = bluetoothDevice.connectGatt(context,false,bluetoothGattCallback,BluetoothDevice.TRANSPORT_LE);
            }else {
                mBluetoothGatt = bluetoothDevice.connectGatt(context,false,bluetoothGattCallback);
            }

            mBluetoothGatt.connect();
            isConnectIng = true;

        }catch(Exception e){
            Log.e(TAG,"e:" + e.getMessage());
        }

        //设置连接超时时间10s
        mHandler.postDelayed(connectOutTimeRunnable,outTime);

        return mBluetoothGatt;
    }

    //连接超时
    private Runnable connectOutTimeRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            if(mBluetoothGatt == null){
                Log.e(TAG,"connectOuttimeRunnable-->mBluetoothGatt == null");
                return;
            }

            isConnectIng = false;
            mBluetoothGatt.disconnect();

            //连接超时当作连接失败回调
            if(onBleConnectListener != null){
                onBleConnectListener.onConnectFailure(mBluetoothGatt,curConnDevice,"连接超时！",-1);  //连接失败回调
            }
        }
    };

    //发现服务超时
    private Runnable serviceDiscoverOutTimeRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            if(mBluetoothGatt == null){
                Log.e(TAG,"connectOuttimeRunnable-->mBluetoothGatt == null");
                return;
            }

            isConnectIng = false;
            mBluetoothGatt.disconnect();

            //发现服务超时当作连接失败回调
            if(onBleConnectListener != null){
                onBleConnectListener.onConnectFailure(mBluetoothGatt,curConnDevice,"发现服务超时！",-1);  //连接失败回调
            }
        }
    };

    /**
     * 获取特定服务及特征
     * 1个serviceUUID -- 1个readUUID -- 1个writeUUID
     * @param bluetoothGatt
     * @param serviceUUID
     * @param readUUID
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean setupService(BluetoothGatt bluetoothGatt,String serviceUUID,String readUUID) {
        if (bluetoothGatt == null) {
            Log.e(TAG, "setupService()-->bluetoothGatt == null");
            return false;
        }

        if(serviceUUID == null){
            Log.e(TAG, "setupService()-->serviceUUID == null");
            return false;
        }

        //直接固定取service
        bluetoothGattService = bluetoothGatt.getService(java.util.UUID.fromString(serviceUUID));


        if (bluetoothGattService == null) {
            //找不到该服务就立即断开连接
            Log.e(TAG, "setupService()-->bluetoothGattService == null");
            return false;
        }
        Log.d(TAG, "setupService()-->bluetoothGattService = " + bluetoothGattService.toString());


//        for (BluetoothGattCharacteristic characteristic : bluetoothGattService.getCharacteristics()) {
//            Log.d(TAG, "characteristcs_num:"+bluetoothGattService.getCharacteristics().size());
//            Log.d(TAG, "值:"+characteristic.getUuid().toString());
//            if(bluetoothGattService.getCharacteristics().size()==1){//我手机只有一个特征值，这里就全赋值了
//                readCharacteristic = characteristic;
//                writeCharacteristic = characteristic;
//            }else{
//                if (characteristic.getUuid().toString().equals(readUUID)) {  //读特征
//                    readCharacteristic = characteristic;
//                } else if (characteristic.getUuid().toString().equals(writeUUID)) {  //写特征
//                    writeCharacteristic = characteristic;
//                }
//            }
//        }

        //固定获取readcharacteristic
        readCharacteristic = bluetoothGattService.getCharacteristic(java.util.UUID.fromString(readUUID));
        if (readCharacteristic == null) {
            Log.e(TAG, "setupService()-->readCharacteristic == null");
            return false;
        }
        /*if (writeCharacteristic == null) {
            Log.e(TAG, "setupService()-->writeCharacteristic == null");
            return false;
        }*/
        //打开读通知
        enableNotification(true, bluetoothGatt, readCharacteristic);

        //重点中重点，需要重新设置
        /*List<BluetoothGattDescriptor> descriptors = readCharacteristic.getDescriptors();
        for (BluetoothGattDescriptor descriptor : descriptors) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }?*/
//固定特征characteristic
        BluetoothGattDescriptor descriptor = readCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);

        //延迟2s，保证所有通知都能及时打开
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);

        //然后读一下这个特性,这里特意加上，后面看情况可删去
        mBluetoothGatt.readCharacteristic(readCharacteristic);//会触发回调，这里就到回调中处理

        return true;

    }
/////////////////////////////////////////  打开通知  //////////////////////////////////////////

    /**
     * 设置读特征接收通知
     * @param enable  为true打开通知
     * @param gatt    连接
     * @param characteristic  特征
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void enableNotification(boolean enable, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
        if(gatt == null){
            Log.e(TAG,"enableNotification-->gatt == null");
            return;
        }
        if(characteristic == null){
            Log.e(TAG,"enableNotification-->characteristic == null");
            return;
        }
        //这一步必须要有，否则接收不到通知
        gatt.setCharacteristicNotification(characteristic,enable);
        Log.d(TAG, "开启特征变化通知成功");
    }


    ///////////////////////////////////  发送数据  ///////////////////////////////////////////////

    /**
     * 发送消息  byte[]数组
     * @param msg  消息
     * @return  true  false
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean sendMessage(String msg){
        return  false;
    }


    ///////////////////////////////////  断开连接  ///////////////////////////////////////////////
    /**
     * 断开连接
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disConnectDevice(){
        if(mBluetoothGatt == null){
            Log.e(TAG,"disConnectDevice-->bluetoothGatt == null");
            return;
        }

        //系统断开
        mBluetoothGatt.disconnect();
        //close()方法应该放在断开回调处，放在此处，会没有回调信息
//        mBluetoothGatt.close();
    }



    /**
     * 检测手机是否支持4.0蓝牙
     * @param context  上下文
     * @return true--支持4.0  false--不支持4.0
     */
    private boolean checkBle(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {  //API 18 Android 4.3
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if(bluetoothManager == null){
                return false;
            }
            bluetooth4Adapter = bluetoothManager.getAdapter();  //BLUETOOTH权限
            if(bluetooth4Adapter == null){
                return false;
            }else{


                bluetooth4Adapter = bluetoothManager.getAdapter();
                //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                //如果调用scanBleDevices2()，请加上这句。 Call requires API level 21
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBleScanner = bluetooth4Adapter.getBluetoothLeScanner();

                }
                Log.d(TAG,"该设备支持蓝牙4.0");
                return true;
            }
        }else{
            return false;
        }
    }

    /**
     * 获取蓝牙状态
     */
    public boolean isEnable(){
        if(bluetooth4Adapter == null){
            return false;
        }
        return bluetooth4Adapter.isEnabled();
    }

    /**
     * 打开蓝牙
     * @param isFast  true 直接打开蓝牙  false 提示用户打开
     */
    public void openBluetooth(Context context,boolean isFast){
        if(!isEnable()){
            if(isFast){
                Log.d(TAG,"直接打开手机蓝牙");
                bluetooth4Adapter.enable();  //BLUETOOTH_ADMIN权限
            }else{
                Log.d(TAG,"提示用户去打开手机蓝牙");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(enableBtIntent);
            }
        }else{
            Log.d(TAG,"手机蓝牙状态已开");
        }
    }

    /**
     * 直接关闭蓝牙
     */
    public void closeBluetooth(){
        if(bluetooth4Adapter == null)
            return;

        bluetooth4Adapter.disable();
    }


    /**
     * 本地蓝牙是否处于正在扫描状态
     * @return true false
     */
    public boolean isDiscovery(){
        if(bluetooth4Adapter ==null){
            return false;
        }
        return bluetooth4Adapter.isDiscovering();
    }

    //读数据转化
    private void parseSampling(byte[] input) {
        Log.d(TAG, "parseSampling: dataingggggggggggggggggggggg!");
        float[] ecgFilter= new float[5];
        float midEcg = 0;
        for (int i = 0; i < input.length; i++) {//byte to double

            if (oneInputIndex < 4) {//对齐头标签
                if (input[i] != oneInput[oneInputIndex]) {
                    oneInputIndex = 0;
                } else {
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
                    data[dataIndex] = (float) ecgData;
                    filter.push((float) ecgData);
                    Double baseline=filter.Medfilt();
                    ecgFilter[j%5] = (float) (baseline-(double) ecgData);

                    //将0.008扩大为0.04，取最值
                    if(Math.abs(midEcg)<Math.abs(ecgFilter[j])){
                        midEcg = ecgFilter[j];
                    }
                    if(j%5==0){
                        //WaveUtil waveUtil = (WaveUtil) mContext.getApplicationContext();
                        MainActivity.instance.Main_show1data(midEcg);
                        //waveUtil.showWave1data(waveShowView,midEcg);
                    }

                    dataIndex++;
                }

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
                    Log.e(TAG, "processed data: " + Arrays.toString(data));
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
}
