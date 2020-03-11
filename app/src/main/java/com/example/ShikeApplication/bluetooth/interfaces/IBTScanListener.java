package com.example.ShikeApplication.bluetooth.interfaces;


import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * created by shike , 20200310
 */

public interface IBTScanListener {

    /**
     * 搜索开始
     */
    void onScanStart();

    /**
     * 搜索结束
     * @param deviceList
     */
    void onScanStop(List<BluetoothDevice> deviceList);

    /**
     * 发现新设备
     * @param device
     */
    void onFindDevice(BluetoothDevice device);
}
