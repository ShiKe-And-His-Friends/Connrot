package com.example.ShikeApplication.bluetooth.interfaces;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * created by shike , 20200310
 */

public interface IBTConnectListener {
    void onConnecting(BluetoothDevice bluetoothDevice);//连接中
    void onConnected(BluetoothDevice bluetoothDevice);//连接成功
    void onDisConnecting(BluetoothDevice bluetoothDevice);//断开中
    void onDisConnect(BluetoothDevice bluetoothDevice);//断开
    void onConnectedDevice(List<BluetoothDevice> devices);//已连接的设备
}
