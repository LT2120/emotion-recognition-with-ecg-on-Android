package com.example.ecg_vr.myTools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.UUID;

public class BleDriver{

    private static final String TAG = "BleDriver";

    // for preprocessing

    double Fs = 125;    // 采样频率
    //SignalFilters signalFilters = new SignalFilters();
    private Medfilt filter = new Medfilt();

    private String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805F9B34FB";
    private String UUID_CHARACTERISTIC = "0000ffe1-0000-1000-8000-00805F9B34FB";
    private String UUID_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    private BluetoothGatt bluetoothGatt;
    private Context context;

    private WaveShowView waveShowView;
    private WaveUtil waveUtil;

    private boolean run;
    private int retryCount;
    private static int GATT_RETRY_MAX_COUNT = 3;
    private int sampleRate = 125;
    private int segLength = 1;

    private boolean flag = false;

    private byte[] oneInput = new byte[]{-86, -86, -15, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int oneInputIndex = 0;
    private double[] data = new double[125];
    private double hrPre = 0;
    private double rrPre = 0;
    private int dataIndex = 0;

    public BleDriver(Context context, Intent intent,WaveShowView waveShowView,WaveUtil waveUtil) {

        this.waveUtil = waveUtil;
        this.waveShowView = waveShowView;
        this.context = context;
        retryCount = 0;
        adapter = BluetoothAdapter.getDefaultAdapter();

        try {
            //this.device = (BluetoothDevice) info.getDeviceObj();
            this.device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        }catch (ClassCastException e){
            Log.e(TAG, "BleDriver: " + e);
        }
    }



    protected boolean connect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        bluetoothGatt = device.connectGatt(context, false, mGattCallback);

        return true;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
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

            if (BluetoothGatt.GATT_SUCCESS == status) {
                run = true;
                //Logger.getInstance().error(TAG, "onConnectionStateChange");
                Log.d(TAG,"status:" + status);
                Log.d(TAG,"newState:" + newState);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (run) {
                            bluetoothGatt.discoverServices();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();
            } else {
                bluetoothGatt.close();
                Log.e(TAG, "code = " + status + " , retry.");
                //Logger.getInstance().error(TAG, "code = " + status + " , retry.");
                bluetoothGatt = device.connectGatt(context, false, mGattCallback);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            run = false;
            //Logger.getInstance().error(TAG, "onServicesDiscovered");
            BluetoothGattService service = bluetoothGatt.getService(java.util.UUID.fromString(UUID_SERVICE));
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(java.util.UUID.fromString(UUID_CHARACTERISTIC));

            //Logger.getInstance().error(TAG,"setCharacteristicNotification: " + bluetoothGatt.setCharacteristicNotification(characteristic, true));
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(UUID_DESCRIPTOR));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
//            Thread readThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (flag) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Logger.getInstance().error(TAG,"readCharacteristic: " + bluetoothGatt.readCharacteristic(characteristic));
//                    }
//                }
//            });
//            readThread.start();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG,"读status: " + status);
            //Logger.getInstance().error(TAG, "status = " + status + " onCharacteristicRead: " + Arrays.toString(characteristic.getValue()));
        }

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
            Log.d(TAG,Arrays.toString(characteristic.getValue()));
            //Logger.getInstance().error(TAG, Arrays.toString(characteristic.getValue()));
            parseSampling(characteristic.getValue());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            //Logger.getInstance().error(TAG, "status = " + status + " onDescriptorRead: " + Arrays.toString(descriptor.getValue()));
            Log.d(TAG, "status = " + status + " onDescriptorRead: " + Arrays.toString(descriptor.getValue()));
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED)
                Log.e(TAG, "GATT_WRITE_NOT_PERMITTED" + " getPermissions: " + descriptor.getPermissions());
                //Logger.getInstance().error(TAG, "GATT_WRITE_NOT_PERMITTED" + " getPermissions: " + descriptor.getPermissions());
            //Logger.getInstance().error(TAG, "status = " + status + " onDescriptorWrite: " + Arrays.toString(descriptor.getValue()));
            Log.e(TAG, "status = " + status + " onDescriptorWrite: " + Arrays.toString(descriptor.getValue()));
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.d(TAG,"onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

        }

        //修改MTU值结果回调
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }


        /*public void onServiceChanged(@NonNull BluetoothGatt gatt) {
            super.onServiceChanged(gatt);
        }*/
    };


    protected boolean enableSampling() {
        Log.e(TAG, "enableSampling: " );

        flag = true;
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//
//                while (flag) {
//
//                    float[] data = new float[125];
//                    for(int i = 0 ; i <= 124;i++){
//                        data[i] = (i+1)%25;
//                    }
//
//                    post(getDeviceInfo().getOutputs().get(0).getKey(),data) ;
//                    try {
//                        Thread.sleep(1000);
//                    }catch (Exception exception){
//
//                    }
//                }
//            }
//        };
//        thread.start();

        return true;
    }


    protected boolean disableSampling() {
        flag = false;
        return true;
    }


    protected boolean disconnect() {

        return false;
    }

    private String convert10to16(byte[] values) {
        StringBuilder result = new StringBuilder();
        for (byte value : values) {
            String i = Integer.toHexString(value);
            result.append(i).append(" ");
        }
        result.deleteCharAt(result.length()-1);

        return result.toString();
    }

    private void parseSampling(byte[] input) {
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
                    double baseline=filter.Medfilt();
                    ecgFilter[j%5] = (float) (baseline-(double) ecgData);

                    //将0.008扩大为0.04，取最值
                    if(Math.abs(midEcg)<Math.abs(ecgFilter[j])){
                        midEcg = ecgFilter[j];
                    }
                    if(j%5==0){
                        waveUtil.showWave1data(waveShowView,midEcg);
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
