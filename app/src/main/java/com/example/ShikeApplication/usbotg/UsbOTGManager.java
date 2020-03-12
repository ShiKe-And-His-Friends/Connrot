package com.example.ShikeApplication.usbotg;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;

public class UsbOTGManager {
    private Activity mContext;
    private final String TAG = "UsbOTGManager";
    private final String ACTION_USB_PERMISSION = "com.idcmeeting.usbotgmanger.USB_PERMISSION";
    private UsbBroadcast mUsbReceiver;

    public void init (Activity context) {
        if (context != null) {
            mContext = context;
        }
        if (mUsbReceiver == null && mContext != null) {
            mUsbReceiver = new UsbBroadcast();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            intentFilter.addAction(ACTION_USB_PERMISSION);
            mContext.registerReceiver(mUsbReceiver, intentFilter);
        }
    }

    private class UsbBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent);
            String action = intent.getAction();
            if (action == null)
                return;
            switch (action) {
                case ACTION_USB_PERMISSION:
                    synchronized (this) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) { //允许权限申请
                            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            OpenDevice(device);
                        } else {
                            //("用户未授权，访问USB设备失败");
                        }
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED://USB设备插入广播
                    //("USB设备插入");
                    applayPermission();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED://USB设备拔出广播
                    //("USB设备拔出");
                    break;
            }
        }
    };

    public void applayPermission () {
        Log.e(TAG, "applayPermission.");
        UsbManager mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        if (mUsbManager == null || mUsbManager.getDeviceList() == null) {
            Log.e(TAG, "UsbManager getDeviceList is null.");
            return;
        }
        for (UsbDevice devices : mUsbManager.getDeviceList().values()) {
            if (devices.getProductName() != null && devices.getProductName().contains("Camera")) {
                Log.d(TAG ,"usb camera devices.");
                continue;
            }
            if (mUsbManager.hasPermission(devices)) {
                OpenDevice(devices);
            } else {
                mUsbManager.requestPermission(devices , mPermissionIntent);
            }
        }
    }

    public void OpenDevice(UsbDevice device) {
        Log.e(TAG, "OpenDeviceSuccess");
        FileSystem currentFs = null;
        try {
            Log.e(TAG, "device name: " + device.getDeviceName() );
            Log.e(TAG, "device: product" + device.getProductName() );
            Log.e(TAG, "device: manufacture" + device.getManufacturerName() );
         /*   currentFs = device.;
            Log.e(TAG, "容量: " + currentFs.getCapacity());
            Log.e(TAG, "已使用空间: " + currentFs.getOccupiedSpace());
            Log.e(TAG, "剩余空间: " + currentFs.getFreeSpace());
            Log.e(TAG, "block数目: " + currentFs.getChunkSize());*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "exception : " + e.getMessage());
        }
        UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(mContext);
        UsbMassStorageDevice mDeviceMassStorage = null;
        boolean findDeviceFileSystem = false;
        Log.d(TAG ,"storageDevices length = " + storageDevices.length);
        for (UsbMassStorageDevice deviceMassStorage : storageDevices) {
            if (device.equals(deviceMassStorage.getUsbDevice())) {
                findDeviceFileSystem = true;
                mDeviceMassStorage = deviceMassStorage;
                break;
            }
        }
        if (findDeviceFileSystem) {
            openDevicesMassStroage(mDeviceMassStorage);
        }
        Log.d(TAG ,"findDeviceFileSystem = " + findDeviceFileSystem);
    }

    //获取到OTG连接的U盘
    public FileSystem openDevicesMassStroage(UsbMassStorageDevice deviceMassStroage) {
        Log.e(TAG, "openDevicesMassStroage.");
        FileSystem currentFs = null;
        try {
            Log.e(TAG, "device name: " + deviceMassStroage.getUsbDevice().getDeviceName() );
            Log.e(TAG, "device: product" + deviceMassStroage.getUsbDevice().getProductName() );
            Log.e(TAG, "device: manufacture" + deviceMassStroage.getUsbDevice().getManufacturerName() );
            deviceMassStroage.init();
            //如果设备不支持一些格式的U盘，这里会有异常
            if (deviceMassStroage.getPartitions() == null ||
                    deviceMassStroage.getPartitions().get(0) == null ||
                    deviceMassStroage.getPartitions().get(0).getFileSystem() == null) {
                return null;
            }
            currentFs = deviceMassStroage.getPartitions().get(0).getFileSystem();
            Log.e(TAG, "容量: " + currentFs.getCapacity());
            Log.e(TAG, "已使用空间: " + currentFs.getOccupiedSpace());
            Log.e(TAG, "剩余空间: " + currentFs.getFreeSpace());
            Log.e(TAG, "block数目: " + currentFs.getChunkSize());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "exception : " + e.getMessage());
            return null;
        }
        return currentFs;
    }

    public void destory () {
        if (mUsbReceiver != null && mContext != null) {
            mContext.unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
        mContext = null;
    }
}
